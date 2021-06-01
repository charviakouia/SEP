package models;

import java.io.Serializable;

public class Location implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private String city;

    private String street;

    private String houseNumber;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    @Override
    public Location clone() {
        Location copy;

        try {
            copy = (Location) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new Error("Clone must be supported!");
        }

        return copy;
    }
}
