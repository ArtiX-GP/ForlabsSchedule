package ru.trixiean.forlabsschedule.Widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ru.trixiean.forlabsschedule.R;
import ru.trixiean.forlabsschedule.Utils.Lesson;
import ru.trixiean.forlabsschedule.Utils.Saver;

/**
 * Если этот код работает, то его написал Никита Громик, если нет,
 * то не знаю кто его писал. (18.02.2018)
 */

public class DayScheduleDataFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "Widget";
    // Занятия на сегодняшний день.
    private ArrayList<Lesson> mSchedule;

    private Context mContext;

    private int mWidgetID;

    public DayScheduleDataFactory(Context context, Intent intent) {
        this.mContext = context;
        mWidgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        mSchedule = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {
        // Получаем расписание на сегодня.
        Locale myLocale = new Locale("ru", "RU");
        Calendar mCalendar = Calendar.getInstance(myLocale);
        int mNumberOfWeek = mCalendar.get(Calendar.WEEK_OF_YEAR);
        int mStartDay = 1;

        switch (mCalendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                mStartDay = 1;
                break;
            case Calendar.TUESDAY:
                mStartDay = 2;
                break;
            case Calendar.WEDNESDAY:
                mStartDay = 3;
                break;
            case Calendar.THURSDAY:
                mStartDay = 4;
                break;
            case Calendar.FRIDAY:
                mStartDay = 5;
                break;
            case Calendar.SATURDAY:
                mStartDay = 6;
                break;
            case Calendar.SUNDAY:
                mStartDay = 7;
                break;
        }

        // Если нечетная неделя является нижней неделей (+7 к стартовому дню).
        if (Saver.get().getBoolean("OddBottom", false)) {
            // Поэтому, если сейчас нечетная неделя, то бустим стартовый день.
            if (mNumberOfWeek % 2 != 0)
                mStartDay += 7;
        } else if (mNumberOfWeek % 2 == 0)
            // Если четная неделя оказалась нижней, то проверяем текущую неделю на четность
            // и если та оказывается четной, то бустим стартовый день.
            mStartDay += 7;

        try {
            SparseArray<ArrayList<Lesson>> mCache = Saver.getScheduleFromCache();
            if (mCache != null) {
                mSchedule = mCache.get(mStartDay);
                Log.i(TAG, "Schedule loaded!");
            } else throw new JSONException("Can't load schedule from cache.");
        } catch (JSONException e) {
            Log.e(TAG, "Can't load schedule.");
            mSchedule = null;
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mSchedule == null)
            return 0;
        return mSchedule.size();
    }


    @Override
    public RemoteViews getViewAt(int position) {
        if (mSchedule.get(position).getTitle() != null && !mSchedule.get(position).getTitle().isEmpty()) {
            RemoteViews mItemView = new RemoteViews(mContext.getPackageName(),
                    R.layout.item_lesson_widget);
            mItemView.setTextViewText(R.id.ItemTitleText, mSchedule.get(position).getTitle() + " (" + mSchedule.get(position).getAudience() + ")");
            mItemView.setTextViewText(R.id.ItemTimeText, getTimeByPosition(mSchedule.get(position).getOrderInCategory()));
            mItemView.setTextViewText(R.id.ItemOrderText, String.valueOf(mSchedule.get(position).getOrderInCategory() + 1));
            return mItemView;
        } else {
            return new RemoteViews(mContext.getPackageName(), R.layout.item_just_empty);
        }
    }

    private String getTimeByPosition(int orderInCategory) {
        switch (orderInCategory) {
            case 0:
                return "08:30";
            case 1:
                return "10:10";
            case 2:
                return "11:50";
            case 3:
                return "13:50";
            case 4:
                return "15:30";
            case 5:
                return "17:00";
            case 6:
                return "18:50";
        }
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
//        return new RemoteViews(mContext.getPackageName(), R.layout.item_empty_lesson);
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
