package ru.trixiean.forlabsschedule;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import ru.trixiean.forlabsschedule.Fragments.Day.DayFragment;
import ru.trixiean.forlabsschedule.Fragments.Day.DayManagerFragment;
import ru.trixiean.forlabsschedule.Fragments.DownloadScheduleFragment;
import ru.trixiean.forlabsschedule.Fragments.Tape.TapeFragment;
import ru.trixiean.forlabsschedule.Utils.Lesson;
import ru.trixiean.forlabsschedule.Utils.Saver;
import ru.trixiean.forlabsschedule.Utils.ThemeWrapper;

public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_SELECT_GROUP = 1001;
    private static final String TAG = "StartActivity";

    private TextView mToolbarTitleView, mSubtitleHeaderDrawer;

    private static SparseArray<ArrayList<Lesson>> mSchedule;

    public SparseArray<ArrayList<Lesson>> getSchedule() {
        return mSchedule;
    }

    /**
     * Установка нового расписания.
     *
     * @param schedule Расписание.
     */
    public void setSchedule(SparseArray<ArrayList<Lesson>> schedule) {
        mSchedule = schedule;
        // Если мы установили расписание, то нужно его показать.
        if (Saver.get().getBoolean("DayPriority", true))
            onShowScheduleDay();
        else
            onShowScheduleTape();
    }

    public void onShowPreferences() {
        SettingsActivity.GeneralPreferenceFragment.setActivityWeakReference(this);
        getFragmentManager().beginTransaction().replace(R.id.StartContainer, new SettingsActivity.GeneralPreferenceFragment(), "Preferences").addToBackStack("Day").commit();
    }

    /**
     * Показывает расписание в виде дня.
     */
    public void onShowScheduleDay() {
        Locale myLocale = new Locale("ru", "RU");
        Calendar mCalendar = Calendar.getInstance(myLocale);

        Log.e(TAG, String.valueOf(mCalendar.get(Calendar.WEEK_OF_YEAR)));

        int mStartDay = Saver.getStartDay();

        Log.e(TAG, "Start day: " + mStartDay);

        DayFragment.setActivityWeakReference(this);
        DayManagerFragment.setActivityWeakReference(this);
        DayManagerFragment mFragment = new DayManagerFragment();
        Bundle mBundle = new Bundle();
        mBundle.putInt("StartDay", mStartDay);
        mFragment.setArguments(mBundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.StartContainer, mFragment, "DayManager").commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Если группа по умолчанию не выбрана, то вынуждаем пользователя выбрать её.
        if (Saver.get().getInt("GroupID", 0) == 0) {
            Intent intent = new Intent(StartActivity.this, GroupSelectActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_GROUP);
        } else {
            // Если расписание не загружено, то загружаем.
            if (mSchedule == null)
                onDownloadSchedule();
            else {
                if (Saver.get().getBoolean("DayPriority", true))
                    onShowScheduleDay();
                else
                    onShowScheduleTape();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeWrapper.onActivityApplyTheme(this, true);
        //setContentView(R.layout.activity_start);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mToolbarTitleView = findViewById(R.id.StartToolbarTitle);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.action_view_day);

        mSubtitleHeaderDrawer = navigationView.getHeaderView(0).findViewById(R.id.textView);
        try {
            if (new Random().nextInt(100) == 13)
                mSubtitleHeaderDrawer.setText("Здесь могла быть ваша реклама!");
            else if (new Random().nextInt(100) == 85)
                mSubtitleHeaderDrawer.setText("Здесь должен был быть номер твоей группы");
            else
                mSubtitleHeaderDrawer.setText(Saver.get().getString("GroupNumber", "Кто-то украл номер твоей группы.."));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_GROUP) {
            if (resultCode == RESULT_OK) {
                Saver.remove("ScheduleJSON");
                onDownloadSchedule();
                // Update Navigation bar
                if (mSubtitleHeaderDrawer != null)
                    mSubtitleHeaderDrawer.setText(data.getStringExtra(GroupSelectActivity.GROUP));
            } else
                Toast.makeText(this, "Access denied!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Устанавливает определенную строку в качестве заголовка.
     *
     * @param s Заголовок.
     */
    public void setTitle(String s) {
        if (mToolbarTitleView != null)
            mToolbarTitleView.setText(s);
    }

    public void onDownloadSchedule() {
        DownloadScheduleFragment.setActivityReference(this);
        getFragmentManager().beginTransaction().replace(R.id.StartContainer, new DownloadScheduleFragment(), "DownloadSchedule").commit();
        setTitle("Составление расписания");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /**
     * Получение цвета из темы приложения.
     *
     * @param context Context.
     * @param id      ID аттрибута.
     * @return Цвет (int).
     */
    public static int getColor(final Context context, int id) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(id, value, true);
        return value.data;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            onShowPreferences();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_update:
                Saver.remove("ScheduleJSON");
                onDownloadSchedule();
                break;
            case R.id.action_settings:
                onShowPreferences();
                break;
            case R.id.action_view_day:
                onShowScheduleDay();
                break;
            case R.id.action_my_studies:
                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("https://forlabs.ru/studies"));
                startActivity(browse);
                break;
            case R.id.action_view_tape:
                onShowScheduleTape();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onShowScheduleTape() {
        TapeFragment.setActivityWeakReference(this);
        getFragmentManager().beginTransaction().replace(R.id.StartContainer, new TapeFragment(), "TapeFragment").commit();
    }

    public static int getScheduleSize() {
        return 14;
    }
}
