package com.syncrotess.openfriday;

public class Status {

    private String[] räume = {"R33", "R55", "R77"};
    private int statusId = 0;
    private String kommentar = "Not changed";

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public String[] getRäume() {
        return räume;
    }

    public void setRäume(String[] räume) {
        this.räume = räume;
    }
}
