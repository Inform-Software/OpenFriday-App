package syncrotess.com.openfriday;

import android.app.Application;
import android.util.Log;

public class OpenfridayApplication extends Application {


    Workshop[] meineworks;
    Workshop workshop;
    Status status;
    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Workshop[] getMeineworks() {
        return meineworks;
    }

    public void setMeineworks(Workshop[] meineworks) {
        this.meineworks = meineworks;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        Log.i("Cleanup", status.getStatusId()+"");
        this.status = status;

    }
}
