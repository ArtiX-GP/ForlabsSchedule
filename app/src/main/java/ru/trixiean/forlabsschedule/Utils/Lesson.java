package ru.trixiean.forlabsschedule.Utils;

/**
 * Created by Trixiean on 07.05.2017.
 * https://play.google.com/store/apps/developer?id=Trixiean
 */

public class Lesson {

    private int OrderInCategory;
    private String Title;
    private String TeacherName;
    private String Audience;

    public Lesson(int mOrderInCategory, String title, String teacherName, String audience) {
        this.OrderInCategory = mOrderInCategory;
        Title = title.trim();
        TeacherName = teacherName.trim();
        Audience = audience.trim();
    }

    public Lesson(int order) {
        this.OrderInCategory = order;
        this.Title = null;
    }

    public int getOrderInCategory() {
        return OrderInCategory;
    }

    public String getTitle() {
        return Title;
    }

    public String getTeacherName() {
        return TeacherName;
    }

    public String getAudience() {
        return Audience;
    }

    @Override
    public String toString() {
        if (Title == null) {
            return OrderInCategory + " Нет пары!";
        }
        return OrderInCategory + " " + Title + " | " + TeacherName + " (" + Audience + ")";
    }
}
