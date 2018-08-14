package ru.trixiean.forlabsschedule;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ru.trixiean.forlabsschedule.Utils.AdapterGroups;
import ru.trixiean.forlabsschedule.Utils.Group;
import ru.trixiean.forlabsschedule.Utils.Saver;
import ru.trixiean.forlabsschedule.Utils.ThemeWrapper;

public class GroupSelectActivity extends AppCompatActivity {

    private static final String FORLABS_URL_GROUPS = "https://forlabs.ru/api/v1/rasp";
    private static final String TAG = GroupSelectActivity.class.getSimpleName();
    public static final String GROUP = "ru.trixiean.forlabsschedule.GROUP";

    private ArrayList<Group> mGroups = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Button mButton;
    private TextView mLoadingMessageView;
    private ImageView mLoadingImageView;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeWrapper.onActivityApplyTheme(this, false);
        setContentView(R.layout.app_bar_select_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AdapterGroups.setFragment(this);

        //Инициализация view элементов
        mButton = findViewById(R.id.SelectGroupButton);
        mLoadingImageView = findViewById(R.id.SelectGroupLoadingIcon);
        mLoadingMessageView = findViewById(R.id.SelectGroupLoadingMessage);
        mProgressBar = findViewById(R.id.SelectGroupProgressBar);
        mRecyclerView = findViewById(R.id.RecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDownload();
            }
        });

        onDownload();


    }

    private void onDownload() {
        if (!MyApp.getSingleTon().isOnline()) {
            showConnectionErr();
            return;
        }

        ListGroupsTask mTask = new ListGroupsTask(this);
        mTask.execute();
    }

    private void showConnectionErr() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingMessageView.setVisibility(View.VISIBLE);
        mLoadingImageView.setVisibility(View.VISIBLE);
        mButton.setVisibility(View.VISIBLE);

        mLoadingImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_connection_failed));
        mLoadingMessageView.setText("Не удается установить надежное соединение с сервером. Проверьте ваше подлючение к интернету.");
    }

    private void showLoadingProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingMessageView.setVisibility(View.VISIBLE);
        mLoadingImageView.setVisibility(View.VISIBLE);
        mButton.setVisibility(View.INVISIBLE);

        mLoadingImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_downloading));
        mLoadingMessageView.setText("Идет загрузка списка групп, пожалуйста подождите...");
    }

    private void showGroups() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingMessageView.setVisibility(View.INVISIBLE);
        mLoadingImageView.setVisibility(View.INVISIBLE);
        mButton.setVisibility(View.INVISIBLE);
    }

    /**
     *
     */
    public void onChooseGroup(Group group) {
        Intent success = new Intent();
        Saver.save("GroupNumber", group.getName());
        Saver.save("GroupScheduleLink", group.getLink());
        int id = Saver.get().getInt("GroupID", 0);
        Saver.save("GroupID", ++id);

        success.putExtra(GROUP, group.getName());
        setResult(RESULT_OK, success);
        this.finish();
    }


    private static class ListGroupsTask extends AsyncTask<Void, Void, JSONArray> {

        private WeakReference<GroupSelectActivity> mActivityReference;

        ListGroupsTask(GroupSelectActivity mActivityReference) {
            this.mActivityReference = new WeakReference<>(mActivityReference);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mActivityReference.get() != null || !mActivityReference.get().isFinishing())
                mActivityReference.get().showLoadingProgress();
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {
            HttpURLConnection mHttpURLConnection = null;
            try {

                URL mURL = new URL(FORLABS_URL_GROUPS);
                mHttpURLConnection = (HttpURLConnection) mURL.openConnection();
                mHttpURLConnection.setRequestMethod("GET");
                mHttpURLConnection.connect();

                //Получаем
                InputStream mInputStream;

                int Status = mHttpURLConnection.getResponseCode();

                Log.e(TAG, "Status response code: " + String.valueOf(Status));

                if (Status >= HttpURLConnection.HTTP_BAD_REQUEST)
                    mInputStream = mHttpURLConnection.getErrorStream();
                else
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
                Log.e(TAG, mResponse.toString());
                return new JSONArray(String.valueOf(mResponse));
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
        protected void onPostExecute(JSONArray JsonArray) {
            super.onPostExecute(JsonArray);
            if (mActivityReference == null)
                return;
            GroupSelectActivity mActivity = mActivityReference.get();
            if (mActivity == null || mActivity.isFinishing())
                return;
            try {
                final int TOTAL_COURSES = 4; //Кол-во курсов, которое может передать сервер
                for (int i = 0; i < TOTAL_COURSES; i++) {
                    String CourseName = JsonArray.getJSONObject(i).getString("course");
                    JSONArray Streams = JsonArray.getJSONObject(i).getJSONArray("streams");
                    mActivity.mGroups.add(new Group(CourseName));
                    for (int j = 0; j < Streams.length(); j++) {
                        mActivity.mGroups.add(new Group(
                                Streams.getJSONObject(j).getString("code"),
                                Streams.getJSONObject(j).getString("link")
                        ));
                    }
                }
                AdapterGroups mAdapterGroups = new AdapterGroups(mActivity.mGroups);
                mActivity.mRecyclerView.setAdapter(mAdapterGroups);
                mAdapterGroups.notifyDataSetChanged();
                mActivity.showGroups();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
