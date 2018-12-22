package moe.yuuta.server.dataverify;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataVerifierTest {
    private SampleObject objectToBeTested;

    @Before
    public void setUp() {
        resetObject();
    }

    @Test
    public void shouldVerify () {
        // Original object which should pass
        assertTrue(DataVerifier.verify(objectToBeTested));

        objectToBeTested.equalZero = 2333;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.greaterZero = 0;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.greaterOrEqualZero = -1;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.lesserZero = 0;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.lesserOrEqualZero = 1;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.lesserOrEqualInvalidNonNumber = "abc";
        // Invalid value will be ignored
        assertTrue(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.nonNullButCanEmptyString = null;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.nonNullObject = null;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.nonNullCannotEmptyString = "";
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.nonNullCannotEmptyString = null;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.nonNullCannotEmptyObject = new Double(1.0);
        assertTrue(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.nonNullCannotEmptyObject = null;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.mustIn123Int = 0;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.mustInApplePearRikkaString = "233";
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.mustInApplePearRikkaString = null;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.shouldGreaterThanN10AndLesserThan0Int = -10;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();

        objectToBeTested.shouldGreaterThanN10AndLesserThan0Int = 10;
        assertFalse(DataVerifier.verify(objectToBeTested));
        resetObject();
    }

    private void resetObject () {
        objectToBeTested = new SampleObject();
    }
}