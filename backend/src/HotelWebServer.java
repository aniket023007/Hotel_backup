import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * HotelWebServer
 * REST API and Static File Server for Hotel Management System.
 * Built with native Java 21 HttpServer.
 * Adheres strictly to OOP Principles: Separation of Concerns, Service Layer Delegation,
 * and Domain-Driven Request Handling.
 */
public class HotelWebServer {

    private static final int DEFAULT_PORT = 8080;

    // Service Layer Abstractions (Dependency Inversion)
    private static final IHotelService hotelService = new HotelService();
    private static final IAuthService authService = AuthService.getInstance();

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // Authentication Endpoints (IAuthService)
            server.createContext("/api/auth/login", new AuthLoginHandler());
            server.createContext("/api/auth/current", new AuthCurrentHandler());
            server.createContext("/api/auth/logout", new AuthLogoutHandler());
            server.createContext("/api/auth/users", new AuthUsersHandler());

            // REST API Endpoints
            server.createContext("/api/health", new HealthHandler());
            server.createContext("/api/stats", new StatsHandler());
            server.createContext("/api/rooms", new RoomsHandler());
            server.createContext("/api/guests", new GuestsHandler());
            server.createContext("/api/bookings/history", new BookingHistoryHandler());
            server.createContext("/api/bookings/next-id", new NextBookingIdHandler());
            server.createContext("/api/bookings", new BookingsHandler());
            server.createContext("/api/bill", new BillHandler());
            server.createContext("/api/checkout", new CheckoutHandler());

            // Static Web UI Handler
            server.createContext("/", new StaticFileHandler());

            server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
            server.start();

            System.out.println("==================================================");
            System.out.println("   GRAND HORIZON - HOTEL MANAGEMENT WEB SERVER    ");
            System.out.println("==================================================");
            System.out.println("  Server running at : http://localhost:" + port);
            System.out.println("  Web Dashboard UI  : http://localhost:" + port + "/index.html");
            System.out.println("  Architecture      : Pure Java OOP (Service/DAO/Model)");
            System.out.println("  Database Backend  : MySQL (hotel_management)");
            System.out.println("==================================================");

        } catch (IOException e) {
            System.err.println("Failed to start Hotel Web Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================================
    // API HANDLERS (Delegating to IHotelService)
    // =========================================================================

    // /api/health
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            Map<String, Object> res = new LinkedHashMap<>();
            try (Connection conn = DatabaseConnection.getConnection()) {
                res.put("status", "healthy");
                res.put("database", "connected");
                res.put("catalog", conn.getCatalog());
                sendJsonResponse(exchange, 200, toJson(res));
            } catch (SQLException e) {
                res.put("status", "degraded");
                res.put("database", "disconnected");
                res.put("error", e.getMessage());
                sendJsonResponse(exchange, 500, toJson(res));
            }
        }
    }

    // /api/stats
    static class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            try {
                HotelStatistics stats = hotelService.getHotelStatistics();
                sendJsonResponse(exchange, 200, toJson(stats.toMap()));
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    // /api/rooms
    static class RoomsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            String method = exchange.getRequestMethod();
            try {
                if ("GET".equalsIgnoreCase(method)) {
                    List<Room> rooms = hotelService.getAllRooms();
                    List<Map<String, Object>> res = new ArrayList<>();
                    for (Room r : rooms) {
                        res.add(r.toMap());
                    }
                    sendJsonResponse(exchange, 200, toJson(res));
                } else if ("POST".equalsIgnoreCase(method)) {
                    String body = readRequestBody(exchange);
                    Map<String, String> data = parseJsonSimple(body);

                    String roomNumber = data.get("roomNumber");
                    String roomType = data.get("roomType");
                    String priceStr = data.get("pricePerNight");

                    if (roomNumber == null || roomNumber.trim().isEmpty() ||
                        roomType == null || roomType.trim().isEmpty() ||
                        priceStr == null) {
                        sendErrorResponse(exchange, 400, "Missing required room parameters.");
                        return;
                    }

                    double price = Double.parseDouble(priceStr);
                    Room room = RoomFactory.createRoom(roomNumber.trim(), roomType.trim(), price);
                    hotelService.addRoom(room);

                    Map<String, Object> res = new LinkedHashMap<>();
                    res.put("success", true);
                    res.put("message", "Room #" + roomNumber + " added successfully.");
                    sendJsonResponse(exchange, 201, toJson(res));
                } else {
                    sendErrorResponse(exchange, 405, "Method not allowed.");
                }
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    // /api/guests
    static class GuestsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            String method = exchange.getRequestMethod();
            try {
                if ("GET".equalsIgnoreCase(method)) {
                    List<Guest> guests = hotelService.getAllGuests();
                    List<Map<String, Object>> res = new ArrayList<>();
                    for (Guest g : guests) {
                        res.add(g.toMap());
                    }
                    sendJsonResponse(exchange, 200, toJson(res));
                } else if ("POST".equalsIgnoreCase(method)) {
                    String body = readRequestBody(exchange);
                    Map<String, String> data = parseJsonSimple(body);

                    String fullName = data.get("fullName");
                    String phone = data.getOrDefault("phone", "");
                    String email = data.getOrDefault("email", "");
                    String address = data.getOrDefault("address", "");

                    if (fullName == null || fullName.trim().isEmpty()) {
                        sendErrorResponse(exchange, 400, "Full Name is required.");
                        return;
                    }

                    Guest guest = new Guest(fullName.trim(), phone.trim(), email.trim(), address.trim());
                    int guestId = hotelService.registerGuest(guest);

                    Map<String, Object> res = new LinkedHashMap<>();
                    res.put("success", true);
                    res.put("guestId", guestId);
                    res.put("message", "Guest registered successfully.");
                    sendJsonResponse(exchange, 201, toJson(res));
                } else {
                    sendErrorResponse(exchange, 405, "Method not allowed.");
                }
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    // /api/bookings
    static class BookingsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            String method = exchange.getRequestMethod();
            try {
                if ("GET".equalsIgnoreCase(method)) {
                    List<Booking> bookings = hotelService.getActiveBookings();
                    List<Map<String, Object>> res = new ArrayList<>();
                    for (Booking b : bookings) {
                        res.add(b.toMap());
                    }
                    sendJsonResponse(exchange, 200, toJson(res));
                } else if ("POST".equalsIgnoreCase(method)) {
                    String body = readRequestBody(exchange);
                    Map<String, String> data = parseJsonSimple(body);

                    int bookingId = 0;
                    if (data.containsKey("bookingId") && !data.get("bookingId").trim().isEmpty()) {
                        bookingId = Integer.parseInt(data.get("bookingId").trim());
                    } else {
                        bookingId = hotelService.getNextBookingId();
                    }

                    int guestId = Integer.parseInt(data.get("guestId").trim());
                    int roomId = Integer.parseInt(data.get("roomId").trim());
                    int numberOfNights = Integer.parseInt(data.get("numberOfNights").trim());

                    Booking booking = hotelService.bookRoom(bookingId, guestId, roomId, numberOfNights);

                    Map<String, Object> res = new LinkedHashMap<>();
                    res.put("success", true);
                    res.put("bookingId", booking.getBookingId());
                    res.put("message", "Room booked successfully.");
                    sendJsonResponse(exchange, 201, toJson(res));
                } else {
                    sendErrorResponse(exchange, 405, "Method not allowed.");
                }
            } catch (Exception e) {
                sendErrorResponse(exchange, 400, e.getMessage());
            }
        }
    }

    // /api/bookings/history
    static class BookingHistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            try {
                List<Booking> bookings = hotelService.getAllBookingsHistory();
                List<Map<String, Object>> res = new ArrayList<>();
                for (Booking b : bookings) {
                    res.add(b.toMap());
                }
                sendJsonResponse(exchange, 200, toJson(res));
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    // /api/bookings/next-id
    static class NextBookingIdHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            try {
                int nextId = hotelService.getNextBookingId();
                Map<String, Object> res = new LinkedHashMap<>();
                res.put("nextBookingId", nextId);
                sendJsonResponse(exchange, 200, toJson(res));
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    // /api/bill?bookingId=...
    static class BillHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            try {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQueryParams(query);
                String idStr = params.get("bookingId");
                if (idStr == null || idStr.trim().isEmpty()) {
                    sendErrorResponse(exchange, 400, "Query parameter 'bookingId' is required.");
                    return;
                }

                int bookingId = Integer.parseInt(idStr.trim());
                Bill bill = hotelService.calculateBill(bookingId);
                sendJsonResponse(exchange, 200, toJson(bill.toMap()));
            } catch (Exception e) {
                sendErrorResponse(exchange, 400, e.getMessage());
            }
        }
    }

    // /api/checkout
    static class CheckoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendErrorResponse(exchange, 405, "Method not allowed. Use POST.");
                return;
            }

            try {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJsonSimple(body);
                String idStr = data.get("bookingId");
                if (idStr == null || idStr.trim().isEmpty()) {
                    sendErrorResponse(exchange, 400, "Field 'bookingId' is required in request body.");
                    return;
                }

                int bookingId = Integer.parseInt(idStr.trim());
                Bill finalizedBill = hotelService.processCheckout(bookingId);

                Map<String, Object> res = new LinkedHashMap<>();
                res.put("success", true);
                res.put("message", "Checkout successful. Room marked as Available.");
                res.put("invoice", finalizedBill.toMap());

                sendJsonResponse(exchange, 200, toJson(res));
            } catch (Exception e) {
                sendErrorResponse(exchange, 400, e.getMessage());
            }
        }
    }

    // =========================================================================
    // AUTHENTICATION HANDLERS (Delegating to IAuthService)
    // =========================================================================

    // /api/auth/login (POST)
    static class AuthLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendErrorResponse(exchange, 405, "Method not allowed. Use POST.");
                return;
            }

            try {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJsonSimple(body);

                String username = data.get("username");
                String password = data.get("password");

                if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                    sendErrorResponse(exchange, 400, "Username and password are required.");
                    return;
                }

                // Polymorphic authentication via encapsulated value object
                LoginCredentials credentials = new LoginCredentials(username, password);
                User user = authService.login(credentials);

                Map<String, Object> res = new LinkedHashMap<>();
                res.put("success", true);
                res.put("message", "Authentication successful.");
                res.put("user", user.toMap());

                sendJsonResponse(exchange, 200, toJson(res));
            } catch (AuthenticationException e) {
                sendErrorResponse(exchange, 401, e.getMessage());
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, "Server error: " + e.getMessage());
            }
        }
    }

    // /api/auth/current (GET)
    static class AuthCurrentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            Map<String, Object> res = new LinkedHashMap<>();
            if (authService.isAuthenticated()) {
                res.put("authenticated", true);
                res.put("user", authService.getCurrentUser().toMap());
            } else {
                res.put("authenticated", false);
                res.put("user", null);
            }
            sendJsonResponse(exchange, 200, toJson(res));
        }
    }

    // /api/auth/logout (POST)
    static class AuthLogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            authService.logout();

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("success", true);
            res.put("message", "User signed out successfully.");
            sendJsonResponse(exchange, 200, toJson(res));
        }
    }

    // /api/auth/users (GET) - Demo endpoint for interviewer inspection of MySQL users
    static class AuthUsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            try {
                List<User> users = authService.listAllUsers();
                List<Map<String, Object>> list = new ArrayList<>();
                for (User u : users) {
                    list.add(u.toMap());
                }
                Map<String, Object> res = new LinkedHashMap<>();
                res.put("success", true);
                res.put("users", list);
                sendJsonResponse(exchange, 200, toJson(res));
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    // =========================================================================
    // STATIC FILE HANDLER
    // =========================================================================

    static class StaticFileHandler implements HttpHandler {
        private final Path webRoot = resolveWebRoot();

        private static Path resolveWebRoot() {
            Path[] candidates = new Path[] {
                Paths.get("frontend"),
                Paths.get("../frontend"),
                Paths.get("web"),
                Paths.get("../web")
            };
            for (Path p : candidates) {
                if (Files.exists(p) && Files.isDirectory(p)) {
                    return p.toAbsolutePath().normalize();
                }
            }
            return Paths.get("frontend").toAbsolutePath().normalize();
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;

            String pathStr = exchange.getRequestURI().getPath();
            if (pathStr.equals("/") || pathStr.isEmpty() || pathStr.equals("/login")) {
                pathStr = "/login.html";
            }

            Path filePath = webRoot.resolve(pathStr.substring(1)).normalize();
            if (!filePath.startsWith(webRoot) || !Files.exists(filePath) || Files.isDirectory(filePath)) {
                Path fallback = webRoot.resolve("login.html");
                if (Files.exists(fallback)) {
                    serveFile(exchange, fallback, "text/html; charset=UTF-8");
                    return;
                }
                String notFound = "<h1>404 Not Found</h1><p>Resource not found: " + pathStr + "</p>";
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, notFound.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            String contentType = getContentType(filePath.getFileName().toString());
            serveFile(exchange, filePath, contentType);
        }

        private void serveFile(HttpExchange exchange, Path path, String contentType) throws IOException {
            byte[] bytes = Files.readAllBytes(path);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private String getContentType(String filename) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
            if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
            if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
            if (lower.endsWith(".svg")) return "image/svg+xml";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".ico")) return "image/x-icon";
            if (lower.endsWith(".woff2")) return "font/woff2";
            return "text/plain; charset=UTF-8";
        }
    }

    // =========================================================================
    // UTILITY HELPERS
    // =========================================================================

    private static boolean handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("success", false);
        err.put("error", message != null ? message : "Unknown error");
        sendJsonResponse(exchange, statusCode, toJson(err));
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int n;
            while ((n = is.read(buf)) != -1) {
                bos.write(buf, 0, n);
            }
            return bos.toString(StandardCharsets.UTF_8.name());
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                try {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
                    String val = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
                    map.put(key, val);
                } catch (Exception ignored) {}
            }
        }
        return map;
    }

    private static Map<String, String> parseJsonSimple(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null) return map;
        String trimmed = json.trim();
        if (trimmed.startsWith("{")) trimmed = trimmed.substring(1);
        if (trimmed.endsWith("}")) trimmed = trimmed.substring(0, trimmed.length() - 1);

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "\"([^\"]+)\"\\s*:\\s*(?:\"([^\"]*)\"|([\\w\\.\\-]+))"
        );
        java.util.regex.Matcher matcher = pattern.matcher(trimmed);
        while (matcher.find()) {
            String key = matcher.group(1);
            String val = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
            map.put(key, val);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) {
            return "\"" + escapeJson((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(escapeJson(String.valueOf(entry.getKey()))).append("\":");
                sb.append(toJson(entry.getValue()));
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : list) {
                if (!first) sb.append(",");
                first = false;
                sb.append(toJson(item));
            }
            sb.append("]");
            return sb.toString();
        }
        return "\"" + escapeJson(obj.toString()) + "\"";
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}
