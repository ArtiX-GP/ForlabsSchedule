package ru.trixiean.forlabsschedule.Fragments.Day;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

import ru.trixiean.forlabsschedule.R;
import ru.trixiean.forlabsschedule.StartActivity;
import ru.trixiean.forlabsschedule.Utils.Lesson;
import ru.trixiean.forlabsschedule.Utils.Saver;

import static ru.trixiean.forlabsschedule.Fragments.Day.DayFragment.getDate;

/**
 * Если этот код работает, то его написал Никита Громик, если нет,
 * то не знаю кто его писал. (06.02.2018)
 */

public class DayManagerFragment extends Fragment {

    private static final String TAG = "DayManager";
    public static WeakReference<StartActivity> mActivityWeakReference;

    /**
     * Отправная точка - день, с которого показывается расписание (1 - 14).
     */
    private int mStartDay;
    public FloatingActionButton mFloatingActionButton;

    public static void setActivityWeakReference(StartActivity activity) {
        DayManagerFragment.mActivityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mStartDay = getArguments().getInt("StartDay");

        Log.e(TAG, "Start Day: " + mStartDay);
    }

    public void setTitle(int mOffsetDay) {
        try {
            if (mActivityWeakReference.get() != null && !mActivityWeakReference.get().isFinishing()) {
                String mTitleToolbarText = getDate(mOffsetDay, true);
                mActivityWeakReference.get().setTitle(mTitleToolbarText);
            }
        } catch (Exception e) {
            Log.e(TAG, "Can't set title. " + e.toString());
        }
    }

    private void setColorFAB(int dayOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayOffset);

        if (Saver.get().getBoolean("OddBottom", false)) {
            if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 != 0)
                mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(StartActivity.getColor(mActivityWeakReference.get(), R.attr.ColorBottomDark)));
            else
                mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(StartActivity.getColor(mActivityWeakReference.get(), R.attr.ColorTopDark)));
        } else if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0)
            mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(StartActivity.getColor(mActivityWeakReference.get(), R.attr.ColorBottomDark)));
        else
            mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(StartActivity.getColor(mActivityWeakReference.get(), R.attr.ColorTopDark)));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View mContainer = inflater.inflate(R.layout.fragment_view_pager, container, false);
        final ViewPager mViewPager = mContainer.findViewById(R.id.ViewPager);

        mFloatingActionButton = mContainer.findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Вжух! Расписание на сегодня!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                try {
                    mViewPager.setCurrentItem(StartActivity.getScheduleSize() * ViewPagerDayAdapter.LOOPS_COUNT / 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            if (mActivityWeakReference.get() != null && !mActivityWeakReference.get().isFinishing()) {
                if (mActivityWeakReference.get().getSchedule() != null) {
                    DayFragment.setParentFragment(this);

                    final ViewPagerDayAdapter mAdapter = new ViewPagerDayAdapter(getFragmentManager(), mActivityWeakReference.get().getSchedule());
                    mViewPager.setAdapter(mAdapter);
                    mViewPager.setOffscreenPageLimit(1);
                    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            setTitle(position - 7000);
                            setColorFAB(position - 7000);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    mViewPager.setCurrentItem(StartActivity.getScheduleSize() * ViewPagerDayAdapter.LOOPS_COUNT / 2, false); // set current item in the adapter to middle
                } else
                    Log.e(TAG, "Расписания-то нет!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Непредвиденная ошибка. " + e.toString());
        }

        return mContainer;
    }

    private class ViewPagerDayAdapter extends FragmentStatePagerAdapter {

        final static int LOOPS_COUNT = 1000;
        private SparseArray<ArrayList<Lesson>> mLessons;

        SparseArray<WeakReference<DayFragment>> mRegisteredFragments = new SparseArray<>();

        ViewPagerDayAdapter(FragmentManager fm, SparseArray<ArrayList<Lesson>> mLessons) {
            super(fm);
            this.mLessons = mLessons;
        }

        @Override
        public Fragment getItem(int position) {
            DayFragment mFragment = new DayFragment();
            Bundle mBundle = new Bundle();
            mBundle.putInt("Position", position);
            mBundle.putInt("StartDay", mStartDay);
            mFragment.setArguments(mBundle);
            return mFragment;
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            DayFragment mFragment = (DayFragment) super.instantiateItem(container, position);
            mRegisteredFragments.put(position, new WeakReference<>(mFragment));
            return mFragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mRegisteredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        WeakReference<DayFragment> getRegisteredFragment(int position) {
            return mRegisteredFragments.get(position);
        }

        @Override
        public int getCount() {
            return mLessons.size() * LOOPS_COUNT;
        }
    }

}
