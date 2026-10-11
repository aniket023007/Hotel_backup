public enum BookingStatus {
    BOOKED("Booked"),
    CHECKED_OUT("Checked Out"),
    CANCELLED("Cancelled");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BookingStatus fromString(String text) {
        if (text == null) return BOOKED;
        for (BookingStatus status : BookingStatus.values()) {
            if (status.displayName.equalsIgnoreCase(text.trim()) ||
                status.name().equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        return BOOKED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
