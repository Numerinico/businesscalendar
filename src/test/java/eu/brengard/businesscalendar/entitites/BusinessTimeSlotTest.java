package eu.brengard.businesscalendar.entitites;

import eu.brengard.businesscalendar.exceptions.BusinessTimeSlotParseException;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

public class BusinessTimeSlotTest extends BusinessTest {
    private LocalTime startTime;
    private LocalTime endTime;

    @Before
    public void setUp() {
        this.startTime = LocalTime.of(9, 0);
        this.endTime = LocalTime.of(17, 0);
    }

    @Test
    public void constructorGoodArguments() {
        try {
            BusinessTimeSlot.of(startTime, endTime);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void constructorIllegalArguments() {
        try {
            BusinessTimeSlot.of(endTime, startTime);
            fail();
        } catch (IllegalArgumentException e) {
            assert true;
        }
    }

    @Test
    public void testStringConstructor1Arg() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);

        BusinessTimeSlot businessTimeSlotFromString =
                BusinessTimeSlot.parse("09:00-17:00");
        assertTrue(businessTimeSlot.equals(businessTimeSlotFromString));
    }

    @Test
    public void testStringConstructor2Args() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);

        BusinessTimeSlot businessTimeSlotFromString =
                BusinessTimeSlot.parse("09:00-17:00", "-");
        assertTrue(businessTimeSlot.equals(businessTimeSlotFromString));
    }

    @Test
    public void testStringConstructor3Args() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);

        BusinessTimeSlot businessTimeSlotFromString = BusinessTimeSlot
                .parse("09:00-17:00", "-", DateTimeFormatter.ofPattern("H:mm"));
        assertTrue(businessTimeSlot.equals(businessTimeSlotFromString));
    }

    @Test
    public void testStringConstructor1ArgException() {
        try {
            BusinessTimeSlot.parse("09:0017:00");
            fail();
        } catch (BusinessTimeSlotParseException e) {
            assert true;
        }
    }

    @Test
    public void testStringConstructor1ArgExceptionBis() {
        try {
            BusinessTimeSlot.parse("");
            fail();
        } catch (BusinessTimeSlotParseException e) {
            assert true;
        }
    }

    @Test
    public void testStringConstructor2ArgsException() {
        try {
            BusinessTimeSlot.parse("09:00-17:00", "/");
            fail();
        } catch (BusinessTimeSlotParseException e) {
            assert true;
        }
    }

    @Test
    public void testStringConstructor3ArgsException() {
        try {
            BusinessTimeSlot.parse("09:00-17:00", "-",
                    DateTimeFormatter.ofPattern("yyyy"));
            fail();
        } catch (BusinessTimeSlotParseException e) {
            assert true;
        }
    }

    @Test
    public void testStringConstructor3ArgsException1() {
        try {
            BusinessTimeSlot.parse("", "", DateTimeFormatter.ofPattern("yyyy"));
            fail();
        } catch (BusinessTimeSlotParseException e) {
            assert true;
        }
    }

    @Test
    public void testStringConstructor3ArgsException2() {
        try {
            BusinessTimeSlot
                    .parse(null, "-", DateTimeFormatter.ofPattern("yyyy"));
            fail();
        } catch (NullPointerException e) {
            assert true;
        }
    }

    @Test
    public void containTrue() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        assertTrue(businessTimeSlot.contains(LocalTime.of(12, 0)));
    }

    @Test
    public void containFalse() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        assertFalse(businessTimeSlot.contains(LocalTime.of(18, 0)));
    }

    @Test
    public void getStartTime() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        assertEquals(startTime, businessTimeSlot.getStartInclusive());
    }

    @Test
    public void getEndTime() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        assertEquals(endTime, businessTimeSlot.getEndExclusive());
    }

    @Test
    public void equals() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        assertEquals(businessTimeSlot, businessTimeSlot);
    }

    @Test
    public void notEquals() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        BusinessTimeSlot businessTimeSlot2 = BusinessTimeSlot
                .of(startTime.plusNanos(1), endTime);
        assertNotEquals(businessTimeSlot, businessTimeSlot2);
    }

    @Test
    public void notEquals2() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        assertNotEquals(businessTimeSlot, null);
    }

    @Test
    public void setStartTime() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        LocalTime startTime = this.startTime.plusHours(1);
        businessTimeSlot.setStartInclusive(startTime);
        assertEquals(startTime, businessTimeSlot.getStartInclusive());
    }

    @Test
    public void setStartTimeException() {
        try {
            BusinessTimeSlot businessTimeSlot =
                    BusinessTimeSlot.of(startTime, endTime);
            LocalTime startTime = this.startTime.plusHours(10);
            businessTimeSlot.setStartInclusive(startTime);
            fail();
        } catch (IllegalArgumentException e) {
            assert true;
        }
    }

    @Test
    public void setEndTime() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        LocalTime endTime = this.endTime.plusHours(1);
        businessTimeSlot.setEndExclusive(endTime);
        assertEquals(endTime, businessTimeSlot.getEndExclusive());
    }

    @Test
    public void setEndTimeException() {
        try {
            BusinessTimeSlot businessTimeSlot =
                    BusinessTimeSlot.of(startTime, endTime);
            LocalTime endTime = this.endTime.minusHours(10);
            businessTimeSlot.setEndExclusive(endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assert true;
        }
    }

    @Test
    public void betweenInside() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        Duration duration = businessTimeSlot
                .getDuration(LocalTime.of(12, 0, 0, 1),
                        LocalTime.of(13, 0, 0, 1));
        assertEquals(Duration.ofHours(1), duration);
    }

    @Test
    public void betweenInsideInverted() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        Duration duration = businessTimeSlot
                .getDuration(LocalTime.of(13, 0), LocalTime.of(12, 0));
        assertEquals(Duration.ofHours(1).negated(), duration);
    }

    @Test
    public void betweenOutside() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        Duration duration = businessTimeSlot
                .getDuration(LocalTime.of(8, 0), LocalTime.of(18, 0));
        assertEquals(Duration.ofHours(8), duration);
    }

    @Test
    public void betweenStartOutside() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        Duration duration = businessTimeSlot
                .getDuration(LocalTime.of(8, 0), LocalTime.of(13, 0));
        assertEquals(Duration.ofHours(4), duration);
    }

    @Test
    public void betweenStartOutsideInverted() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        Duration duration = businessTimeSlot
                .getDuration(LocalTime.of(13, 0), LocalTime.of(8, 0));
        assertEquals(Duration.ofHours(4).negated(), duration);
    }

    @Test
    public void betweenEndOutside() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        Duration duration = businessTimeSlot
                .getDuration(LocalTime.of(12, 32), LocalTime.of(18, 0));
        assertEquals(Duration.ofMinutes(4 * 60 + 60 - 32), duration);
    }

    @Test
    public void betweenEndOutsideAfter() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        Duration duration = businessTimeSlot
                .getDuration(LocalTime.of(18, 0), LocalTime.of(20, 0));
        assertEquals(Duration.ZERO, duration);
    }

    @Test
    public void betweenEndOutsideBefore() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(startTime, endTime);
        Duration duration = businessTimeSlot
                .getDuration(LocalTime.of(7, 0), LocalTime.of(8, 0));
        assertEquals(Duration.ZERO, duration);
    }

    @Test
    public void startingAtMidnight() {
        BusinessTimeSlot businessTimeSlot =
                BusinessTimeSlot.of(LocalTime.of(0, 0), LocalTime.of(7, 0));
        Duration duration = businessTimeSlot
                .getDuration(LocalTime.of(0, 0), LocalTime.of(7, 0));
        assertEquals(Duration.ofHours(7), duration);
    }

}