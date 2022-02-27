package com.example.daily_cashbook.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daily_cashbook.activity.ItemsDetail;
import com.example.daily_cashbook.activity.MetaApplication;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.CashbookDatabase;
import com.example.daily_cashbook.dbutils.SharedPref;
import com.example.daily_cashbook.entity.Cashbook;
import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.msg.Hint;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private int flag; //0=select date, 1=time range

    private int userId;

    String date;

    MyAdapter adapter;
    RecyclerView recyclerView;

    ArrayList<Cashbook> array;
    DatePickerDialog.OnDateSetListener setListener;

    public HomeFragment() {

    }

    public HomeFragment(int flag) {
        this.flag = flag;
    }

    public void update1() {
        getBookingItems(selectDate);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = null;
        userId = SharedPref.getUser().getId();

        if (this.flag == 0) {    //select date
            view = inflater.inflate(R.layout.home_content_select_date, container, false);
            recyclerView = view.findViewById(R.id.rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            TextView fgHomeSelectDate;
            fgHomeSelectDate = view.findViewById(R.id.fg_home_select_date);

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            date = year + "/" + ((month + 1) >= 10 ? (month + 1) : "0" + (month + 1)) + "/" + day;
            fgHomeSelectDate.setText(date);

            selectDatePage(view, fgHomeSelectDate, year, month, day);
        } else if (this.flag == 1) {  //time range
            view = inflater.inflate(R.layout.home_content_range_date, container, false);
            recyclerView = view.findViewById(R.id.rv2);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            rangeDate(view);
        }

        return view;
    }

    String selectDate;
    TextView outcome;
    TextView income;

    /**
     * 特定日期页面
     */
    public void selectDatePage(View view, TextView fgHomeSelectDate,
                               int year, int month, int day) {
        LinearLayout layoutSDYear;
        LinearLayout layoutSDMonth;
        LinearLayout layoutSDDay;
        TextView tvyear;
        TextView tvmonth;
        TextView tvday;

        outcome = (TextView) view.findViewById(R.id.home_nboutcome);
        income = (TextView) view.findViewById(R.id.home_nbincome);
        layoutSDYear = (LinearLayout) view.findViewById(R.id.home_sd_year);
        layoutSDMonth = (LinearLayout) view.findViewById(R.id.home_sd_month);
        layoutSDDay = (LinearLayout) view.findViewById(R.id.home_sd_day);
        tvyear = (TextView) view.findViewById(R.id.home_sd_year_t);
        tvmonth = (TextView) view.findViewById(R.id.home_sd_month_t);
        tvday = (TextView) view.findViewById(R.id.home_sd_day_t);

        //DatePicker
        Calendar calendar = Calendar.getInstance();
        final int yearB = calendar.get(Calendar.YEAR);
        final int monthB = calendar.get(Calendar.MONTH);
        final int dayB = calendar.get(Calendar.DAY_OF_MONTH);
        selectDate = yearB + "/" +
                ((monthB + 1) >= 10 ? (monthB + 1) : "0" + (monthB + 1)) + "/" +
                ((dayB) >= 10 ? (dayB) : "0" + (dayB));

        fgHomeSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        selectDate = year + "/" +
                                ((month + 1) >= 10 ? (month + 1) : "0" + (month + 1)) + "/" +
                                ((day) >= 10 ? (day) : "0" + (day));
                        fgHomeSelectDate.setText(selectDate);
                        getBookingItems(selectDate);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        //按照年月日提取
        layoutSDYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBookingItems(selectDate.substring(0, 4));
                //设置字体颜色
                tvyear.setTextColor(getResources().getColor(R.color.brown));
                tvmonth.setTextColor(getResources().getColor(R.color.bg_white));
                tvday.setTextColor(getResources().getColor(R.color.bg_white));
            }
        });
        layoutSDMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBookingItems(selectDate.substring(0, 7));
                tvyear.setTextColor(getResources().getColor(R.color.bg_white));
                tvmonth.setTextColor(getResources().getColor(R.color.brown));
                tvday.setTextColor(getResources().getColor(R.color.bg_white));
            }
        });
        layoutSDDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBookingItems(selectDate);
                tvyear.setTextColor(getResources().getColor(R.color.bg_white));
                tvmonth.setTextColor(getResources().getColor(R.color.bg_white));
                tvday.setTextColor(getResources().getColor(R.color.brown));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.flag == 0) {
            getBookingItems(selectDate);
        } else {

            getBookingItems(dateB, dateE, outcome, income);
        }

    }

    String dateB;
    String dateE;

    /**
     * 时间范围页面
     */
    public void rangeDate(View view) {
        TextView outcome;
        TextView income;
        TextView fgHomeTimeBegin;
        TextView fgHomeTimeEnd;

        outcome = view.findViewById(R.id.home_nboutcome_rd);
        income = view.findViewById(R.id.home_nbincome_rd);
        fgHomeTimeBegin = view.findViewById(R.id.fg_home_time_begin);
        fgHomeTimeEnd = view.findViewById(R.id.fg_home_time_end);
        //begin
        Calendar calendarB = Calendar.getInstance();
        final int yearB = calendarB.get(Calendar.YEAR);
        final int monthB = calendarB.get(Calendar.MONTH);
        final int dayB = calendarB.get(Calendar.DAY_OF_MONTH);
        final String[] dateB = {null};


        Calendar calendarE = Calendar.getInstance();
        int yearE = calendarE.get(Calendar.YEAR);
        int monthE = calendarE.get(Calendar.MONTH);
        int dayE = calendarE.get(Calendar.DAY_OF_MONTH);
        final String[] dateE = {null};

        fgHomeTimeBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialogB = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int yearB, int monthB, int dayB) {
                        dateB[0] = yearB + "/" +
                                ((monthB + 1) >= 10 ? (monthB + 1) : "0" + (monthB + 1)) + "/" +
                                ((dayB) >= 10 ? (dayB) : "0" + (dayB));
                        fgHomeTimeBegin.setText(dateB[0]);
                        getBookingItems(dateB[0], dateE[0], outcome, income);
                    }
                }, yearB, monthB, dayB);
                datePickerDialogB.show();
            }
        });

        //end
        fgHomeTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialogE = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int yearE, int monthE, int dayE) {
                        dateE[0] = yearE + "/" +
                                ((monthE + 1) >= 10 ? (monthE + 1) : "0" + (monthE + 1)) + "/" +
                                ((dayE) >= 10 ? (dayE) : "0" + (dayE));
                        fgHomeTimeEnd.setText(dateE[0]);
                        getBookingItems(dateB[0], dateE[0], outcome, income);
                    }
                }, yearE, monthE, dayE);
                datePickerDialogE.show();
            }
        });

    }

    /**
     * 提取记账数据
     */
    public void getBookingItems(String date) {
        array = CashbookDatabase.getInstence().search(userId, date);
        double nbOutcome = 0;
        double nbIncome = 0;

        for (int i = 0; i < array.size(); i++) {
            String inorout = array.get(i).getInorout();
            double money = Double.parseDouble(array.get(i).getMoney());
            if (inorout.equals("input")) {
                nbIncome += money;
            } else {
                nbOutcome += money;
            }
        }
        outcome.setText(String.valueOf(nbOutcome));
        income.setText(String.valueOf(nbIncome));

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new MyAdapter();
            recyclerView.setAdapter(adapter);
        }
    }

    public void getBookingItems(String beginDate, String endDate, TextView outcome, TextView income) {

        if (beginDate == null || endDate == null || beginDate.equals("") || endDate.equals("")) {
            return;
        }

        array = CashbookDatabase.getInstence().searchPeriod(userId, beginDate, endDate);
        double nbOutcome = 0;
        double nbIncome = 0;

        for (int i = 0; i < array.size(); i++) {
            String inorout = array.get(i).getInorout();
            double money = Double.parseDouble(array.get(i).getMoney());
            if (inorout.equals("input")) {
                nbIncome += money;
            } else {
                nbOutcome += money;
            }
        }
        outcome.setText(String.valueOf(nbOutcome));
        income.setText(String.valueOf(nbIncome));
        //recyclerView没有初始化
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new MyAdapter();
            recyclerView.setAdapter(adapter);
        }
    }

    public boolean checkTime(String beginDate, String endDate) {
        long time1 = timeToStamp(beginDate);
        long time2 = timeToStamp(endDate);

        if (time1 > time2) {
            Hint.show("请输入正确的时间范围");
            return false;
        }
        return true;
    }

    //日期转换为时间戳
    public long timeToStamp(String timers) {
        Date d = new Date();
        long timeStemp = 0;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
            d = sf.parse(timers);// 日期转换为时间戳
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeStemp = d.getTime();
        return timeStemp;
    }

    @Override
    public void onClick(View v) {

    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.items_single, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ItemsDetail.class);
                    intent.putExtra("booking", array.get(position));
                    startActivity(intent);
                }
            });
            Cashbook booking = array.get(position);
            String inorout = booking.getInorout();
            viewHolder.type.setText(booking.getType());
            if (position > 0) {
                if (array.get(position - 1).getTime().split("/")[2].equals(booking.getTime().split("/")[2])) {
                    viewHolder.time.setText(booking.getTime());
                    viewHolder.time.setVisibility(View.GONE);
                    viewHolder.line.setVisibility(View.GONE);
                } else {
                    viewHolder.time.setText(booking.getTime());
                    viewHolder.time.setVisibility(View.VISIBLE);
                    viewHolder.line.setVisibility(View.VISIBLE);
                }
            } else {
                viewHolder.time.setVisibility(View.VISIBLE);
                viewHolder.line.setVisibility(View.VISIBLE);
                viewHolder.time.setText(booking.getTime());
            }

            String text = (inorout.equals("input") ? "+" : "-") + booking.getMoney();
            viewHolder.money.setText(text);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return array.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        View line;
        ImageView iconItem;
        TextView type;
        TextView money;

        public ViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.single_time);
            line = view.findViewById(R.id.single_line);
            iconItem = view.findViewById(R.id.single_icitem);
            type = view.findViewById(R.id.single_type);
            money = view.findViewById(R.id.single_money);
        }
    }

}
