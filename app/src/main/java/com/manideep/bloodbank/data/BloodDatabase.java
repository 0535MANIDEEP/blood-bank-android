package com.manideep.bloodbank.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Local SQLite store. Everything works offline with no account and no network,
 * so the app is usable the moment it is installed.
 */
public class BloodDatabase extends SQLiteOpenHelper {

    private static final String NAME = "bloodbank.db";
    private static final int VERSION = 1;

    public BloodDatabase(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE donors ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "phone TEXT NOT NULL, "
                + "blood_group TEXT NOT NULL, "
                + "city TEXT NOT NULL DEFAULT '', "
                + "lat REAL NOT NULL DEFAULT 0, "
                + "lon REAL NOT NULL DEFAULT 0, "
                + "has_location INTEGER NOT NULL DEFAULT 0, "
                + "available INTEGER NOT NULL DEFAULT 1, "
                + "created_at INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE requests ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "blood_group TEXT NOT NULL, "
                + "units INTEGER NOT NULL, "
                + "hospital TEXT NOT NULL, "
                + "city TEXT NOT NULL DEFAULT '', "
                + "lat REAL NOT NULL DEFAULT 0, "
                + "lon REAL NOT NULL DEFAULT 0, "
                + "has_location INTEGER NOT NULL DEFAULT 0, "
                + "contact_phone TEXT NOT NULL DEFAULT '', "
                + "status TEXT NOT NULL, "
                + "created_at INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE inventory ("
                + "blood_group TEXT PRIMARY KEY, "
                + "units INTEGER NOT NULL)");

        seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS donors");
        db.execSQL("DROP TABLE IF EXISTS requests");
        db.execSQL("DROP TABLE IF EXISTS inventory");
        onCreate(db);
    }

    /**
     * First-run sample data so the app demonstrates itself immediately.
     * Coordinates are real Hyderabad locations, which makes the distance
     * ranking meaningful. The people, hospitals and stock figures are invented
     * sample values, not real records.
     */
    private void seed(SQLiteDatabase db) {
        // Typed parallel arrays rather than Object[][], so every value put()
        // call is type correct without casting.
        String[][] donorText = {
                {"Ananya Rao", "9845012345", "O+", "Banjara Hills"},
                {"Rohit Menon", "9845012346", "A+", "Gachibowli"},
                {"Sridhar Reddy", "9845012347", "B+", "Secunderabad"},
                {"Fatima Sheikh", "9845012348", "AB+", "Charminar"},
                {"Vikram Rao", "9845012349", "O-", "Kukatpally"},
                {"Praveen Kumar", "9845012350", "A-", "Dilsukhnagar"},
                {"Sameer Khan", "9845012351", "B-", "HITEC City"},
                {"Divya Sharma", "9845012352", "AB-", "LB Nagar"},
                {"Naveen Chandra", "9845012353", "O+", "Begumpet"},
                {"Kiran Joseph", "9845012354", "A+", "Uppal"},
        };
        double[][] donorGeo = {
                {17.4126, 78.4347}, {17.4401, 78.3489}, {17.3899, 78.4983}, {17.3616, 78.4747},
                {17.4149, 78.5200}, {17.3327, 78.5010}, {17.4435, 78.3772}, {17.2751, 78.5029},
                {17.4232, 78.4624}, {17.3996, 78.5591},
        };
        int[] donorAvailable = {1, 1, 1, 1, 1, 0, 1, 1, 1, 1};

        for (int i = 0; i < donorText.length; i++) {
            ContentValues cv = new ContentValues();
            cv.put("name", donorText[i][0]);
            cv.put("phone", donorText[i][1]);
            cv.put("blood_group", donorText[i][2]);
            cv.put("city", donorText[i][3]);
            cv.put("lat", donorGeo[i][0]);
            cv.put("lon", donorGeo[i][1]);
            cv.put("has_location", 1);
            cv.put("available", donorAvailable[i]);
            cv.put("created_at", System.currentTimeMillis());
            db.insert("donors", null, cv);
        }

        String[][] requestText = {
                {"O+", "Sunrise Blood Bank", "Banjara Hills", "04040001234"},
                {"A+", "City Care Hospital", "Secunderabad", "04040005678"},
                {"B-", "Metro General Hospital", "Gachibowli", "04040009012"},
        };
        int[] requestUnits = {3, 2, 1};
        double[][] requestGeo = {
                {17.4130, 78.4350}, {17.3905, 78.4990}, {17.4395, 78.3495},
        };

        for (int i = 0; i < requestText.length; i++) {
            ContentValues cv = new ContentValues();
            cv.put("blood_group", requestText[i][0]);
            cv.put("units", requestUnits[i]);
            cv.put("hospital", requestText[i][1]);
            cv.put("city", requestText[i][2]);
            cv.put("lat", requestGeo[i][0]);
            cv.put("lon", requestGeo[i][1]);
            cv.put("has_location", 1);
            cv.put("contact_phone", requestText[i][3]);
            cv.put("status", BloodRequest.STATUS_OPEN);
            cv.put("created_at", System.currentTimeMillis());
            db.insert("requests", null, cv);
        }

        int[] stock = {14, 9, 7, 4, 5, 2, 11, 3};
        for (int i = 0; i < BloodType.ALL.length; i++) {
            ContentValues cv = new ContentValues();
            cv.put("blood_group", BloodType.ALL[i]);
            cv.put("units", stock[i]);
            db.insert("inventory", null, cv);
        }
    }

    // ---------- donors ----------

    public long insertDonor(Donor donor) {
        ContentValues cv = new ContentValues();
        cv.put("name", donor.name);
        cv.put("phone", donor.phone);
        cv.put("blood_group", donor.bloodGroup);
        cv.put("city", donor.city);
        cv.put("lat", donor.latitude);
        cv.put("lon", donor.longitude);
        cv.put("has_location", hasLocation(donor.latitude, donor.longitude) ? 1 : 0);
        cv.put("available", donor.available ? 1 : 0);
        cv.put("created_at", donor.createdAt);
        return getWritableDatabase().insert("donors", null, cv);
    }

    public List<Donor> allDonors() {
        return readDonors("SELECT * FROM donors ORDER BY created_at DESC", null);
    }

    public List<Donor> availableDonors() {
        return readDonors("SELECT * FROM donors WHERE available = 1", null);
    }

    public void setDonorAvailable(long id, boolean available) {
        ContentValues cv = new ContentValues();
        cv.put("available", available ? 1 : 0);
        getWritableDatabase().update("donors", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteDonor(long id) {
        getWritableDatabase().delete("donors", "id = ?", new String[]{String.valueOf(id)});
    }

    private List<Donor> readDonors(String sql, String[] args) {
        List<Donor> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(sql, args);
        while (c.moveToNext()) {
            Donor d = new Donor();
            d.id = c.getLong(c.getColumnIndexOrThrow("id"));
            d.name = c.getString(c.getColumnIndexOrThrow("name"));
            d.phone = c.getString(c.getColumnIndexOrThrow("phone"));
            d.bloodGroup = c.getString(c.getColumnIndexOrThrow("blood_group"));
            d.city = c.getString(c.getColumnIndexOrThrow("city"));
            boolean hasLoc = c.getInt(c.getColumnIndexOrThrow("has_location")) == 1;
            double lat = c.getDouble(c.getColumnIndexOrThrow("lat"));
            double lon = c.getDouble(c.getColumnIndexOrThrow("lon"));
            d.latitude = hasLoc ? lat : Double.NaN;
            d.longitude = hasLoc ? lon : Double.NaN;
            d.available = c.getInt(c.getColumnIndexOrThrow("available")) == 1;
            d.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
            out.add(d);
        }
        c.close();
        return out;
    }

    // ---------- requests ----------

    public long insertRequest(BloodRequest request) {
        ContentValues cv = new ContentValues();
        cv.put("blood_group", request.bloodGroup);
        cv.put("units", request.unitsNeeded);
        cv.put("hospital", request.hospital);
        cv.put("city", request.city);
        cv.put("lat", request.latitude);
        cv.put("lon", request.longitude);
        cv.put("has_location", hasLocation(request.latitude, request.longitude) ? 1 : 0);
        cv.put("contact_phone", request.contactPhone);
        cv.put("status", request.status);
        cv.put("created_at", request.createdAt);
        return getWritableDatabase().insert("requests", null, cv);
    }

    public List<BloodRequest> allRequests() {
        return readRequests("SELECT * FROM requests ORDER BY created_at DESC", null);
    }

    public List<BloodRequest> openRequests() {
        return readRequests("SELECT * FROM requests WHERE status = ? ORDER BY created_at DESC",
                new String[]{BloodRequest.STATUS_OPEN});
    }

    public BloodRequest requestById(long id) {
        List<BloodRequest> list = readRequests("SELECT * FROM requests WHERE id = ?",
                new String[]{String.valueOf(id)});
        return list.isEmpty() ? null : list.get(0);
    }

    public void setRequestStatus(long id, String status) {
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        getWritableDatabase().update("requests", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteRequest(long id) {
        getWritableDatabase().delete("requests", "id = ?", new String[]{String.valueOf(id)});
    }

    private List<BloodRequest> readRequests(String sql, String[] args) {
        List<BloodRequest> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(sql, args);
        while (c.moveToNext()) {
            BloodRequest r = new BloodRequest();
            r.id = c.getLong(c.getColumnIndexOrThrow("id"));
            r.bloodGroup = c.getString(c.getColumnIndexOrThrow("blood_group"));
            r.unitsNeeded = c.getInt(c.getColumnIndexOrThrow("units"));
            r.hospital = c.getString(c.getColumnIndexOrThrow("hospital"));
            r.city = c.getString(c.getColumnIndexOrThrow("city"));
            boolean hasLoc = c.getInt(c.getColumnIndexOrThrow("has_location")) == 1;
            double lat = c.getDouble(c.getColumnIndexOrThrow("lat"));
            double lon = c.getDouble(c.getColumnIndexOrThrow("lon"));
            r.latitude = hasLoc ? lat : Double.NaN;
            r.longitude = hasLoc ? lon : Double.NaN;
            r.contactPhone = c.getString(c.getColumnIndexOrThrow("contact_phone"));
            r.status = c.getString(c.getColumnIndexOrThrow("status"));
            r.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
            out.add(r);
        }
        c.close();
        return out;
    }

    // ---------- inventory ----------

    public int unitsFor(String bloodGroup) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT units FROM inventory WHERE blood_group = ?",
                new String[]{BloodType.normalise(bloodGroup)});
        int units = c.moveToFirst() ? c.getInt(0) : 0;
        c.close();
        return units;
    }

    public int totalUnits() {
        Cursor c = getReadableDatabase().rawQuery("SELECT COALESCE(SUM(units), 0) FROM inventory", null);
        int total = c.moveToFirst() ? c.getInt(0) : 0;
        c.close();
        return total;
    }

    public void adjustStock(String bloodGroup, int delta) {
        String group = BloodType.normalise(bloodGroup);
        int current = unitsFor(group);
        ContentValues cv = new ContentValues();
        cv.put("units", Math.max(0, current + delta));
        getWritableDatabase().insertWithOnConflict("inventory", null, cv,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<String[]> inventoryRows() {
        List<String[]> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT blood_group, units FROM inventory", null);
        while (c.moveToNext()) {
            out.add(new String[]{c.getString(0), String.valueOf(c.getInt(1))});
        }
        c.close();
        return out;
    }

    private static boolean hasLocation(double lat, double lon) {
        return !Double.isNaN(lat) && !Double.isNaN(lon)
                && (lat != 0d || lon != 0d);
    }
}
