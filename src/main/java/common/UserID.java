package common;

import java.io.Serializable;
import java.util.Objects;


public class UserID implements Serializable {

    private final int id;
    private final String name;


    public UserID(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getId() + ":" + getName();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UserID && ((UserID) obj).getId() == getId() && Objects.equals(((UserID) obj).getName(), getName());
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
