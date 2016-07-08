package eu.brengard.businesscalendar.entitites;

import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class BusinessDayTest extends BusinessTest {
    private DayOfWeek dayOfWeek;
    private Set<BusinessTimeSlot> businessTimeSlots;

    @Before
    public void setUp() {
        this.dayOfWeek = DayOfWeek.MONDAY;
        businessTimeSlots = new HashSet<>();
    }

    @Test
    public void testStringConstructor() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(13, 0), LocalTime.of(17, 0)));
        BusinessDay day1 = BusinessDay.of(dayOfWeek, businessTimeSlots);

        Set<BusinessTimeSlot> businessTimeSlotsFromString = BusinessTimeSlot
                .parseMultiple("09:00-12:00,13:00-17:00",
                        BusinessTimeSlot.DEFAULT_SLOTS_SEPARATOR);
        BusinessDay day2 = BusinessDay.of(dayOfWeek,
                businessTimeSlotsFromString);
        assertTrue(day1.equals(day2));
    }

    @Test
    public void isWorkingTimeTrue() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        LocalTime localTime = LocalTime.of(11, 0);
        assertTrue(businessDay.contains(localTime));
    }

    @Test
    public void isWorkingTimeFalse() {
        LocalTime localTime = LocalTime.of(17, 30);
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        assertFalse(businessDay.contains(localTime));
    }

    @Test
    public void isWorkingDayFalse() {
        BusinessDay localBusinessDay = BusinessDay.of(this.dayOfWeek);
        assertFalse(localBusinessDay.isWorkingDay());
    }

    @Test
    public void isWorkingDayTrue() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        assertTrue(businessDay.isWorkingDay());
    }

    @Test
    public void getDaysOfWeek() {
        DayOfWeek monday = DayOfWeek.MONDAY;
        BusinessDay businessDay = BusinessDay.of(monday);
        assertEquals(monday, businessDay.getDayOfWeek());
    }

    @Test
    public void getBusinessTimeSlots() {
        DayOfWeek monday = DayOfWeek.MONDAY;
        Set<BusinessTimeSlot> businessTimeSlots = new HashSet<>();

        BusinessDay businessDay = BusinessDay.of(monday, businessTimeSlots);

        assertEquals(businessTimeSlots, businessDay.getBusinessTimeSlots());
    }

    @Test
    public void setDayOfWeek() {
        DayOfWeek monday = DayOfWeek.MONDAY;
        BusinessDay businessDay = BusinessDay.of(monday);
        DayOfWeek tuesday = DayOfWeek.TUESDAY;
        businessDay.setDayOfWeek(tuesday);
        assertEquals(tuesday, businessDay.getDayOfWeek() );
    }

    @Test
    public void equals() {
        BusinessDay businessDay = BusinessDay.of(DayOfWeek.MONDAY);
        assertEquals(businessDay, businessDay);
    }

    @Test
    public void notEquals() {
        BusinessDay businessDay = BusinessDay.of(DayOfWeek.MONDAY);
        BusinessDay businessDay1 = BusinessDay.of(DayOfWeek.TUESDAY);
        assertNotEquals(businessDay, businessDay1);
    }

    @Test
    public void notEquals2() {
        BusinessDay businessDay = BusinessDay.of(DayOfWeek.MONDAY);
        assertNotEquals(businessDay, null);
    }

    @Test
    public void betweenNoTimeSlot() {
        BusinessDay businessDay = BusinessDay.of(DayOfWeek.MONDAY);
        Duration duration = businessDay
                .getDuration(LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertEquals(Duration.ZERO, duration);
    }

    @Test
    public void TimeSlotMin() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(0, 0), LocalTime.of(2, 0)));
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        Duration duration = businessDay
                .getDuration(LocalTime.of(0, 0), LocalTime.of(11, 0));
        assertEquals(Duration.ofHours(2), duration);
    }

    @Test
    public void betweenInside() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(13, 0), LocalTime.of(17, 0)));
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        Duration duration = businessDay
                .getDuration(LocalTime.of(11, 0), LocalTime.of(14, 0));
        assertEquals(Duration.ofHours(2), duration);
    }

    @Test
    public void betweenInsideInverted() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(13, 0), LocalTime.of(17, 0)));
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        Duration duration = businessDay
                .getDuration(LocalTime.of(14, 0), LocalTime.of(11, 0));
        assertEquals(Duration.ofHours(2).negated(), duration);
    }

    @Test
    public void betweenOutside() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(13, 0), LocalTime.of(17, 0)));
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        Duration duration = businessDay
                .getDuration(LocalTime.of(8, 0), LocalTime.of(18, 0));
        assertEquals(Duration.ofHours(7), duration);
    }

    @Test
    public void betweenStartOutside() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(13, 0), LocalTime.of(17, 0)));
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        Duration duration = businessDay
                .getDuration(LocalTime.of(8, 0), LocalTime.of(16, 0));
        assertEquals(Duration.ofHours(6), duration);
    }

    @Test
    public void betweenEndOutside() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(13, 0), LocalTime.of(17, 0)));
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        Duration duration = businessDay
                .getDuration(LocalTime.of(11, 0), LocalTime.of(18, 0));
        assertEquals(Duration.ofHours(5), duration);
    }

    @Test
    public void multipleSameBusinessTimeSlots() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        Duration duration = businessDay
                .getDuration(LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertEquals(Duration.ofHours(1), duration);

    }

    @Test
    public void multipleNearlySameBusinessTimeSlots() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(10, 0), LocalTime.of(15, 0)));
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        Duration duration = businessDay
                .getDuration(LocalTime.of(11, 0), LocalTime.of(13, 0));
        //expected behavior allow coefficient
        assertEquals(Duration.ofHours(3), duration);
    }

    @Test
    public void multipleNearlySameBusinessTimeSlotsBis() {
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        businessTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(10, 0), LocalTime.of(12, 0)));
        BusinessDay businessDay = BusinessDay.of(dayOfWeek, businessTimeSlots);
        Duration duration = businessDay
                .getDuration(LocalTime.of(12, 0), LocalTime.of(11, 0));
        //expected behavior allow coefficient
        assertEquals(Duration.ofHours(2).negated(), duration);
    }

}