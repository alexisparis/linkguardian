package org.blackdog.linkguardian.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * compile activities per user
 */
public class ActivityReport {

    private Map<String, List<AbstractActivity>> activitiesPerLogin = new HashMap<>();

    public void reportActivity(String login, AbstractActivity activityDuringTime) {
        if (login != null && activityDuringTime != null) {
            List<AbstractActivity> activities = this.activitiesPerLogin.get(login);
            if (activities == null) {
                activities = new ArrayList<>();
                this.activitiesPerLogin.put(login, activities);
            }
            activities.add(activityDuringTime);
        }
    }

    public Iterable<AbstractActivity> activityForUser(String login) {
        Iterable<AbstractActivity> result = this.activitiesPerLogin.get(login);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    public Iterable<String> logins() {
        return this.activitiesPerLogin.keySet();
    }

    public Map<String, List<AbstractActivity>> activitiesPerLogin() {
        return Collections.unmodifiableMap(activitiesPerLogin);
    }
}
