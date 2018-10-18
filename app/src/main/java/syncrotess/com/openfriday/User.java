package syncrotess.com.openfriday;


import java.util.HashMap;

public class User {
    Long id;
    String name;
    boolean admin = false;
    Long[] workids;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Long[] getWorkids() {
        return workids;
    }

    public void setWorkids(Long[] workids) {
        this.workids = workids;
    }
}

