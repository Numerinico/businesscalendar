package eu.brengard.businesscalendar.entitites;

import eu.brengard.businesscalendar.exceptions.BusinessCalendarSameBusinessDaysException;
import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class BusinessCalendarTest extends BusinessTest {

    private LinkedHashSet<BusinessDay> businessDays;
    private Set<LocalDate> holidays;

    @Before
    public void setUp() {
        BusinessDay monday = BusinessDay.of(DayOfWeek.MONDAY);
        Set<BusinessTimeSlot> mondayTimeSlots = monday.getBusinessTimeSlots();

        mondayTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(12, 0)));
        mondayTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(13, 0), LocalTime.of(17, 0)));

        BusinessDay tuesday = BusinessDay.of(DayOfWeek.TUESDAY);
        Set<BusinessTimeSlot> tuesdayTimeSlots = tuesday.getBusinessTimeSlots();
        tuesdayTimeSlots.add(BusinessTimeSlot
                .of(LocalTime.of(9, 0), LocalTime.of(17, 0)));


        //        tuesday2.getBusinessTimeSlots().add(BusinessTimeSlot
        //                .of(LocalTime.of(9, 0), LocalTime.of(16, 0)));

        businessDays = new LinkedHashSet<>();
        businessDays.add(monday);
        businessDays.add(tuesday);

        holidays = new HashSet<>();
        holidays.add(LocalDate.of(2016, 4, 26));
    }

    @Test
    public void isWorkingTimeNullParameter1() {
        try {
            BusinessCalendar.of(null, holidays);
            fail();
        } catch (NullPointerException e) {
            assert true;
        }
    }

    @Test
    public void isWorkingTimeNullParameter2() {
        try {
            BusinessCalendar.of(businessDays, null);
            fail();
        } catch (NullPointerException e) {
            assert true;
        }
    }


    @Test
    public void isWorkingTimeTrue() {
        ZonedDateTime mondayAt11h00 =
                ZonedDateTime.of(2016, 4, 18, 11, 0, 0, 0, zoneId);
        BusinessCalendar businessCalendar = BusinessCalendar
                .of(businessDays, holidays);
        Boolean isWorkingTime = businessCalendar.isWorkingTime(mondayAt11h00);

        assertTrue(isWorkingTime);
    }

    @Test
    public void isWorkingTimeFalse() {
        ZonedDateTime mondayAt5h00 =
                ZonedDateTime.of(2016, 4, 18, 5, 0, 0, 0, zoneId);
        BusinessCalendar businessCalendar = BusinessCalendar
                .of(businessDays, holidays);
        Boolean isWorkingTime = businessCalendar.isWorkingTime(mondayAt5h00);

        assertFalse(isWorkingTime);
    }

    @Test
    public void timeSame() {
        ZonedDateTime tuesdayAt15h30 =
                ZonedDateTime.of(2016, 4, 19, 15, 30, 0, 0, zoneId);
        BusinessCalendar businessCalendar = BusinessCalendar
                .of(businessDays, holidays);
        assertEquals(Duration.ZERO,
                businessCalendar.getDuration(tuesdayAt15h30, tuesdayAt15h30));
    }

    @Test
    public void timeBetweenLower() {
        ZonedDateTime tuesdayAt15h30 =
                ZonedDateTime.of(2016, 4, 19, 15, 30, 0, 0, zoneId);
        ZonedDateTime tuesdayAt16h30 =
                ZonedDateTime.of(2016, 4, 19, 16, 30, 0, 0, zoneId);
        BusinessCalendar businessCalendar = BusinessCalendar
                .of(businessDays, holidays);
        assertEquals(Duration.ofHours(1),
                businessCalendar.getDuration(tuesdayAt15h30, tuesdayAt16h30));
    }

    @Test
    public void timeBetweenGreater() {
        ZonedDateTime tuesdayAt15h30 =
                ZonedDateTime.of(2016, 4, 19, 15, 30, 0, 0, zoneId);
        ZonedDateTime tuesdayAt16h30 =
                ZonedDateTime.of(2016, 4, 19, 16, 30, 0, 0, zoneId);
        BusinessCalendar businessCalendar = BusinessCalendar
                .of(businessDays, holidays);
        assertEquals(Duration.ofHours(1).negated(),
                businessCalendar.getDuration(tuesdayAt16h30, tuesdayAt15h30));
    }

    @Test
    public void timeBetweenTwoDays() {
        ZonedDateTime mondayAt16h30 =
                ZonedDateTime.of(2016, 4, 18, 16, 30, 0, 0, zoneId);
        ZonedDateTime tuesdayAt9h30 =
                ZonedDateTime.of(2016, 4, 19, 9, 30, 0, 0, zoneId);
        BusinessCalendar businessCalendar = BusinessCalendar
                .of(businessDays, holidays);
        assertEquals(Duration.ofHours(1),
                businessCalendar.getDuration(mondayAt16h30, tuesdayAt9h30));
    }

    @Test
    public void longerThanADayTimeBetweenTwoDays() {
        ZonedDateTime sundayAt16h30 =
                ZonedDateTime.of(2016, 4, 17, 16, 30, 0, 0, zoneId);
        ZonedDateTime tuesdayAt10h00 =
                ZonedDateTime.of(2016, 4, 19, 10, 0, 0, 0, zoneId);
        BusinessCalendar businessCalendar = BusinessCalendar
                .of(businessDays, holidays);
        assertEquals(Duration.ofHours(8),
                businessCalendar.getDuration(sundayAt16h30, tuesdayAt10h00));
    }

    @Test
    public void TimeBetweenTwoDaysIncludingAWeekEnd() {
        ZonedDateTime mondayAt16h30 =
                ZonedDateTime.of(2016, 4, 18, 16, 30, 0, 0, zoneId);
        ZonedDateTime mondayAt9h30WeekAfter =
                ZonedDateTime.of(2016, 4, 25, 9, 30, 0, 0, zoneId);
        BusinessCalendar businessCalendar = BusinessCalendar
                .of(businessDays, holidays);
        assertEquals(Duration.ofHours(9), businessCalendar
                .getDuration(mondayAt16h30, mondayAt9h30WeekAfter));
    }

    @Test
    public void TimeBetweenTwoDaysIncludingHolidayAndWeekEnd() {
        ZonedDateTime tuesdayAt16h00 =
                ZonedDateTime.of(2016, 4, 19, 16, 0, 0, 0, zoneId);
        ZonedDateTime tuesdayAt9h30WeekAfter =
                ZonedDateTime.of(2016, 4, 26, 9, 30, 0, 0, zoneId);
        BusinessCalendar businessCalendar = BusinessCalendar
                .of(businessDays, holidays);
        assertEquals(Duration.ofHours(8), businessCalendar
                .getDuration(tuesdayAt16h00, tuesdayAt9h30WeekAfter));
    }

    @Test
    public void TimeBetweenTwoDaysIncludingHolidayAndWeekEndInverted() {
        ZonedDateTime tuesdayAt16h00 =
                ZonedDateTime.of(2016, 4, 19, 16, 0, 0, 0, zoneId);
        ZonedDateTime tuesdayAt9h30WeekAfter =
                ZonedDateTime.of(2016, 4, 26, 9, 30, 0, 0, zoneId);
        BusinessCalendar businessCalendar = BusinessCalendar
                .of(businessDays, holidays);
        assertEquals(Duration.ofHours(8).negated(), businessCalendar
                .getDuration(tuesdayAt9h30WeekAfter, tuesdayAt16h00));
    }

    @Test
    public void TimeInsideWeekEnd() {
        ZonedDateTime saturdayAt10h00 =
                ZonedDateTime.of(2016, 4, 23, 10, 0, 0, 0, zoneId);
        ZonedDateTime sundayAt11h00 =
                ZonedDateTime.of(2016, 4, 24, 11, 0, 0, 0, zoneId);
        BusinessCalendar businessCalendar = BusinessCalendar
                .of(businessDays, holidays);
        assertEquals(Duration.ofHours(0),
                businessCalendar.getDuration(saturdayAt10h00, sundayAt11h00));
    }

    @Test
    public void addBusinessDay() {
        BusinessCalendar businessCalendar = BusinessCalendar.of(businessDays);
        businessCalendar.addBusinessDay(BusinessDay.of(DayOfWeek.WEDNESDAY));
        int sizeAfter = businessCalendar.getBusinessDays().size();
        assertEquals(2, sizeAfter - 1);
    }

    @Test
    public void removeBusinessDay() {
        BusinessCalendar businessCalendar = BusinessCalendar.of(businessDays);
        BusinessDay businessDayToRemove = null;
        //get first business day of the set, the ugly way
        for (BusinessDay businessDay : businessDays) {
            businessDayToRemove = businessDay;
            break;
        }
        businessCalendar.removeBusinessDay(businessDayToRemove);
        int sizeAfter = businessCalendar.getBusinessDays().size();
        assertEquals(1, sizeAfter);
    }

    @Test
    public void removeBusinessDayOfWeek() {
        BusinessCalendar businessCalendar = BusinessCalendar.of(businessDays);
        businessCalendar.removeBusinessDayFromDayOfWeek(DayOfWeek.MONDAY);
        int sizeAfter = businessCalendar.getBusinessDays().size();
        assertEquals(1, sizeAfter);
    }

    @Test
    public void addHoliday() {
        BusinessCalendar businessCalendar = BusinessCalendar.of(businessDays);
        businessCalendar.addHoliday(LocalDate.of(2016, 12, 12));
        int sizeAfter = businessCalendar.getHolidays().size();
        assertEquals(1, sizeAfter);
    }

    @Test
    public void removeHoliday() {
        BusinessCalendar businessCalendar = BusinessCalendar.of(businessDays);
        businessCalendar.setHolidays(holidays);
        businessCalendar.removeHoliday(LocalDate.of(2016, 4, 26));
        int sizeAfter = businessCalendar.getHolidays().size();
        assertEquals(0, sizeAfter);
    }

    @Test
    public void addSameBusinessDayToCalendar() {
        try {
            BusinessDay tuesday = BusinessDay.of(DayOfWeek.TUESDAY);
            BusinessCalendar businessCalendar =
                    BusinessCalendar.of(businessDays);
            businessCalendar.addBusinessDay(tuesday);
            fail();
        } catch (BusinessCalendarSameBusinessDaysException e) {
            assert true;
        }
    }

    @Test
    public void calendarFromSetWithTwoSameBusinessDay() {
        try {
            BusinessDay tuesday = BusinessDay.of(DayOfWeek.TUESDAY);
            businessDays.add(tuesday);
            BusinessCalendar.of(businessDays);
            fail();
        } catch (BusinessCalendarSameBusinessDaysException e) {
            assert true;
        }
    }
}