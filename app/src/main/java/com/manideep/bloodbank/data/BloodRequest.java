package com.manideep.bloodbank.data;

/** A hospital or blood bank asking for units of a given group. */
public class BloodRequest {

    public static final String STATUS_OPEN = "open";
    public static final String STATUS_FULFILLED = "fulfilled";

    public long id;
    public String bloodGroup;
    public int unitsNeeded;
    public String hospital;
    public String city;
    public double latitude;
    public double longitude;
    public String contactPhone;
    public String status;
    public long createdAt;

    public BloodRequest() {
    }

    public BloodRequest(String bloodGroup, int unitsNeeded, String hospital, String city,
                        double latitude, double longitude, String contactPhone) {
        this.bloodGroup = BloodType.normalise(bloodGroup);
        this.unitsNeeded = unitsNeeded;
        this.hospital = hospital;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contactPhone = contactPhone;
        this.status = STATUS_OPEN;
        this.createdAt = System.currentTimeMillis();
    }
}
