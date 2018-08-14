package ru.trixiean.forlabsschedule.Fragments.Day;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.trixiean.forlabsschedule.R;
import ru.trixiean.forlabsschedule.StartActivity;
import ru.trixiean.forlabsschedule.Utils.Lesson;
import ru.trixiean.forlabsschedule.Utils.LocalConverter;
import ru.trixiean.forlabsschedule.Utils.Saver;

/**
 * Если этот код работает, то его написал Никита Громик, если нет,
 * то не знаю кто его писал. (06.02.2018)
 */

public class DayFragment extends Fragment {

    private static final String TAG = "DayFragment";

    public int mDay = -1;

    // Флажок статуса недели (верхняя/нижняя).
    private boolean isBottom;

    private static WeakReference<StartActivity> mActivityWeakReference;
    private static WeakReference<DayManagerFragment> mParentFragment;
    private int mPosition = -1;

    public static void setActivityWeakReference(StartActivity activity) {
        mActivityWeakReference = new WeakReference<>(activity);
    }

    public static void setParentFragment(DayManagerFragment mParentFragment) {
        DayFragment.mParentFragment = new WeakReference<>(mParentFragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setUserVisibleHint(false);
        if (getArguments() != null) {
            // Центр (т.е. сегодняшний день) является нулевой позицией.
            mPosition = getArguments().getInt("Position") - 7000;
            int mStartDay = getArguments().getInt("StartDay");
            mDay = (mPosition + mStartDay) % StartActivity.getScheduleSize();

            Saver.save("DayPriority", true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.i(TAG, isBottom ? "Нижняя неделя" : "Верхняя неделя");
            Log.i(TAG, String.valueOf(mPosition));
            /*setTitle(mPosition);
            if (mParentFragment.get() != null && mActivityWeakReference != null && !mActivityWeakReference.get().isFinishing()) {
                if (isBottom)
                    mParentFragment.get().mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(StartActivity.getColor(mActivityWeakReference.get(), R.attr.ColorBottomDark)));
                else
                    mParentFragment.get().mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(StartActivity.getColor(mActivityWeakReference.get(), R.attr.ColorTopDark)));
            }*/
        }
    }

    public static String getDate(int mOffsetDay, boolean showWeekStatus) {
        long mCurrentTime = System.currentTimeMillis();
        // Смещение времени.
        mCurrentTime += mOffsetDay * (long) (24 * 60 * 60 * 1000);

        Locale myLocale = new Locale("ru", "RU");
        Calendar mCalendar = Calendar.getInstance(myLocale);

        Log.e(TAG, "Offset: " + mOffsetDay);

        if (mOffsetDay != 0)
            mCalendar.setTime(new Date(mCurrentTime));

        String mTitleToolbarText;
        if (!showWeekStatus) {
            mTitleToolbarText = String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)) + " ";

            switch (mCalendar.get(Calendar.MONTH)) {
                case Calendar.JANUARY:
                    mTitleToolbarText += "января";
                    break;
                case Calendar.FEBRUARY:
                    mTitleToolbarText += "февраля";
                    break;
                case Calendar.MARCH:
                    mTitleToolbarText += "марта";
                    break;
                case Calendar.APRIL:
                    mTitleToolbarText += "апреля";
                    break;
                case Calendar.MAY:
                    mTitleToolbarText += "мая";
                    break;
                case Calendar.JUNE:
                    mTitleToolbarText += "июня";
                    break;
                case Calendar.JULY:
                    mTitleToolbarText += "июля";
                    break;
                case Calendar.AUGUST:
                    mTitleToolbarText += "августа";
                    break;
                case Calendar.SEPTEMBER:
                    mTitleToolbarText += "сентября";
                    break;
                case Calendar.OCTOBER:
                    mTitleToolbarText += "октября";
                    break;
                case Calendar.NOVEMBER:
                    mTitleToolbarText += "ноября";
                    break;
                case Calendar.DECEMBER:
                    mTitleToolbarText += "декабря";
                    break;
            }

        } else {
            // Если нечетная неделя является нижней неделей (+7 к стартовому дню).
            if (Saver.get().getBoolean("OddBottom", false)) {
                // Поэтому, если сейчас нечетная неделя, то бустим стартовый день.
                if (mCalendar.get(Calendar.WEEK_OF_YEAR) % 2 != 0)
                    mTitleToolbarText = "Нижняя неделя";
                else
                    mTitleToolbarText = "Верхняя неделя";
            } else if (mCalendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0)
                // Если четная неделя оказалась нижней, то проверяем текущую неделю на четность
                // и если та оказывается четной, то бустим стартовый день.
                mTitleToolbarText = "Нижняя неделя";
            else
                mTitleToolbarText = "Верхняя неделя";
        }
        return mTitleToolbarText;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (mActivityWeakReference.get().getSchedule() != null) {
            final View mContainer = inflater.inflate(R.layout.fragment_day, container, false);
            RecyclerView mRecyclerView = mContainer.findViewById(R.id.DayRecyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            DayAdapter mAdapter = new DayAdapter(mActivityWeakReference.get().getSchedule().get(mDay), isBottom);

            long mCurrentTime = System.currentTimeMillis();
            // Смещение времени.
            mCurrentTime += mPosition * (long) (24 * 60 * 60 * 1000);

            Locale myLocale = new Locale("ru", "RU");
            Calendar mCalendar = Calendar.getInstance(myLocale);

            if (mPosition != 0)
                mCalendar.setTime(new Date(mCurrentTime));

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

            mDayTitle += ", " + getDate(mPosition, false);

            mAdapter.setDayTitle(mDayTitle);

            if (mPosition == 0)
                mAdapter.setToday();

            if (Saver.get().getBoolean("OddBottom", false)) {
                isBottom = mCalendar.get(Calendar.WEEK_OF_YEAR) % 2 != 0;
            } else isBottom = mCalendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0;

            mAdapter.setBottom(isBottom);
            mRecyclerView.setAdapter(mAdapter);
            return mContainer;
        } else if (mActivityWeakReference.get() != null)
            mActivityWeakReference.get().onDownloadSchedule();
        return null;
    }

    private class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

        private ArrayList<Lesson> mLessons;

        private boolean isBottom;

        private int mCurrentLessonOrder = -1;

        public void setBottom(boolean bottom) {
            isBottom = bottom;
        }

        // День недели.
        private String mDayTitle;

        // Расписание на сегодня?
        private boolean isToday;

        void setToday() {
            if (Saver.get().getBoolean("ShowNowLesson", true))
                isToday = true;
        }

        void setDayTitle(String mDayTitle) {
            this.mDayTitle = mDayTitle;
            notifyItemChanged(0);
        }

        private final int TYPE_EMPTY = -1;
        private final int TYPE_LESSON = 0;
        private final int TYPE_HEADER = 1;

        DayAdapter(ArrayList<Lesson> mLessons, boolean isBottom) {
            this.mLessons = mLessons;
            this.isBottom = isBottom;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mRoot;
            if (viewType == TYPE_EMPTY)
                mRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_lesson, parent, false);
            else if (viewType == TYPE_LESSON)
                mRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
            else
                mRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_lesson, parent, false);

            return new ViewHolder(mRoot);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int mColor = isBottom ? StartActivity.getColor(holder.itemView.getContext(), R.attr.ColorBottom) : StartActivity.getColor(holder.itemView.getContext(), R.attr.ColorTop);
            if (getItemViewType(position) == TYPE_HEADER) {
                holder.Day.setText(mDayTitle);
                holder.Day.setTextColor(mColor);
            } else if (mLessons != null) {
                Lesson mCurrent = mLessons.get(holder.getAdapterPosition() - 1);
                holder.Time.setText(getTimeByPosition(mCurrent.getOrderInCategory()));

                if (getItemViewType(position) == TYPE_LESSON) {
                    holder.Title.setText(mCurrent.getTitle() + " (" + mCurrent.getAudience() + ")");
                    int mBackgroundID = isBottom ? R.drawable.header_secondary_background : R.drawable.header_group_background;
                    holder.Title.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), mBackgroundID));
                    holder.Title.setTextColor(mColor);
                    holder.Time.setTextColor(mColor);

                    int mPaddingPX;
                    if (holder.Title.getLineCount() > 1)
                        mPaddingPX = (int) LocalConverter.convertDpToPixel(getActivity(), 4);
                    else
                        mPaddingPX = (int) LocalConverter.convertDpToPixel(getActivity(), 8);
                    holder.Title.setPadding(mPaddingPX, mPaddingPX, mPaddingPX, mPaddingPX);

                    // Установка смещения относительно правой границы экрана.
                    int mMarginPX = (int) LocalConverter.convertDpToPixel(getActivity(),
                            Saver.get().getInt("TitlePaddingRight", 0));
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    llp.setMargins(0, 0, mMarginPX, 0);
                    holder.Title.setLayoutParams(llp);
                }

                // Если расписание на сегодня и отображение текущей пары включено.
                if (isToday) {
                    Calendar mRightNow = Calendar.getInstance();
                    // Вернет текущий час (0 - 23).
                    int mMinutes = mRightNow.get(Calendar.MINUTE) + (60 * mRightNow.get(Calendar.HOUR_OF_DAY));

                    mCurrentLessonOrder = -1;
                    // Если часы в пределах учебного дня.
                    // Считаем по минутам, конец рабочего дня = 1160, начало = 510.
                    if (mMinutes <= 1160 && mMinutes >= 510) {
                        if (mMinutes >= 1130)
                            mCurrentLessonOrder = 7;
                        else if (mMinutes >= 1030)
                            mCurrentLessonOrder = 6;
                        else if (mMinutes >= 930)
                            mCurrentLessonOrder = 5;
                        else if (mMinutes >= 830)
                            mCurrentLessonOrder = 4;
                        else if (mMinutes >= 710)
                            mCurrentLessonOrder = 3;
                        else if (mMinutes >= 610)
                            mCurrentLessonOrder = 2;
                        else
                            mCurrentLessonOrder = 1;
                    }
                }

                if (mCurrentLessonOrder == holder.getAdapterPosition()) {
                    int mBackgroundID = isBottom ? R.drawable.background_current_lesson : R.drawable.background_current_lesson_top;
                    holder.Layout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), mBackgroundID));
                    holder.Time.setTextColor(Color.parseColor("#FFFFFF"));
                }
            } else {
                holder.Time.setText(getTimeByPosition(holder.getAdapterPosition() - 1));
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
                    return "17:10";
                case 6:
                    return "18:50";
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return TYPE_HEADER;
            if (mLessons != null && mLessons.get(position - 1).getTitle() != null)
                return TYPE_LESSON;
            return TYPE_EMPTY;
        }

        @Override
        public int getItemCount() {
            if (mLessons != null)
                return mLessons.size() + 1;
            return 8;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView Time, Title, Day;
            LinearLayout Layout;

            ViewHolder(View itemView) {
                super(itemView);
                Time = itemView.findViewById(R.id.ItemTimeText);
                Title = itemView.findViewById(R.id.ItemTitleText);
                Day = itemView.findViewById(R.id.ItemDayText);
                Layout = itemView.findViewById(R.id.ItemLayout);
            }
        }
    }

}
