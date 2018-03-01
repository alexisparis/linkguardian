package org.blackdog.linkguardian.report;

import java.time.temporal.TemporalAmount;

/**
 * represent a countable activity for a given type of activity during an amount of time
 */
public class ActivityDuringTime extends AbstractActivity {

    private Long count;

    private TemporalAmount duration;

    public ActivityDuringTime(ActivityType type, Long count, TemporalAmount duration) {
        super(type);
        this.count = count;
        this.duration = duration;
    }

    public Long getCount() {
        return count;
    }

    public TemporalAmount getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "ActivityDuringTime{" + "type=" + this.getActivityType() + ", count=" + count + ", duration=" + duration + '}';
    }
}
