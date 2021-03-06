package org.rhq.cassandra.schema;

import java.util.Properties;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Period;

/**
 * For RHQ 4.9 - 4.11 installations this migrates data from the metrics_index table into the new metrics_idx table. For
 * 4.12 installations, it migrates data from metrics_cache_index into metrics_idx. The old index tables are deleted
 * after successfully migrating data.
 *
 * @author John Sanda
 */
public class ReplaceIndex implements Step {

    private static final Log log = LogFactory.getLog(ReplaceIndex.class);

    private Session session;

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public void bind(Properties properties) {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void execute() {
        DateRanges dateRanges = new DateRanges();
        dateRanges.rawEndTime = DateTime.now().hourOfDay().roundFloorCopy();
        dateRanges.rawStartTime = dateRanges.rawEndTime.minusDays(3);
        dateRanges.oneHourStartTime = getTimeSlice(dateRanges.rawStartTime, Hours.SIX.toStandardDuration());
        dateRanges.oneHourEndTime = getTimeSlice(dateRanges.rawEndTime, Hours.SIX.toStandardDuration());
        dateRanges.sixHourStartTime = getTimeSlice(dateRanges.rawStartTime, Days.ONE.toStandardDuration());
        dateRanges.sixHourEndTime = getTimeSlice(dateRanges.rawEndTime, Days.ONE.toStandardDuration());

        if (cacheIndexExists()) {
            log.info("Preparing to replace metrics_cache_index");
            new Replace412Index(session).execute(dateRanges);
        } else {
            log.info("Preparing to replace metrics_index");
            new ReplaceRHQ411Index(session).execute(dateRanges);
        }
    }

    protected static DateTime getTimeSlice(DateTime dt, Duration duration) {
        Period p = duration.toPeriod();

        if (p.getYears() != 0) {
            return dt.yearOfEra().roundFloorCopy().minusYears(dt.getYearOfEra() % p.getYears());
        } else if (p.getMonths() != 0) {
            return dt.monthOfYear().roundFloorCopy().minusMonths((dt.getMonthOfYear() - 1) % p.getMonths());
        } else if (p.getWeeks() != 0) {
            return dt.weekOfWeekyear().roundFloorCopy().minusWeeks((dt.getWeekOfWeekyear() - 1) % p.getWeeks());
        } else if (p.getDays() != 0) {
            return dt.dayOfMonth().roundFloorCopy().minusDays((dt.getDayOfMonth() - 1) % p.getDays());
        } else if (p.getHours() != 0) {
            return dt.hourOfDay().roundFloorCopy().minusHours(dt.getHourOfDay() % p.getHours());
        } else if (p.getMinutes() != 0) {
            return dt.minuteOfHour().roundFloorCopy().minusMinutes(dt.getMinuteOfHour() % p.getMinutes());
        } else if (p.getSeconds() != 0) {
            return dt.secondOfMinute().roundFloorCopy().minusSeconds(dt.getSecondOfMinute() % p.getSeconds());
        }
        return dt.millisOfSecond().roundCeilingCopy().minusMillis(dt.getMillisOfSecond() % p.getMillis());
    }

    protected static DateTime getUTCTimeSlice(DateTime dateTime, Duration duration) {
        return getTimeSlice(new DateTime(dateTime.getMillis(), DateTimeZone.UTC), duration);
    }

    protected static DateTime plusDSTAware(DateTime dateTime, Duration duration) {
        //(BZ 1161806) Added code to adjust to the shifts in time due to
        // changes from DST to non-DST and the reverse.
        //
        // 1) When switching from DST to non-DST, the time after the
        // duration increment needs to be adjusted by a positive
        // one hour
        //
        // 2) When switching from non-DST to DST, the time after the
        // duration increment needs to be adjusted by a negative
        // one hour
        //
        // Note: this does not work if the duration is exactly one
        // hour because it will create an infinite loop when switching
        // from non-DST to DST times.

        if (duration.toPeriod().getHours() <= 1) {
            dateTime = dateTime.plus(duration);
        } else {
            DateTimeZone zone = dateTime.getZone();
            int beforeOffset = zone.getOffset(dateTime.getMillis());
            dateTime = dateTime.plus(duration);
            int afterOffset = zone.getOffset(dateTime.getMillis());
            dateTime = dateTime.plus(beforeOffset - afterOffset);
        }

        return dateTime;
    }

    private boolean cacheIndexExists() {
        ResultSet resultSet = session.execute("SELECT columnfamily_name FROM system.schema_columnfamilies " +
            "WHERE keyspace_name = 'rhq' AND columnfamily_name = 'metrics_cache_index'");
        return !resultSet.isExhausted();
    }

}
