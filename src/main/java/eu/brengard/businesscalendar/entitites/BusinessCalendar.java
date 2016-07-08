package eu.brengard.businesscalendar.entitites;

import eu.brengard.businesscalendar.exceptions.BusinessCalendarSameBusinessDaysException;

import java.io.Serializable;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * A business calendar, with typical working days and holidays.
 * <p>
 * This class provides methods to create a typical business calendar for a
 * company. It also provide a method to get the {@link Duration} getDuration two
 * {@link ZonedDateTime} by counting only the time where the company is open.
 * The company is considered open using its typical working days and holidays
 * <p>
 * A business calendar is constituted of two objects :
 * <ul>
 * <li>
 * A collection of {@link BusinessDay}s defining the typical days of a company
 * week.
 * It is <b>important</b> to know that it can not contains two
 * {@link BusinessDay}s defining to the same day of week.
 * </li>
 * <li>
 * A set of {@link LocalDate} containing all the company holidays.
 * </li>
 * </ul>
 *
 * @author Nicolas BRENGARD (eu.brengard.businesscalendar.entitites@brengard.eu)
 * @see java.time
 * @since 1.8
 */
public class BusinessCalendar implements Serializable {


    /**
     * {@link HashMap} that contains a business day (value) and its day of week (key).
     */
    private Map<DayOfWeek, BusinessDay> businessDays;

    /**
     * Set that contains the company holidays as {@link LocalDate}s.
     */
    private Set<LocalDate> holidays;

    /**
     * Simple private constructor that initialize fields.
     */
    private BusinessCalendar() {
        this.businessDays = new HashMap<>(7);
        this.holidays = new HashSet<>(20);
    }

    /**
     * Obtains an empty instance of {@code BusinessCalendar}.
     *
     * @return the business calendar, empty, not null.
     */
    public static BusinessCalendar empty() {
        return new BusinessCalendar();
    }

    /**
     * Obtains an instance of {@code BusinessCalendar} from typical
     * business days.
     *
     * @param businessDays the business days to use, not null.
     * @return the business calendar, not null.
     */
    public static BusinessCalendar of(Set<BusinessDay> businessDays) {
        BusinessCalendar businessCalendar = new BusinessCalendar();
        businessCalendar.setBusinessDays(businessDays);
        return businessCalendar;
    }

    /**
     * Obtains an instance of {@code BusinessCalendar} from typical
     * business days and holidays.
     *
     * @param businessDays the business days to use, not null.
     * @param holidays     the holidays to use, not null.
     * @return the business calendar, not null.
     */
    public static BusinessCalendar of(Set<BusinessDay> businessDays,
                                      Set<LocalDate> holidays) {
        BusinessCalendar businessCalendar = new BusinessCalendar();
        businessCalendar.setBusinessDays(businessDays);
        businessCalendar.setHolidays(holidays);
        return businessCalendar;
    }

    /**
     * Get the duration getDuration two zonedDateTimes. This duration is
     * calculated from "working hours" (eg: Duration = real Duration - off
     * hours Duration - holidays Duration).
     *
     * @param startInclusive the start instant, inclusive, not null.
     * @param endExclusive   the end instant, exclusive, not null.
     * @return the duration getDuration two zonedDateTimes, not null, eventually
     * negative.
     */
    public Duration getDuration(ZonedDateTime startInclusive,
                                ZonedDateTime endExclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(endExclusive, "endExclusive");

        Duration duration = Duration.ZERO;
        boolean isNegated = false;

        //Negate duration if startInclusive is after endExclusive
        if (startInclusive.isAfter(endExclusive)) {
            ZonedDateTime temp = endExclusive;
            endExclusive = startInclusive;
            startInclusive = temp;
            isNegated = true;
        }

        long days =
                Duration.between(startInclusive.truncatedTo(ChronoUnit.DAYS),
                        endExclusive.truncatedTo(ChronoUnit.DAYS)).toDays();

        //if startInclusive and endExclusive are the same day
        if (days == 0) {
            //get the Duration getDuration these two parameters
            duration = getDailyDuration(startInclusive.toLocalTime(),
                    endExclusive.toLocalTime(), startInclusive);
        } else {
            //get the duration for the first day
            //eg : the Duration getDuration the startInclusive and its end of
            // the day
            duration = duration.plus(
                    getDailyDuration(startInclusive.toLocalTime(),
                            LocalTime.MAX, startInclusive));

            //add the Duration for all the days getDuration startInclusive and
            // endExclusive
            for (long i = 0; i + 1 < days; i++) {
                startInclusive = startInclusive.plusDays(1);
                duration = duration.plus(
                        getDailyDuration(LocalTime.MIN, LocalTime.MAX,
                                startInclusive));
            }

            //add the Duration for the last day
            //eg : the Duration getDuration the endExclusive begin of the day and
            // endExclusive
            duration = duration.plus(
                    getDailyDuration(LocalTime.MIN, endExclusive.toLocalTime(),
                            endExclusive));
        }

        if (isNegated) {
            return duration.negated();
        } else {
            return duration;
        }
    }

    /**
     * Get the duration getDuration two localTimes for a given day. This duration
     * is calculated from "working hours" (eg : Duration = Real Duration -
     * off hours - holidays).
     *
     * @param startInclusive the start instant, inclusive, not null.
     * @param endExclusive   the end instant, exclusive, not null.
     * @param dayConcerned   the given day, not null.
     * @return the duration getDuration two localTimes for a given day, not null.
     */
    private Duration getDailyDuration(LocalTime startInclusive,
                                      LocalTime endExclusive,
                                      ZonedDateTime dayConcerned) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(endExclusive, "endExclusive");
        Objects.requireNonNull(dayConcerned, "dayConcerned");

        //if the day the dayConcerned is not part of holidays
        if (!holidays.contains(dayConcerned.toLocalDate())) {

            BusinessDay businessDay =
                    businessDays.get(dayConcerned.getDayOfWeek());

            //if that day is part of businessDays, then return the duration
            // getDuration startInclusive and endExclusive for that given day
            if (businessDay != null) {
                return businessDay.getDuration(startInclusive, endExclusive);
            }

        }
        return Duration.ZERO;
    }

    /**
     * Check if a zonedDateTime is in "working hours".
     *
     * @param zonedDateTime the zonedDateTime to check, not null.
     * @return true if the zonedDateTime is in "working hours", false if not.
     */
    public Boolean isWorkingTime(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");

        if (!holidays.contains(zonedDateTime.toLocalDate())) {
            DayOfWeek dayOfWeek = DayOfWeek.from(zonedDateTime);
            BusinessDay businessDay = businessDays.get(dayOfWeek);

            if (businessDay != null) {
                if (businessDay.contains(zonedDateTime.toLocalTime())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get a <b>copy</b> of the calendar business days.
     *
     * @return the business days, not null.
     */
    public Set<BusinessDay> getBusinessDays() {
        return new HashSet<>(businessDays.values());
    }

    /**
     * Set the calendar business days.
     *
     * @param businessDays the business days to set, not null.
     * @throws BusinessCalendarSameBusinessDaysException if this Set contains
     *                                                   multiple
     *                                                   BusinessDays having
     *                                                   the same day of week.
     */
    public void setBusinessDays(Set<BusinessDay> businessDays) {
        Objects.requireNonNull(businessDays, "businessDays");

        this.businessDays.clear();
        businessDays.forEach(this::addBusinessDay);
    }

    /**
     * Add a business day to the calendar
     *
     * @param businessDay the business day to add, not null.
     * @throws BusinessCalendarSameBusinessDaysException if this day is
     *                                                   already in the
     *                                                   business days.
     */
    public void addBusinessDay(BusinessDay businessDay) {
        Objects.requireNonNull(businessDay, "businessDay");

        DayOfWeek dayOfWeek = businessDay.getDayOfWeek();
        if (businessDays.containsKey(dayOfWeek)) {
            throw new BusinessCalendarSameBusinessDaysException(dayOfWeek);
        } else {
            businessDays.put(dayOfWeek, businessDay);
        }
    }

    /**
     * Remove a business day from the calendar business days.
     *
     * @param businessDay the business day to remove, not null.
     */
    public void removeBusinessDay(BusinessDay businessDay) {
        Objects.requireNonNull(businessDay, "businessDay");

        businessDays.remove(businessDay.getDayOfWeek());
    }

    /**
     * Remove a business day corresponding to a day-of-week from the calendar
     * business days.
     *
     * @param dayOfWeek the day-of-week to remove, not null.
     */
    public void removeBusinessDayFromDayOfWeek(DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");

        businessDays.remove(dayOfWeek);
    }

    /**
     * Get the holidays from the calendar.
     *
     * @return the holidays, not null.
     */
    public Set<LocalDate> getHolidays() {
        return holidays;
    }

    /**
     * Set the calendar holidays.
     *
     * @param holidays the holidays to set, not null.
     */
    public void setHolidays(Set<LocalDate> holidays) {
        Objects.requireNonNull(holidays, "holidays");

        this.holidays = holidays;
    }

    /**
     * Add an holiday to the calendar holidays.
     *
     * @param holiday the holiday to add, not null.
     * @return true if this day has been added, false if not.
     */
    public boolean addHoliday(LocalDate holiday) {
        Objects.requireNonNull(holiday, "holiday");

        return this.holidays.add(holiday);
    }

    /**
     * Remove an holiday from the calendar holidays
     *
     * @param holiday the holiday to remove, not null.
     * @return true if this day has been removed, false if not.
     */
    public boolean removeHoliday(LocalDate holiday) {
        Objects.requireNonNull(holiday, "holiday");

        return this.holidays.remove(holiday);
    }
}

