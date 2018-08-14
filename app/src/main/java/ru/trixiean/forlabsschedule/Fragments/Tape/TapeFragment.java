package ru.trixiean.forlabsschedule.Fragments.Tape;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ru.trixiean.forlabsschedule.R;
import ru.trixiean.forlabsschedule.StartActivity;
import ru.trixiean.forlabsschedule.Utils.Lesson;
import ru.trixiean.forlabsschedule.Utils.LocalConverter;
import ru.trixiean.forlabsschedule.Utils.Saver;

/**
 * Если этот код работает, то его написал Никита Громик, если нет,
 * то не знаю кто его писал. (06.08.2018)
 */
public class TapeFragment extends Fragment {

    private final int LOOPS_COUNT = 1000;

    private static WeakReference<StartActivity> mActivityWeakReference;

    public static void setActivityWeakReference(StartActivity activity) {
        mActivityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivityWeakReference.get() != null && !mActivityWeakReference.get().isFinishing())
            mActivityWeakReference.get().setTitle("Расписание");
        Saver.save("DayPriority", false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mRoot = inflater.inflate(R.layout.fragment_tape, container, false);
        RecyclerView mRecyclerView = mRoot.findViewById(R.id.RecyclerViewTape);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new TapeAdapter());
        mRecyclerView.scrollToPosition(LOOPS_COUNT * 7);
        return mRoot;
    }

    private class TapeAdapter extends RecyclerView.Adapter<TapeAdapter.ViewHolder> {

        private final int TYPE_HEADER = 1;
        private final int TYPE_LESSON = 2;
        private final int TYPE_EMPTY = 3;

        private boolean isBottom = true;

        private SparseArray<ArrayList<Lesson>> mSchedule;
        private int mCurrentLessonOrder;

        TapeAdapter() {
            try {
                mSchedule = Saver.getScheduleFromCache();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            return true;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER)
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_tape, parent, false));
            if (viewType == TYPE_LESSON)
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false));
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_lesson, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int mDayOffset = (position - getItemCount() / 2) / 8;

            Locale myLocale = new Locale("ru", "RU");
            Calendar mCalendar = Calendar.getInstance(myLocale);
            mCalendar.add(Calendar.DATE, mDayOffset);

            if (Saver.get().getBoolean("OddBottom", false)) {
                isBottom = mCalendar.get(Calendar.WEEK_OF_YEAR) % 2 != 0;
            } else isBottom = mCalendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0;

            int mColor = isBottom ? StartActivity.getColor(holder.itemView.getContext(), R.attr.ColorBottom) : StartActivity.getColor(holder.itemView.getContext(), R.attr.ColorTop);

            if (getItemViewType(position) == TYPE_HEADER) {

                String mDateString = String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)) + " ";

                switch (mCalendar.get(Calendar.MONTH)) {
                    case Calendar.JANUARY:
                        mDateString += "января";
                        break;
                    case Calendar.FEBRUARY:
                        mDateString += "февраля";
                        break;
                    case Calendar.MARCH:
                        mDateString += "марта";
                        break;
                    case Calendar.APRIL:
                        mDateString += "апреля";
                        break;
                    case Calendar.MAY:
                        mDateString += "мая";
                        break;
                    case Calendar.JUNE:
                        mDateString += "июня";
                        break;
                    case Calendar.JULY:
                        mDateString += "июля";
                        break;
                    case Calendar.AUGUST:
                        mDateString += "августа";
                        break;
                    case Calendar.SEPTEMBER:
                        mDateString += "сентября";
                        break;
                    case Calendar.OCTOBER:
                        mDateString += "октября";
                        break;
                    case Calendar.NOVEMBER:
                        mDateString += "ноября";
                        break;
                    case Calendar.DECEMBER:
                        mDateString += "декабря";
                        break;
                }

                String mDayTitle = "";
                switch (mCalendar.get(Calendar.DAY_OF_WEEK)) {
                    case Calendar.MONDAY:
                        mDayTitle = "ПН";
                        break;
                    case Calendar.TUESDAY:
                        mDayTitle = "ВТ";
                        break;
                    case Calendar.WEDNESDAY:
                        mDayTitle = "СР";
                        break;
                    case Calendar.THURSDAY:
                        mDayTitle = "ЧТ";
                        break;
                    case Calendar.FRIDAY:
                        mDayTitle = "ПТ";
                        break;
                    case Calendar.SATURDAY:
                        mDayTitle = "СБ";
                        break;
                    case Calendar.SUNDAY:
                        mDayTitle = "ВС";
                        break;
                }

                //mDateString += " " + String.valueOf(mDayOffset);

                try {
                    holder._Date.setText(mDateString);
                    holder._DayWeek.setText(mDayTitle);
                    holder._Date.setTextColor(mColor);
                    holder._DayWeek.setTextColor(mColor);
                    holder._Divider.setBackgroundColor(mColor);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else if (mSchedule != null && mSchedule.get(mDayOffset % 14) != null) {
                Lesson mCurrent = mSchedule.get(mDayOffset % 14).get(position % 7);
                holder.Time.setText(getTimeByPosition(position));

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

                    // Установка смещения относительно правойц границы экрана.
                    int mMarginPX = (int) LocalConverter.convertDpToPixel(getActivity(),
                            Saver.get().getInt("TitlePaddingRight", 0));
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    llp.setMargins(0, 0, mMarginPX, 0);
                    holder.Title.setLayoutParams(llp);
                }

            } else {
                holder.Time.setText(getTimeByPosition(holder.getAdapterPosition() - 1));

                // Reset layout.
                TypedValue typedValue = new TypedValue();

                // I used getActivity() as if you were calling from a fragment.
                // You just want to call getTheme() on the current activity, however you can get it
                getActivity().getTheme().resolveAttribute(R.attr.TextColorDark, typedValue, true);

                // it's probably a good idea to check if the color wasn't specified as a resource
                if (typedValue.resourceId != 0) {
                    holder.Time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), typedValue.resourceId));
                } else {
                    // this should work whether there was a resource id or not
                    holder.Time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), typedValue.data));
                }

                holder.Layout.setBackground(null);
            }

            // Если расписание на сегодня и отображение текущей пары включено.
            if (Saver.get().getBoolean("ShowNowLesson", true) && mDayOffset == 0 && getItemViewType(position) != TYPE_HEADER) {
                // Вернет текущий час (0 - 23).
                int mMinutes = mCalendar.get(Calendar.MINUTE) + (60 * mCalendar.get(Calendar.HOUR_OF_DAY));

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

                if (mCurrentLessonOrder > 0 && (mCurrentLessonOrder + getItemCount() / 2) == position) {
                    int mBackgroundID = isBottom ? R.drawable.background_current_lesson : R.drawable.background_current_lesson_top;
                    holder.Layout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), mBackgroundID));
                    holder.Time.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        }

        @Override
        public int getItemCount() {
            return LOOPS_COUNT * 14;
        }

        @Override
        public int getItemViewType(int position) {
            if (position % 8 == 0)
                return TYPE_HEADER;
            if (mSchedule != null && mSchedule.get(position % 14) != null && mSchedule.get(position % 14).get(position % getItemCount() / 2) != null &&
                    mSchedule.get(position % 14).get(position % getItemCount() / 2).getTitle() != null)
                return TYPE_LESSON;
            return TYPE_EMPTY;
        }

        private String getTimeByPosition(int orderInCategory) {
            switch (orderInCategory % 8) {
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
            return String.valueOf(orderInCategory % 8);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView _Date, _DayWeek;
            View _Divider;
            TextView Time, Title, Day;
            LinearLayout Layout;

            ViewHolder(View itemView) {
                super(itemView);
                _Date = itemView.findViewById(R.id.TapeDateTextView);
                _Divider = itemView.findViewById(R.id.TapeDivider);
                _DayWeek = itemView.findViewById(R.id.TapeDayWeekTextView);
                Time = itemView.findViewById(R.id.ItemTimeText);
                Title = itemView.findViewById(R.id.ItemTitleText);
                Day = itemView.findViewById(R.id.ItemDayText);
                Layout = itemView.findViewById(R.id.ItemLayout);
            }
        }
    }
}
