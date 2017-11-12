package common;


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
        return "Received profile for user: " + profile.getName();
    }

}
