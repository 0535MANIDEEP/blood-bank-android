package com.manideep.bloodbank.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Red cell compatibility rules.
 *
 * These decide who may give blood to whom, so the table is asserted in full
 * rather than spot checked. An earlier version of this rule had the Rh
 * direction inverted, which wrongly let Rh positive blood reach an Rh negative
 * recipient. {@code positiveIntoNegativeIsNeverAllowed} exists to stop that
 * regressing.
 */
public class BloodTypeTest {

    @Test
    public void oNegativeIsTheUniversalDonor() {
        for (String recipient : BloodType.ALL) {
            assertTrue("O- should donate to " + recipient,
                    BloodType.canDonate("O-", recipient));
        }
    }

    @Test
    public void abPositiveCanOnlyDonateToAbPositive() {
        for (String recipient : BloodType.ALL) {
            boolean expected = "AB+".equals(recipient);
            assertEquals("AB+ -> " + recipient, expected,
                    BloodType.canDonate("AB+", recipient));
        }
    }

    @Test
    public void positiveIntoNegativeIsNeverAllowed() {
        String[] positives = {"O+", "A+", "B+", "AB+"};
        String[] negatives = {"O-", "A-", "B-", "AB-"};

        for (String donor : positives) {
            for (String recipient : negatives) {
                assertFalse(donor + " must not donate to " + recipient,
                        BloodType.canDonate(donor, recipient));
            }
        }
    }

    @Test
    public void negativeCanDonateToPositive() {
        // This is what makes O negative universal, so it must never be blocked.
        assertTrue(BloodType.canDonate("O-", "O+"));
        assertTrue(BloodType.canDonate("A-", "A+"));
        assertTrue(BloodType.canDonate("B-", "B+"));
        assertTrue(BloodType.canDonate("AB-", "AB+"));
    }

    @Test
    public void antigenMustMatchUnlessGroupO() {
        assertFalse(BloodType.canDonate("A-", "B+"));
        assertFalse(BloodType.canDonate("B-", "A+"));
        assertFalse(BloodType.canDonate("AB-", "O+"));
        assertTrue(BloodType.canDonate("A-", "A-"));
        assertTrue(BloodType.canDonate("AB+", "AB+"));
    }

    @Test
    public void recipientsTable() {
        assertEquals(set("O-"), set(BloodType.donorsFor("O-")));
        assertEquals(set("O-", "O+"), set(BloodType.donorsFor("O+")));
        assertEquals(set("O-", "A-"), set(BloodType.donorsFor("A-")));
        assertEquals(set("O-", "O+", "A-", "A+"), set(BloodType.donorsFor("A+")));
        assertEquals(set("O-", "B-"), set(BloodType.donorsFor("B-")));
        assertEquals(set("O-", "O+", "B-", "B+"), set(BloodType.donorsFor("B+")));
        assertEquals(set("O-", "AB-"), set(BloodType.donorsFor("AB-")));
        assertEquals(set("O-", "O+", "AB-", "AB+"), set(BloodType.donorsFor("AB+")));
    }

    @Test
    public void invalidInputIsRejected() {
        assertFalse(BloodType.canDonate("", "O+"));
        assertFalse(BloodType.canDonate("O+", ""));
        assertFalse(BloodType.canDonate("Z+", "O+"));
        assertFalse(BloodType.canDonate(null, "O+"));
    }

    @Test
    public void inputIsNormalised() {
        assertTrue(BloodType.canDonate("o-", "o+"));
        assertTrue(BloodType.canDonate("  ab-  ", "AB+"));
        assertTrue(BloodType.isValid("a+"));
        assertFalse(BloodType.isValid("A"));
    }

    private static Set<String> set(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}
