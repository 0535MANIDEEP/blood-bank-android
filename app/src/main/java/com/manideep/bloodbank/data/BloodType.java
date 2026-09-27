package com.manideep.bloodbank.data;

/**
 * The eight ABO blood groups and the rules for who can donate to whom.
 *
 * Compatibility is directional: a person with O negative can donate to every
 * group, while AB positive can only receive from AB positive. The app only
 * ever asks "which donors can serve this request", so the donor-side rule set
 * below is what matters.
 */
public final class BloodType {

    public static final String[] ALL = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    private BloodType() {
    }

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        for (String type : ALL) {
            if (type.equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }

    public static String normalise(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    /**
     * The ABO antigen set of a group, without the Rh sign.
     * AB is two characters, so this must drop only the trailing sign.
     */
    private static String antigenOf(String group) {
        return group.substring(0, group.length() - 1);
    }

    /**
     * True when a donor of {@code donorGroup} may donate to a recipient of
     * {@code recipientGroup}.
     *
     * <p>Red cell rules, both of which must hold:</p>
     * <ul>
     *   <li>Antigen. Group O carries neither A nor B, so O may donate to any
     *       group. Every other donor must carry the same antigen set as the
     *       recipient, which is why AB is compared in full and not by its
     *       first letter alone.</li>
     *   <li>Rh. Group positive carries the D antigen, so a positive donor may
     *       only give to a positive recipient. A negative donor may give to
     *       either, which is why O negative is the universal donor.</li>
     * </ul>
     */
    public static boolean canDonate(String donorGroup, String recipientGroup) {
        String donor = normalise(donorGroup);
        String recipient = normalise(recipientGroup);

        if (!isValid(donor) || !isValid(recipient)) {
            return false;
        }

        // Antigen must match, unless the donor is group O.
        String donorAntigen = antigenOf(donor);
        if (!"O".equals(donorAntigen) && !donorAntigen.equals(antigenOf(recipient))) {
            return false;
        }

        boolean donorPositive = donor.endsWith("+");
        boolean recipientPositive = recipient.endsWith("+");

        // Rh positive blood must never go into an Rh negative recipient.
        return !(donorPositive && !recipientPositive);
    }

    /** Every group that can donate to the given recipient group. */
    public static String[] donorsFor(String recipientGroup) {
        java.util.List<String> result = new java.util.ArrayList<>();
        for (String candidate : ALL) {
            if (canDonate(candidate, recipientGroup)) {
                result.add(candidate);
            }
        }
        return result.toArray(new String[0]);
    }
}
