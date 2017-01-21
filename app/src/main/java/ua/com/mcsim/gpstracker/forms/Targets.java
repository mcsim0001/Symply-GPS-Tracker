package ua.com.mcsim.gpstracker.forms;

/**
 * Created by mcsim on 19.01.2017.
 */

public class Targets {
    public Targets(String phone, String status) {
        this.phone = phone;
        this.status = status;
    }

    public Targets() {

    }

    private String name;
    private String phone;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
