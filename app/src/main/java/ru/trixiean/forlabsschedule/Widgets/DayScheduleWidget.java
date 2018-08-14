package ru.trixiean.forlabsschedule.Widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Locale;

import ru.trixiean.forlabsschedule.R;

/**
 * Implementation of App Widget functionality.
 */
public class DayScheduleWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //region Вычисление даты
        Locale myLocale = new Locale("ru", "RU");
        Calendar mCalendar = Calendar.getInstance(myLocale);

        String mDayTitle = "";

        switch (mCalendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                mDayTitle = "Понедельник";
                break;
            case Calendar.TUESDAY:
                mDayTitle = "Вторник";
                break;
            case Calendar.WEDNESDAY:
                mDayTitle = "Среда";
                break;
            case Calendar.THURSDAY:
                mDayTitle = "Четверг";
                break;
            case Calendar.FRIDAY:
                mDayTitle = "Пятница";
                break;
            case Calendar.SATURDAY:
                mDayTitle = "Суббота";
                break;
            case Calendar.SUNDAY:
                mDayTitle = "Воскресенье";
                break;
        }

        mDayTitle += ", " + String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)) + " ";

        switch (mCalendar.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                mDayTitle += "января";
                break;
            case Calendar.FEBRUARY:
                mDayTitle += "февраля";
                break;
            case Calendar.MARCH:
                mDayTitle += "марта";
                break;
            case Calendar.APRIL:
                mDayTitle += "апреля";
                break;
            case Calendar.MAY:
                mDayTitle += "мая";
                break;
            case Calendar.JUNE:
                mDayTitle += "июня";
                break;
            case Calendar.JULY:
                mDayTitle += "июля";
                break;
            case Calendar.AUGUST:
                mDayTitle += "августа";
                break;
            case Calendar.SEPTEMBER:
                mDayTitle += "сентября";
                break;
            case Calendar.OCTOBER:
                mDayTitle += "октября";
                break;
            case Calendar.NOVEMBER:
                mDayTitle += "ноября";
                break;
            case Calendar.DECEMBER:
                mDayTitle += "декабря";
                break;
        }
        //endregion

        // Construct the RemoteViews object
        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.day_schedule_widget);

        // Instruct the widget manager to update the widget
        //appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, DayScheduleService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.day_schedule_widget);
            rv.setRemoteAdapter(R.id.DayWidgetListView, intent);
            rv.setEmptyView(R.id.DayWidgetListView, R.id.empty);

            //updateAppWidget(context, appWidgetManager, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

