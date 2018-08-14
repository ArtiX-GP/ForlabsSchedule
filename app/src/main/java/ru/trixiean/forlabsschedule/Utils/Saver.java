package ru.trixiean.forlabsschedule.Utils;

import android.content.SharedPreferences;
import android.os.Build;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Trixiean on 06.05.2017.
 * https://play.google.com/store/apps/developer?id=Trixiean
 */

@SuppressWarnings("SameParameterValue")
public class Saver {

    private static SharedPreferences mSharedPreferences;

    public static void set(SharedPreferences s) {
        mSharedPreferences = s;
    }

    public static SharedPreferences get() {
        return mSharedPreferences;
    }

    public static void save(String key, String value) {
        SharedPreferences.Editor localEditor;
        if (mSharedPreferences != null) {
            localEditor = mSharedPreferences.edit();
            localEditor.putString(key, value);
            if (Build.VERSION.SDK_INT >= 16) {
                localEditor.apply();
            }
        } else {
            return;
        }
        localEditor.commit();
    }

    public static void save(String key, int value) {
        SharedPreferences.Editor localEditor;
        if (mSharedPreferences != null) {
            localEditor = mSharedPreferences.edit();
            localEditor.putInt(key, value);
            if (Build.VERSION.SDK_INT >= 16) {
                localEditor.apply();
            }
        } else {
            return;
        }
        localEditor.commit();
    }


    public static void save(String key, long value) {
        SharedPreferences.Editor localEditor;
        if (mSharedPreferences != null) {
            localEditor = mSharedPreferences.edit();
            localEditor.putLong(key, value);
            if (Build.VERSION.SDK_INT >= 16) {
                localEditor.apply();
            }
        } else {
            return;
        }
        localEditor.commit();
    }

    public static void save(String key, boolean value) {
        SharedPreferences.Editor localEditor;
        if (mSharedPreferences != null) {
            localEditor = mSharedPreferences.edit();
            localEditor.putBoolean(key, value);
            if (Build.VERSION.SDK_INT >= 16) {
                localEditor.apply();
            }
        } else {
            return;
        }
        localEditor.commit();
    }

    public static void remove(String key) {
        SharedPreferences.Editor localEditor;
        if (mSharedPreferences != null) {
            localEditor = mSharedPreferences.edit();
            localEditor.remove(key);
            if (Build.VERSION.SDK_INT >= 16) {
                localEditor.apply();
            }
        } else {
            return;
        }
        localEditor.commit();
    }

    public static SparseArray<ArrayList<Lesson>> getScheduleFromCache() throws JSONException {
        String mCache = Saver.get().getString("ScheduleJSON", "");
        if (mCache.isEmpty())
            return null;

        JSONObject jsonObject = new JSONObject(mCache);
        SparseArray<ArrayList<Lesson>> mSchedule = new SparseArray<>();

        //Часы занятий, которые есть в вузе.
        final String[] mTimes = new String[]{
                "08.30-10.00",
                "10.10-11.40",
                "11.50-13.20",
                "13.50-15.20",
                "15.30-17.00",
                "17.10-18.40",
                "18.50-19.20",
        };

        // Расписание на 14 дней.
        for (int i = 1; i <= 14; i++) {
            // Так настроен forlabs, что если пар нет, то он вернет пустой массив.
            if (jsonObject.get("day" + i) instanceof JSONArray)
                continue;

            //Составляем расписание на день
            JSONObject mDay = jsonObject.getJSONObject("day" + i);

            // Занятия, которые будут в эти сутки.
            ArrayList<Lesson> mLessons = new ArrayList<>(7);
            // Максимум - 7 пар за день.
            for (int j = 0; j < 7; j++) {
                // Существует ли пара на назначенное время?
                if (mDay.has(mTimes[j])) {
                    // Если существует, то добавляем ее.
                    mLessons.add(new Lesson(j,
                            mDay.getJSONObject(mTimes[j]).getString("study"),
                            mDay.getJSONObject(mTimes[j]).getString("lecturer"),
                            mDay.getJSONObject(mTimes[j]).getString("room")));
                } else {
                    // Если пары на назначенное время нет, то ставим "заглушку".
                    mLessons.add(new Lesson(j));
                }
            }
            mSchedule.put(i, mLessons);
        }

        return mSchedule;
    }

    public static int getStartDay() {
        // Узнаем номер недели.
        Calendar c = Calendar.getInstance();
        int mNumberOfWeek = c.get(Calendar.WEEK_OF_YEAR);
        int mStartDay = 1;

        switch (c.get(Calendar.DAY_OF_WEEK)) {
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
        return mStartDay;
    }
}
