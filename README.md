# Blood Bank Management

A native Android app for donor registration, blood bank stock, and matching
open requests to the closest compatible donors.

Written in Java with XML layouts and a local SQLite store. It works fully
offline: no account, no network, no API keys.

## What it does

- **Donors.** Register a donor with blood group, phone and area. Mark them
  available or paused. Optional device location is used to rank proximity.
- **Requests.** Raise a request for N units of a group from a hospital or bank.
- **Matching.** Filters to compatible donors, then ranks them nearest first,
  with an exact group match preferred over a universal donor at equal distance.
- **Stock.** Blood bank inventory per group.

## Compatibility rules

Donor to recipient, for red cells. Both must hold:

- **Antigen.** Group O carries neither A nor B, so O may donate to any group.
  Every other donor must carry the same antigen set, so AB is compared in full
  and not by its first letter.
- **Rh.** A positive donor may only give to a positive recipient. A negative
  donor may give to either, which is why O negative is the universal donor.

The rule that must never be violated is positive into negative. `BloodTypeTest`
asserts the full table in both directions, including a test named
`positiveIntoNegativeIsNeverAllowed` that exists to stop that regressing.

## Build

Requires JDK 17 or newer and an Android SDK with platform 35.

```bash
./gradlew testDebugUnitTest     # unit tests
./gradlew assembleDebug         # debug APK
./gradlew assembleRelease       # release APK
```

The release build is signed with the debug key so the APK is installable
straight from a download link. A Play Store release would use a private upload
keystore instead.

## Install

Download `app-release.apk` from the releases page, allow installs from your
browser, then open it. Android 7.0 or newer.

## Sample data

The app seeds ten donors, three requests and stock levels on first launch so it
demonstrates itself immediately. The coordinates are real Hyderabad locations,
which makes the distance ranking meaningful. The people, hospitals and stock
figures are invented sample values, not real records.

## Layout

```
app/src/main/java/com/manideep/bloodbank/
  data/        BloodType, Donor, BloodRequest, BloodDatabase (SQLite)
  logic/       MatchingEngine, Geo (haversine), DeviceLocation
  ui/          MainActivity, fragments, adapters, form activities
app/src/test/  BloodTypeTest
```
