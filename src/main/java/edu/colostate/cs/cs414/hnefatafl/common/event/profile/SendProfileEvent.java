package edu.colostate.cs.cs414.hnefatafl.common.event.profile;


import edu.colostate.cs.cs414.hnefatafl.common.Event;
import edu.colostate.cs.cs414.hnefatafl.common.Profile;

public class SendProfileEvent extends Event {

    private final Profile profile;


    public SendProfileEvent(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    @Override
    public String toString() {
        return "Received profile for user: " + profile.getId();
    }

}
