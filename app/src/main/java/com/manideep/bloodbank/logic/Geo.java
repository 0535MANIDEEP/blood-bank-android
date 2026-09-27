package com.manideep.bloodbank.logic;

/** Great-circle distance between two coordinates. */
public final class Geo {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private Geo() {
    }

    /**
     * Haversine distance in kilometres.
     *
     * @return distance in km, or {@link Double#NaN} if either coordinate is unset
     */
    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        if (Double.isNaN(lat1) || Double.isNaN(lon1) || Double.isNaN(lat2) || Double.isNaN(lon2)) {
            return Double.NaN;
        }

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public static String formatKm(double km) {
        if (Double.isNaN(km)) {
            return "Location unknown";
        }
        if (km < 1) {
            return Math.round(km * 1000) + " m away";
        }
        return String.format(java.util.Locale.US, "%.1f km away", km);
    }
}
