package com.manideep.bloodbank.data;

/** A registered donor. */
public class Donor {

    public long id;
    public String name;
    public String phone;
    public String bloodGroup;
    public String city;
    public double latitude;
    public double longitude;
    /** 1 when the donor is willing to be contacted right now. */
    public boolean available;
    public long createdAt;

    public Donor() {
    }

    public Donor(String name, String phone, String bloodGroup, String city,
                 double latitude, double longitude, boolean available) {
        this.name = name;
        this.phone = phone;
        this.bloodGroup = BloodType.normalise(bloodGroup);
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.available = available;
        this.createdAt = System.currentTimeMillis();
    }
}
