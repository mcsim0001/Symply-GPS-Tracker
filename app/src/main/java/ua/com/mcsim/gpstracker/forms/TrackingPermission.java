package ua.com.mcsim.gpstracker.forms;



public class TrackingPermission {

    private String from;
    private String to;
    private String status;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getStatus() {
        return status;
    }

    public TrackingPermission(String from, String to, int status) {
        this.from = from;
        this.to = to;
        this.status = String.valueOf(status);

    }

    public TrackingPermission() {
    }
}
