package eu.brengard.businesscalendar.entitites;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A business day, defined by a day-of-week and "working" time slots.
 * <p>
 * This class provides methods to create and handle a business day.
 * This includes a method to get the {@link Duration} getDuration two
 * {@link LocalTime}s by counting only the time where the company is open.
 * <p>
 * A business day is constituted of two objects :
 * <ul>
 * <li>
 * A {@link DayOfWeek} which is the day-of-week for that business day.
 * </li>
 * <li>
 * A collection of {@link BusinessTimeSlot} defining the business day
 * time slots.
 * It is <b>important</b> to know that defining two time slots crossing (eg:
 * 9h - 12h , 10h - 12h) will result in a <b>pondered</b> duration on the
 * crossing times. (eg: if we take again the two time slots 9h - 12h , 10h -
 * 12h ; the duration getDuration 11h and 12h using
 * {@link #getDuration(LocalTime, LocalTime)} method will return <b>2 hours</b>.
 * </li>
 * </ul>
 *
 * @author Nicolas BRENGARD (eu.brengard.businesscalendar.entitites@brengard.eu)
 * @see java.time
 * @since 1.8
 */
public class BusinessDay implements Serializable {

    /**
     * Business day day-of-week.
     */
    private DayOfWeek dayOfWeek;

    /**
     * Business day time slots.
     */
    private Set<BusinessTimeSlot> businessTimeSlots;

    /**
     * Simple private constructor that initialize empty time slots from the
     * given day-of-week.
     *
     * @param dayOfWeek the business day day-of-week, not null.
     */
    private BusinessDay(DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");

        this.dayOfWeek = dayOfWeek;
        this.businessTimeSlots = new HashSet<>();
    }

    /**
     * Obtains an instance of {@code BusinessCalendar} from a day-of-week.
     *
     * @param dayOfWeek the day-of-week to use, not null.
     * @return the business day, not null.
     */
    public static BusinessDay of(DayOfWeek dayOfWeek) {
        return new BusinessDay(dayOfWeek);
    }

    /**
     * Obtains an instance of {@code BusinessCalendar} from a day-of-week and
     * time slots.
     *
     * @param dayOfWeek         the day-of-week to use, not null.
     * @param businessTimeSlots the time slots, not null.
     * @return the business day, not null.
     */
    public static BusinessDay of(DayOfWeek dayOfWeek,
                                 Set<BusinessTimeSlot> businessTimeSlots) {
        BusinessDay businessDay = new BusinessDay(dayOfWeek);
        businessDay.setBusinessTimeSlots(businessTimeSlots);
        return businessDay;
    }

    /**
     * Get the duration getDuration two localTimes. This duration is calculated
     * from "working hours" (eg: Duration = real Duration - day-of-week off
     * hours Duration).
     * It is <b>important</b> to know that if this business day contains
     * two time slots crossing (eg: 9h - 12h , 10h - 12h) this method will
     * return a <b>pondered</b> duration on the crossing times. (eg: if we
     * take again the two time slots 9h - 12h , 10h - 12h ; the duration
     * getDuration 11h and 12h will be <b>2 hours</b>.
     *
     * @param startInclusive the start instant, inclusive, not null.
     * @param endExclusive   the end instant, exclusive, not null.
     * @return the duration getDuration two localTimes, not null, eventually
     * pondered, eventually negative.
     */
    public Duration getDuration(LocalTime startInclusive,
                                LocalTime endExclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(endExclusive, "endExclusive");

        Duration duration = Duration.ZERO;
        for (BusinessTimeSlot businessTimeSlot : businessTimeSlots) {
            duration = duration.plus(
                    businessTimeSlot.getDuration(startInclusive, endExclusive));
        }
        return duration;
    }

    /**
     * Check if this business day is a working day (eg: it has no time slots).
     *
     * @return true if this business day is a working day, false if not.
     */
    public boolean isWorkingDay() {
        return !businessTimeSlots.isEmpty();
    }

    /**
     * Check if a localTime is part of the time slots.
     *
     * @param localTime the localTime to check, not null.
     * @return true if it is part of the time slots, false if not.
     */
    public boolean contains(LocalTime localTime) {
        Objects.requireNonNull(localTime, "localTime");

        boolean isWorkingTime = false;
        if (isWorkingDay()) {
            for (BusinessTimeSlot businessTimeSlot : businessTimeSlots) {
                if (businessTimeSlot.contains(localTime)) {
                    isWorkingTime = true;
                }
            }
        }
        return isWorkingTime;
    }

    /**
     * Get the business day day-of-week.
     *
     * @return the business day day-of-week, not null.
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Set the business day day-of-week.
     *
     * @param dayOfWeek the day-of-week to set, not null.
     */
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");

        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Get the business day time slots.
     *
     * @return the business day time slots, not null.
     */
    public Set<BusinessTimeSlot> getBusinessTimeSlots() {
        return businessTimeSlots;
    }

    /**
     * Set the business day time slots.
     *
     * @param businessTimeSlots the time slots to set, not null.
     */
    public void setBusinessTimeSlots(Set<BusinessTimeSlot> businessTimeSlots) {
        Objects.requireNonNull(businessTimeSlots, "businessTimeSlots");

        this.businessTimeSlots = businessTimeSlots;
    }

    /**
     * Check if two business days are equals.
     *
     * @param o the second business day.
     * @return true if the two business days have the same day-of-week and
     * the sames time slots.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BusinessDay that = (BusinessDay) o;
        return dayOfWeek == that.dayOfWeek && businessTimeSlots
                .equals(that.businessTimeSlots);
    }

    /**
     * Get hashCode from the business day day-of-week and time slots.
     *
     * @return the business day hashcode.
     */
    @Override
    public int hashCode() {
        int result = dayOfWeek.hashCode();
        result = 31 * result + businessTimeSlots.hashCode();
        return result;
    }
}

