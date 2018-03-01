package org.blackdog.linkguardian.report;

/**
 * abstract activity
 */
public abstract class AbstractActivity {

    private ActivityType type;

    public AbstractActivity(ActivityType type) {
        this.type = type;
    }

    public ActivityType getActivityType() {
        return type;
    }
}
