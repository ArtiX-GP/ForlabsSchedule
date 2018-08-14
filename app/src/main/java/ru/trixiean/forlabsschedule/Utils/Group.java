package ru.trixiean.forlabsschedule.Utils;

/**
 * Created by Trixiean on 06.05.2017.
 * https://play.google.com/store/apps/developer?id=Trixiean
 */

public class Group {

    private String Link;
    private String Name;
    private boolean isHeader;

    public Group(String name, String link) {
        this.Link = link;
        this.Name = name;
    }

    public Group(String name) {
        this.Name = name;
        this.isHeader = true;
    }

    public String getLink() {
        return Link;
    }

    public String getName() {
        return Name;
    }

    public boolean isHeader() {
        return isHeader;
    }

    @Override
    public String toString() {
        if (isHeader)
            return "Header: " + Name;
        else
            return Name + " (" + Link + ")";
    }
}
