package xyz.crowdedgeek.dormir;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView waketv, sleeptv;
    String ti;
    ImageView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.ac_bar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        waketv = findViewById(R.id.tv_wake);
        sleeptv = findViewById(R.id.tv_sleep);
        waketv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeDialog(true);
            }
        });

        sleeptv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeDialog(false);
            }
        });

    }

    private void selectTimeDialog(final boolean t) {
        final AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        View v = View.inflate(MainActivity.this, R.layout.ald_tp, null);
        final TimePicker picker = v.findViewById(R.id.timep);
        b.setView(v);
        b.setPositiveButton("calculate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        if(Build.VERSION.SDK_INT < 23){
                            ti = String.valueOf(picker.getCurrentHour()).concat(":").concat(String.valueOf(picker.getCurrentMinute()));
                        } else{
                            ti = String.valueOf(picker.getHour()).concat(":").concat(String.valueOf(picker.getMinute()));
                        }
                        String times[];
                        if(t){
                            times = getWakeTimes(ti);
                        }else{
                            times = getSleepTimes(ti);
                        }
                        AlertDialog.Builder bl = new AlertDialog.Builder(MainActivity.this);
                        View v = View.inflate(MainActivity.this, R.layout.times, null);
                        final TextView time = v.findViewById(R.id.time);
                        final TextView nCycles = v.findViewById(R.id.n_cycles);
                        final TextView timeTaken = v.findViewById(R.id.time_taken);
                        final ImageView addAlarm = v.findViewById(R.id.add_al);
                        final ImageView nextTime = v.findViewById(R.id.next_time);
                        final ImageView prevTime = v.findViewById(R.id.prev_time);
                        time.setText(times[0]);
                        nCycles.setText("6 CYCLES");
                        timeTaken.setText("9h 15m");
                        prevTime.setVisibility(View.GONE);
                        prevTime.setEnabled(false);
                        addAlarm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String tm = time.getText().toString();
                                if(time.getText().toString().contains("AM") || time.getText().toString().contains("PM")) {
                                    tm = convertTo24Hr(time.getText().toString());
                                }
                                addAlarm(tm);
                            }
                        });

                        nextTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(time.getText().toString().equals(times[0])){
                                    time.setText(times[1]);
                                    prevTime.setVisibility(View.VISIBLE);
                                    prevTime.setEnabled(true);
                                    nCycles.setText("5 CYCLES");
                                    timeTaken.setText("7h 45m");
                                } else if(time.getText().toString().equals(times[1])){
                                    time.setText(times[2]);
                                    nCycles.setText("4 CYCLES");
                                    timeTaken.setText("6h 15m");
                                } else if(time.getText().toString().equals(times[2])){
                                    time.setText(times[3]);
                                    nCycles.setText("3 CYCLES");
                                    timeTaken.setText("4h 45m");
                                    nextTime.setEnabled(false);
                                    nextTime.setVisibility(View.GONE);
                                }
                            }
                        });

                        prevTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(time.getText().toString().equals(times[1])){
                                    prevTime.setVisibility(View.GONE);
                                    time.setText(times[0]);
                                    nCycles.setText("6 CYCLES");
                                    timeTaken.setText("9h 15m");
                                    prevTime.setEnabled(false);
                                } else if(time.getText().toString().equals(times[2])){
                                    time.setText(times[1]);
                                    nCycles.setText("5 CYCLES");
                                    timeTaken.setText("7h 45m");
                                } else if(time.getText().toString().equals(times[3])){
                                    time.setText(times[2]);
                                    nCycles.setText("4 CYCLES");
                                    timeTaken.setText("6h 15m");
                                    nextTime.setEnabled(true);
                                    nextTime.setVisibility(View.VISIBLE);
                                }
                            }
                        });


                        View vw = View.inflate(MainActivity.this, R.layout.title, null);
                        if(t){
                            ((TextView)vw.findViewById(R.id.wak_tv)).setText("wake up at");
                        } else {
                            ((TextView)vw.findViewById(R.id.wak_tv)).setText("sleep at");
                        }
                        bl.setCustomTitle(vw);
                        bl.setView(v);
                        bl.show();
                    }
                });
        View vw = View.inflate(MainActivity.this, R.layout.title, null);
        if(t){
            ((TextView)vw.findViewById(R.id.wak_tv)).setText("sleep at?");
        } else {
            ((TextView)vw.findViewById(R.id.wak_tv)).setText("wake up at?");
        }
        b.setCustomTitle(vw);
        b.show();
    }

    private String convertTo24Hr(String s) {
        String res = s;
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = displayFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        res = displayFormat.format(date);
        return res;
    }

    private void addAlarm(String tm) {
        int hours = Integer.valueOf(tm.split(":")[0]);
        int minutes = Integer.valueOf(tm.split(":")[1]);
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_HOUR, hours);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        startActivity(i);
    }


    private String[] getSleepTimes(String ti) {
        String res[] = new String[4];
        int hour = Integer.valueOf(ti.split(":")[0]);
        int min = Integer.valueOf(ti.split(":")[1]);
        TimeArithmetic arithmetic = new TimeArithmetic(hour, min, false);

        res[0]=arithmetic.addOrSub(-9, -15);
        res[1]=arithmetic.addOrSub(-7, -45);
        res[2]=arithmetic.addOrSub(-6, -15);
        res[3]=arithmetic.addOrSub(-4, -45);

        return res;
    }

    private String[] getWakeTimes(String ti) {
        String res[] = new String[4];
        int hour = Integer.valueOf(ti.split(":")[0]);
        int min = Integer.valueOf(ti.split(":")[1]);
        TimeArithmetic arithmetic = new TimeArithmetic(hour, min, false);

        res[0]=arithmetic.addOrSub(9, 15);
        res[1]=arithmetic.addOrSub(7, 45);
        res[2]=arithmetic.addOrSub(6, 15);
        res[3]=arithmetic.addOrSub(4, 45);

        return res;
    }
}
