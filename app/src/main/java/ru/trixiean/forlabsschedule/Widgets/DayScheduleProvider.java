package ru.trixiean.forlabsschedule.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

import ru.trixiean.forlabsschedule.R;

/**
 * Если этот код работает, то его написал Никита Громик, если нет,
 * то не знаю кто его писал. (06.08.2018)
 */
public class DayScheduleProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds)
            updateWidget(context, appWidgetManager, i);
    }

    void updateWidget(Context context, AppWidgetManager appWidgetManager,
                      int appWidgetId) {
        Log.i("Widget", "Updated!");
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.day_schedule_widget);

        rv.setImageViewBitmap(R.id.DayWidgetSeason, BitmapFactory.decodeResource(context.getResources(), getSeasonPicture()));

        setTextView(rv, context, appWidgetId);
        setDate(rv, context, appWidgetId);

        setList(rv, context, appWidgetId);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
                R.id.DayWidgetListView);
    }

    private void setDate(RemoteViews rv, Context context, int appWidgetId) {
        String date = "";
        Calendar c = Calendar.getInstance();
        date += c.get(Calendar.DAY_OF_MONTH);
        date += " ";
        switch (c.get(Calendar.MONTH)) {
            case 0:
                date += "января";
                break;
            case 1:
                date += "февраля";
                break;
            case 2:
                date += "марта";
                break;
            case 3:
                date += "апреля";
                break;
            case 4:
                date += "мая";
                break;
            case 5:
                date += "июня";
                break;
            case 6:
                date += "июля";
                break;
            case 7:
                date += "августа";
                break;
            case 8:
                date += "сентября";
                break;
            case 9:
                date += "октября";
                break;
            case 10:
                date += "ноября";
                break;
            case 11:
                date += "декабря";
                break;
        }

        rv.setImageViewBitmap(R.id.DayWidgetCurrentDate, convertToImg(date, context, 75, 0, 75));
    }

    void setList(RemoteViews rv, Context context, int appWidgetId) {
        Intent adapter = new Intent(context, DayScheduleService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        rv.setRemoteAdapter(R.id.DayWidgetListView, adapter);
        rv.setEmptyView(R.id.DayWidgetListView, R.id.empty);
    }

    void setTextView(RemoteViews rv, Context context, int appWidgetId) {
        rv.setImageViewBitmap(R.id.DayWidgetTapToUpdate, convertToImg("КОСНИТЕСЬ, ЧТОБЫ ОБНОВИТЬ", context, 20, 0, 75));
        Intent updIntent = new Intent(context, DayScheduleProvider.class);
        updIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[]{appWidgetId});
        PendingIntent updPIntent = PendingIntent.getBroadcast(context,
                appWidgetId, updIntent, 0);
        rv.setOnClickPendingIntent(R.id.DayWidgetTapToUpdate, updPIntent);
        rv.setOnClickPendingIntent(R.id.DayWidgetSeason, updPIntent);
    }

    private int getSeasonPicture() {
        Calendar c = Calendar.getInstance();
        switch (c.get(Calendar.MONTH)) {
            case 0:
                return R.drawable.january;
            case 1:
                return R.drawable.february;
            case 2:
                return R.drawable.march;
            case 3:
                return R.drawable.april;
            case 4:
                return R.drawable.may;
            case 5:
                return R.drawable.june;
            case 6:
                return R.drawable.jule;
            case 7:
                return R.drawable.august;
            case 8:
                return R.drawable.september;
            case 9:
                return R.drawable.october;
            case 10:
                return R.drawable.november;
            case 11:
                return R.drawable.december;
            default:
                return R.drawable.september;
        }
    }

    /**
     * Конвертация текста в картинку.
     *
     * @param text
     * @param context
     * @return
     */
    private Bitmap convertToImg(String text, Context context, int size, int x, int y) {
        Bitmap btmText = Bitmap.createBitmap(450, 100, Bitmap.Config.ARGB_4444);
        Canvas cnvText = new Canvas(btmText);

        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/ComfortaaRegular.ttf");

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(tf);
        paint.setColor(Color.WHITE);
        paint.setTextSize(size);

        cnvText.drawText(text, x, y, paint);
        return btmText;
    }

}
