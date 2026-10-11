public enum RoomStatus {
    AVAILABLE("Available"),
    BOOKED("Booked"),
    MAINTENANCE("Maintenance");

    private final String displayName;

    RoomStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RoomStatus fromString(String text) {
        if (text == null) return AVAILABLE;
        for (RoomStatus status : RoomStatus.values()) {
            if (status.displayName.equalsIgnoreCase(text.trim()) ||
                status.name().equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        return AVAILABLE;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
