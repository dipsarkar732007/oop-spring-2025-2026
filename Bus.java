package entity;

public class Bus {
    private String id;
    private String routeName;
    private String totalSeats;
    private String farePerSeat;

    public Bus(String id, String routeName, String totalSeats, String farePerSeat) {
        this.id = id;
        this.routeName = routeName;
        this.totalSeats = totalSeats;
        this.farePerSeat = farePerSeat;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(String totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getFarePerSeat() {
        return farePerSeat;
    }

    public void setFarePerSeat(String farePerSeat) {
        this.farePerSeat = farePerSeat;
    }
}