package com.manideep.bloodbank;

import android.app.Application;

import com.manideep.bloodbank.data.BloodDatabase;

/** Holds the single database instance for the process. */
public class BloodBankApp extends Application {

    private BloodDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = new BloodDatabase(this);
    }

    public BloodDatabase db() {
        return database;
    }
}
