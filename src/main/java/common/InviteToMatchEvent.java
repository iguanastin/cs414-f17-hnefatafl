package common;


public class InviteToMatchEvent extends Event {

    private final String name;


    public InviteToMatchEvent(String target) {
        this.name = target;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Inviting user to match: " + getName();
    }

}
