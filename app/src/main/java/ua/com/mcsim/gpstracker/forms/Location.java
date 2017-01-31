package ua.com.mcsim.gpstracker.forms;


public class Location {

    private String coordLat;
    private String coordLong;
    private String coordTime;


    public String getCoordLat() {
        return coordLat;
    }

    public String getCoordLong() {
        return coordLong;
    }

    public String getCoordTime() {
        return coordTime;
    }

    public Location() {
        // Default constructor required for calls to DataSnapshot.getValue(Location.class)
    }

    public Location(String coordLat, String coordLong, String coordTime) {

        this.coordLat = coordLat;
        this.coordLong = coordLong;
        this.coordTime = coordTime;
    }
}
