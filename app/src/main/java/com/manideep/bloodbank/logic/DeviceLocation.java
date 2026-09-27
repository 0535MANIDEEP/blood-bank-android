package com.manideep.bloodbank.logic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.content.ContextCompat;

/**
 * Optional device location via the platform LocationManager.
 *
 * Deliberately not Google Play Services: the app must not need a Maps API key
 * to record a position. If permission is missing or no provider is enabled the
 * entry is still saved, just without coordinates.
 */
public final class DeviceLocation {

    public static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private DeviceLocation() {
    }

    public static boolean hasPermission(Context context) {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    /** @return last known position, or null when unavailable */
    public static Location lastKnown(Context context) {
        if (!hasPermission(context)) {
            return null;
        }
        LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager == null) {
            return null;
        }

        Location best = null;
        try {
            for (String provider : manager.getProviders(true)) {
                Location candidate = manager.getLastKnownLocation(provider);
                if (candidate == null) {
                    continue;
                }
                if (best == null || candidate.getAccuracy() < best.getAccuracy()) {
                    best = candidate;
                }
            }
        } catch (SecurityException ignored) {
            return null;
        }
        return best;
    }
}
