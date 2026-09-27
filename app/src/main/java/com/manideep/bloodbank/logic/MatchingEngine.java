package com.manideep.bloodbank.logic;

import com.manideep.bloodbank.data.BloodRequest;
import com.manideep.bloodbank.data.BloodType;
import com.manideep.bloodbank.data.Donor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Donor matching.
 *
 * A donor is a candidate when the blood groups are compatible, the donor has
 * marked themselves available, and the stock of that group is not exhausted.
 * Candidates are ranked by distance from the requesting hospital, nearest
 * first, because a donation that cannot be collected in time is not useful.
 */
public final class MatchingEngine {

    /** One candidate donor with their distance from the request. */
    public static class Match {
        public final Donor donor;
        public final double distanceKm;

        Match(Donor donor, double distanceKm) {
            this.donor = donor;
            this.distanceKm = distanceKm;
        }
    }

    private MatchingEngine() {
    }

    public static List<Match> matchesFor(BloodRequest request, List<Donor> donors) {
        List<Match> matches = new ArrayList<>();

        for (Donor donor : donors) {
            if (!donor.available) {
                continue;
            }
            if (!BloodType.canDonate(donor.bloodGroup, request.bloodGroup)) {
                continue;
            }

            double km = Geo.distanceKm(
                    donor.latitude, donor.longitude,
                    request.latitude, request.longitude);

            // Unknown location sorts last rather than pretending to be nearest.
            matches.add(new Match(donor, Double.isNaN(km) ? Double.MAX_VALUE : km));
        }

        Collections.sort(matches, new Comparator<Match>() {
            @Override
            public int compare(Match a, Match b) {
                int byDistance = Double.compare(a.distanceKm, b.distanceKm);
                if (byDistance != 0) {
                    return byDistance;
                }
                // Exact group match is a better tie-breaker than a universal donor.
                return Integer.compare(
                        rank(a.donor.bloodGroup, request.bloodGroup),
                        rank(b.donor.bloodGroup, request.bloodGroup));
            }
        });

        return matches;
    }

    /** 0 when the donor is exactly the requested group, 1 when they are a universal donor. */
    private static int rank(String donorGroup, String requestedGroup) {
        if (BloodType.normalise(donorGroup).equals(BloodType.normalise(requestedGroup))) {
            return 0;
        }
        return 1;
    }

    /** How many compatible donors exist, ignoring distance. Used for the request summary. */
    public static int compatibleCount(BloodRequest request, List<Donor> donors) {
        int count = 0;
        for (Donor donor : donors) {
            if (donor.available && BloodType.canDonate(donor.bloodGroup, request.bloodGroup)) {
                count++;
            }
        }
        return count;
    }
}
