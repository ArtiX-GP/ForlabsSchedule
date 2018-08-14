package ru.trixiean.forlabsschedule.Fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ru.trixiean.forlabsschedule.R;
import ru.trixiean.forlabsschedule.StartActivity;
import ru.trixiean.forlabsschedule.Utils.Lesson;
import ru.trixiean.forlabsschedule.Utils.Saver;

/**
 * Если этот код работает, то его написал Никита Громик, если нет,
 * то не знаю кто его писал. (05.02.2018)
 */

public class DownloadScheduleFragment extends Fragment {

    private static final String TAG = "DownloadSchedule";

    public TextView mStatusDownloadingTextView;
    public ImageView mIconDownloadingImageView;
    public ProgressBar mProgressBar;

    private static WeakReference<StartActivity> mActivityReference;

    public static void setActivityReference(StartActivity mActivity) {
        mActivityReference = new WeakReference<>(mActivity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        onDownload();
    }

    private void onDownload() {
        String mSchedule = Saver.get().getString("ScheduleJSON", "");
        // Если сохраненного расписания нет, то придется загружать.
        if (mSchedule.isEmpty()) {
            DownloadScheduleTask mTask = new DownloadScheduleTask(this);
            String link = Saver.get().getString("GroupScheduleLink", "");
            Log.i(TAG, link);
            mTask.execute(link);
        } else
            try {
                parse(new JSONObject(mSchedule));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View mContainer = inflater.inflate(R.layout.fragment_download_schedule, container, false);
        mStatusDownloadingTextView = mContainer.findViewById(R.id.DownloadingScheduleLoadingMessage);
        mIconDownloadingImageView = mContainer.findViewById(R.id.DownloadingScheduleLoadingIcon);
        mProgressBar = mContainer.findViewById(R.id.DownloadingScheduleProgressBar);

        return mContainer;
    }

    /**
     * Разбор JSON-объекта и превращение его в читаемое расписание.
     *
     * @param jsonObject То, что мы загрузили из forlabs.
     */
    public void parse(JSONObject jsonObject) throws JSONException {
        Log.i(TAG, "Начало парсинга");
        SparseArray<ArrayList<Lesson>> mSchedule = new SparseArray<>();
        if (jsonObject != null && (mActivityReference.get() != null || !mActivityReference.get().isFinishing())) {
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
                // Занятия, которые будут в эти сутки.
                ArrayList<Lesson> mLessons = new ArrayList<>(7);

                // Так настроен forlabs, что если пар нет, то он вернет пустой массив.
                if (jsonObject.get("day" + i) instanceof JSONArray) {
                    for (int j = 0; j < 7; j++)
                        mLessons.add(new Lesson(j));
                    mSchedule.put(i, mLessons);
                    continue;
                }

                //Составляем расписание на день
                JSONObject mDay = jsonObject.getJSONObject("day" + i);

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
            // Расписание составлено, отправляем его в главную Activity.
            mActivityReference.get().setSchedule(mSchedule);
        }
    }

    private static class DownloadScheduleTask extends AsyncTask<String, Integer, JSONObject> {

        WeakReference<DownloadScheduleFragment> mFragmentReference;

        DownloadScheduleTask(DownloadScheduleFragment mFragment) {
            this.mFragmentReference = new WeakReference<>(mFragment);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            HttpURLConnection mHttpURLConnection = null;
            try {

                URL mURL = new URL(strings[0]);
                mHttpURLConnection = (HttpURLConnection) mURL.openConnection();
                mHttpURLConnection.setRequestMethod("GET");
                mHttpURLConnection.connect();

                //Получаем
                InputStream mInputStream;

                int Status = mHttpURLConnection.getResponseCode();

                if (Status >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    return null;
                } else
                    mInputStream = mHttpURLConnection.getInputStream();

                BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
                StringBuilder mResponse = new StringBuilder();
                String line;
                while ((line = mBufferedReader.readLine()) != null) {
                    mResponse.append(line);
                    mResponse.append('\n');
                }
                mBufferedReader.close();

                //Обрабатываем JSON строку
                return new JSONObject(String.valueOf(mResponse));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (mHttpURLConnection != null)
                    mHttpURLConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject == null) {
                if (mFragmentReference.get() != null) {
                    mFragmentReference.get().mProgressBar.setVisibility(View.INVISIBLE);
                    mFragmentReference.get().mStatusDownloadingTextView.setText(
                            "Произошла непредвиденная ошибка, не исключено, что ошибка могла быть на стороне сервера. Попробуйте повторить загрузить расписание позже...");
                    mFragmentReference.get().mIconDownloadingImageView.setImageResource(R.drawable.ic_connection_failed);
                }
            } else {
                if (mFragmentReference.get() != null)
                    try {
                        mFragmentReference.get().parse(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                // Сохраняем расписание, чтобы больше не загружать его без необходимости.
                Saver.save("ScheduleJSON", jsonObject.toString());
            }
        }
    }

}
