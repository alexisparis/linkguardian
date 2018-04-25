package org.blackdog.linkguardian.service.dto;

public class YesNoResponse {

    private boolean state;

    private YesNoResponse(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public static YesNoResponse withState(boolean state) {
        YesNoResponse response = new YesNoResponse(state);
        return response;
    }
}
