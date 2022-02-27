package com.example.daily_cashbook.fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.utils.Easing;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.CashbookDatabase;
import com.example.daily_cashbook.dbutils.SharedPref;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReportFragment extends Fragment implements View.OnClickListener {

    View view;

    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    String date = year + "/" + ((month + 1) >= 10 ? (month + 1) : "0" + (month + 1))
            + "/" + (day >= 10 ? day: "0" + day);;

    TextView tvdate;
    TextView tvout, tvin;
    LinearLayout lyyear;
    LinearLayout lymonth;
    LinearLayout lyday;
    TextView tvyear;
    TextView tvmonth;
    TextView tvday;

    String inorout = "output";
    JSONArray array, arrTop;

    ListView lv;
    int userId;
    PieChart chart;

    MyAdapter adapter;
    TimePickerDialog mDialogAll;
    long timeStamp;

    public ReportFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.report_content, container, false);

        chart = view.findViewById(R.id.chart);
        userId = SharedPref.getUser().getId();
        tvdate = view.findViewById(R.id.tvdate);
        tvout = view.findViewById(R.id.tvout);
        tvin = view.findViewById(R.id.tvin);
        lv = view.findViewById(R.id.lv);
        lyyear = view.findViewById(R.id.cyear);
        lymonth = view.findViewById(R.id.cmonth);
        lyday = view.findViewById(R.id.cday);
        tvyear = view.findViewById(R.id.report_year);
        tvmonth = view.findViewById(R.id.report_month);
        tvday = view.findViewById(R.id.report_day);

        tvout.setOnClickListener(this);
        tvin.setOnClickListener(this);

        //DatePicker
        final String[] selectDate = {null};
        selectDate[0] = date;

        tvdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        selectDate[0] = year + "/" +
                                ((month + 1) >= 10 ? (month + 1) : "0" + (month + 1)) + "/" +
                                ((day) >= 10 ? (day) : "0" + (day));
                        tvdate.setText(selectDate[0]);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        //按照年月日提取
        lyyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyyear.setBackgroundColor(getResources().getColor(R.color.bg_white));
                lymonth.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                lyday.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                tvyear.setTextColor(getResources().getColor(R.color.darkGreen));
                tvmonth.setTextColor(getResources().getColor(R.color.bg_white));
                tvday.setTextColor(getResources().getColor(R.color.bg_white));
                getItems(selectDate[0].substring(0,4));
            }
        });
        lymonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyyear.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                lymonth.setBackgroundColor(getResources().getColor(R.color.bg_white));
                lyday.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                tvyear.setTextColor(getResources().getColor(R.color.bg_white));
                tvmonth.setTextColor(getResources().getColor(R.color.darkGreen));
                tvday.setTextColor(getResources().getColor(R.color.bg_white));
                tvout.setClickable(true);
                tvin.setClickable(true);
                getItems(selectDate[0].substring(0, 7));
            }
        });
        lyday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyyear.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                lymonth.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                lyday.setBackgroundColor(getResources().getColor(R.color.bg_white));
                tvyear.setTextColor(getResources().getColor(R.color.bg_white));
                tvmonth.setTextColor(getResources().getColor(R.color.bg_white));
                tvday.setTextColor(getResources().getColor(R.color.darkGreen));
                getItems(selectDate[0]);
            }
        });

        tvdate.setText(date);
        timeStamp = timeToStamp(date);

        arrTop = CashbookDatabase.getInstence().searchMoney(userId, date, inorout);
        drawChar();
        rank(date);

        return view;
    }

    public void rank(String date) {
        array = CashbookDatabase.getInstence().ranking(userId, date, inorout);
        if (adapter == null) {
            adapter = new MyAdapter();
            lv.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    public void drawChar() {
        chart.clear();
        chart.setUsePercentValues(false);
        chart.getDescription().setEnabled(false);
        chart.setTransparentCircleRadius(0f);
        chart.setHoleRadius(0f);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        chart.animateY(1000);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setEnabled(true);

        ArrayList<PieEntry> entries = new ArrayList<>();
        double totals = 0;
        for (int i = 0; i < arrTop.length(); i++) {
            JSONObject object = arrTop.optJSONObject(i);
            totals += object.optDouble("total");
        }

        for (int i = 0; i < arrTop.length(); i++) {
            JSONObject object = arrTop.optJSONObject(i);
            PieEntry barEntry = new PieEntry((float) (object.optDouble("total") / totals * 100),
                    object.optString("type") + " " + object.optDouble("total"));
            entries.add(barEntry);
        }

        chart.setEntryLabelColor(R.color.darkGreen);
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(1f);

        //旋转角度
        chart.setRotationAngle(-30);


        //colors
        int endColor1 = ContextCompat.getColor(getActivity(), R.color.chart1);
        int endColor2 = ContextCompat.getColor(getActivity(), R.color.chart2);
        int endColor3 = ContextCompat.getColor(getActivity(), R.color.chart3);
        int endColor4 = ContextCompat.getColor(getActivity(), R.color.chart4);
        int endColor5 = ContextCompat.getColor(getActivity(), R.color.chart5);
        int endColor6 = ContextCompat.getColor(getActivity(), R.color.chart6);
        int endColor7 = ContextCompat.getColor(getActivity(), R.color.chart7);
        int endColor8 = ContextCompat.getColor(getActivity(), R.color.chart8);
        int endColor9 = ContextCompat.getColor(getActivity(), R.color.chart9);
        int endColor10 = ContextCompat.getColor(getActivity(), R.color.chart10);
        int endColor11 = ContextCompat.getColor(getActivity(), R.color.chart11);
        int endColor12 = ContextCompat.getColor(getActivity(), R.color.chart12);
        int endColor13 = ContextCompat.getColor(getActivity(), R.color.chart13);
        int endColor14 = ContextCompat.getColor(getActivity(), R.color.chart14);
        int endColor15 = ContextCompat.getColor(getActivity(), R.color.chart15);


        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(endColor1);
        colors.add(endColor2);
        colors.add(endColor3);
        colors.add(endColor4);
        colors.add(endColor5);
        colors.add(endColor6);
        colors.add(endColor7);
        colors.add(endColor8);
        colors.add(endColor9);
        colors.add(endColor10);
        colors.add(endColor11);
        colors.add(endColor12);
        colors.add(endColor13);
        colors.add(endColor14);
        colors.add(endColor15);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);

        chart.setDrawEntryLabels(true);
        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);//
        chart.setData(data);
        chart.setDrawSliceText(false);
        chart.highlightValues(null);
        chart.invalidate();
    }

    public void getItems(String time) {
        arrTop = CashbookDatabase.getInstence().searchMoney(userId, time, inorout);
        drawChar();
        rank(time);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tvout) {
            inorout = "output";
            tvout.setTextColor(getResources().getColor(R.color.darkGreen));
            tvout.setBackgroundColor(getResources().getColor(R.color.bg_white));
            tvin.setBackgroundColor(getResources().getColor(R.color.lightGreen));
            tvin.setTextColor(getResources().getColor(R.color.bg_white));
            arrTop = CashbookDatabase.getInstence().searchMoney(userId, date, inorout);

            drawChar();
            rank(date);
        } else if (v.getId() == R.id.tvin) {
            inorout = "input";
            tvout.setTextColor(getResources().getColor(R.color.bg_white));
            tvout.setBackgroundColor(getResources().getColor(R.color.lightGreen));
            tvin.setBackgroundColor(getResources().getColor(R.color.bg_white));
            tvin.setTextColor(getResources().getColor(R.color.darkGreen));
            arrTop = CashbookDatabase.getInstence().searchMoney(userId, date, inorout);
            drawChar();
            rank(date);
        }

    }

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

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return array.length();
        }

        @Override
        public Object getItem(int position) {
            return array.opt(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.items_single, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            JSONObject object = array.optJSONObject(position);
            viewHolder.type.setText(object.optString("type"));
            viewHolder.time.setText("第" + (position + 1) + "名");
            viewHolder.money.setText(object.optString("total"));

            return convertView;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        ImageView iconItem;
        TextView type;
        TextView money;

        public ViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.single_time);
            iconItem = view.findViewById(R.id.single_icitem);
            type = view.findViewById(R.id.single_type);
            money = view.findViewById(R.id.single_money);
        }
    }
}
