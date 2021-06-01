package models;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private final Integer id;

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private Date birthday;

    private Location location;

    public User(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public User clone() {
        User copy;

        try {
            copy = (User) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new Error("Clone must be supported!");
        }

        return copy;
    }
}
