package ua.com.mcsim.gpstracker.forms;



public class TrackingPermission {

    private String from;
    private String to;
    private String status;
    private String date;

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public TrackingPermission(String from, String to, int status) {
        this.from = from;
        this.to = to;
        this.status = String.valueOf(status);
        this.date = String.valueOf(System.currentTimeMillis());

    }

    public TrackingPermission() {
    }
}
