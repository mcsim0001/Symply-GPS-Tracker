package ua.com.mcsim.gpstracker.forms;


public class User {

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }


    private String userName;
    private String phone;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userName, String phone) {
        this.userName = userName;
        this.phone = phone;
    }
}
