package ua.com.mcsim.gpstracker.forms;


public class GPSmessage {

    private String userMail;
    private String phoneNumber;
    private String coordLat;
    private String coordLong;
    private String coordTime;
    private String comment;

    public String getUserMail() {
        return userMail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCoordLat() {
        return coordLat;
    }

    public String getCoordLong() {
        return coordLong;
    }

    public String getCoordTime() {
        return coordTime;
    }

    public String getComment() {
        return comment;
    }

    public GPSmessage(String userMail, String phoneNumber, String coordLat, String coordLong, String coordTime, String comment) {
        this.userMail = userMail;
        this.phoneNumber = phoneNumber;
        this.coordLat = coordLat;
        this.coordLong = coordLong;
        this.coordTime = coordTime;
        this.comment = comment;
    }
}
