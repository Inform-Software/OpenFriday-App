package syncrotess.com.openfriday;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Workshop {
    private Long id;
    private Long persid;
    private int       countvotes=0;
    private String thema;
    private String name;
    private String beschreibung;
    private int session;
    private boolean[] notime={true,true,true};
    private String raum;
    boolean selected = false;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersid() {
        return persid;
    }

    public void setPersid(Long persid) {
        this.persid = persid;
    }

    public String getThema() {
        return thema;
    }

    public void setThema(String thema) {
        this.thema = thema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public String getRaum() {
        return raum;
    }

    public void setRaum(String raum) {
        this.raum = raum;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean[] getNotime() {
        return notime;
    }

    public void setNotime(boolean[] notime) {
        this.notime = notime;
    }

    public int getCountvotes() {
        return countvotes;
    }

    public void setCountvotes(int countvotes) {
        this.countvotes = countvotes;
    }
}

