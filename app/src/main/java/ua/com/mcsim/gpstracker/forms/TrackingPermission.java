package ua.com.mcsim.gpstracker.forms;



public class TrackingPermission {

    private String from;
    private String to;
    private String status;
    private String password;

    public TrackingPermission(String from, String to, String status) {
        this.from = from;
        this.to = to;
        this.status = status;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
