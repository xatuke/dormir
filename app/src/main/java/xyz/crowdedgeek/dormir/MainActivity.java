package xyz.crowdedgeek.dormir;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.AlarmClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity {

    TextView waketv, sleeptv;
    String ti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.ac_bar);
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
                    public void onClick(DialogInterface dialog, int which) {
                        ti = String.valueOf(picker.getHour()).concat(String.valueOf(picker.getMinute()));
                        String times[];
                        if(t) {
                            times = getWakeTimes(Integer.valueOf(ti));
                        } else {
                            times = getSleepTimes(Integer.valueOf(ti));
                        }
                        AlertDialog.Builder bl = new AlertDialog.Builder(MainActivity.this);
                        View v = View.inflate(MainActivity.this, R.layout.times, null);
                        final TextView t1 = v.findViewById(R.id.time1);
                        final TextView t2 = v.findViewById(R.id.time2);
                        final TextView t3 = v.findViewById(R.id.time3);
                        final TextView t4 = v.findViewById(R.id.time4);
                        t1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int[] times = new int[2];
                                times[0] = Integer.valueOf(t1.getText().toString().split(":")[0]);
                                times[1] = Integer.valueOf(t1.getText().toString().split(":")[1]);
                                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                                i.putExtra(AlarmClock.EXTRA_HOUR, times[0]);
                                i.putExtra(AlarmClock.EXTRA_MINUTES, times[1]);
                                startActivity(i);
                            }
                        });
                        t2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int[] times = new int[2];
                                times[0] = Integer.valueOf(t2.getText().toString().split(":")[0]);
                                times[1] = Integer.valueOf(t2.getText().toString().split(":")[1]);
                                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                                i.putExtra(AlarmClock.EXTRA_HOUR, times[0]);
                                i.putExtra(AlarmClock.EXTRA_MINUTES, times[1]);
                                startActivity(i);
                            }
                        });
                        t3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int[] times = new int[2];
                                times[0] = Integer.valueOf(t3.getText().toString().split(":")[0]);
                                times[1] = Integer.valueOf(t3.getText().toString().split(":")[1]);
                                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                                i.putExtra(AlarmClock.EXTRA_HOUR, times[0]);
                                i.putExtra(AlarmClock.EXTRA_MINUTES, times[1]);
                                startActivity(i);
                            }
                        });
                        t4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int[] times = new int[2];
                                times[0] = Integer.valueOf(t4.getText().toString().split(":")[0]);
                                times[1] = Integer.valueOf(t4.getText().toString().split(":")[1]);
                                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                                i.putExtra(AlarmClock.EXTRA_HOUR, times[0]);
                                i.putExtra(AlarmClock.EXTRA_MINUTES, times[1]);
                                startActivity(i);
                            }
                        });
                        t1.setText(times[0]);
                        t2.setText(times[1]);
                        t3.setText(times[2]);
                        t4.setText(times[3]);
                        View vw = View.inflate(MainActivity.this, R.layout.title, null);
                        if(t){
                            ((TextView)vw.findViewById(R.id.wak_tv)).setText("wake at:");
                        } else {
                            ((TextView)vw.findViewById(R.id.wak_tv)).setText("sleep at:");
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
            ((TextView)vw.findViewById(R.id.wak_tv)).setText("wake at?");
        }
        b.setCustomTitle(vw);
        b.show();
    }


    private String[] getSleepTimes(Integer ti) {
        String res[] = new String[4];
        if(ti>900){
            res[0]=String.valueOf(ti-900);
            res[1]=String.valueOf(ti-730);
            res[2]=String.valueOf(ti-600);
            res[3]=String.valueOf(ti-430);
        } else {
            res[0]=String.valueOf(ti-900);
            res[1]=String.valueOf(ti-730);
            res[2]=String.valueOf(ti-600);
            res[3]=String.valueOf(ti-430);
            if(Integer.valueOf(res[0])<0){
                res[0] = String.valueOf(Integer.valueOf(res[0])+2400);
            }
            if(Integer.valueOf(res[1])<0){
                res[1] = String.valueOf(Integer.valueOf(res[1])+2400);
            }
            if(Integer.valueOf(res[2])<0){
                res[2] = String.valueOf(Integer.valueOf(res[2])+2400);
            }
            if(Integer.valueOf(res[3])<0){
                res[3] = String.valueOf(Integer.valueOf(res[3])+2400);
            }
        }
        if((Integer.valueOf(res[1])%100)>60){
            res[1] = String.valueOf(Integer.valueOf(res[1])-40);

        }
        if((Integer.valueOf(res[3])%100)>60){
            res[3] = String.valueOf(Integer.valueOf(res[3])-40);

        }
        if(Integer.valueOf(res[0])<60){
            res[0] = "00"+String.valueOf(Integer.valueOf(res[0]));
        }
        if(Integer.valueOf(res[1])<60){
            res[1] = "00"+String.valueOf(Integer.valueOf(res[1]));
        }
        if(Integer.valueOf(res[2])<60){
            res[2] = "00"+String.valueOf(Integer.valueOf(res[2]));
        }
        if(Integer.valueOf(res[3])<60){
            res[3] = "00"+String.valueOf(Integer.valueOf(res[3]));
        }
        if(Integer.valueOf(res[0])<1000){
            res[0] = "0"+String.valueOf(Integer.valueOf(res[0]));
        }
        if(Integer.valueOf(res[1])<1000){
            res[1] = "0"+String.valueOf(Integer.valueOf(res[1]));
        }
        if(Integer.valueOf(res[2])<1000){
            res[2] = "0"+String.valueOf(Integer.valueOf(res[2]));
        }
        if(Integer.valueOf(res[3])<1000){
            res[3] = "0"+String.valueOf(Integer.valueOf(res[3]));
        }
        res[0]=String.valueOf(Integer.valueOf(res[0])/100)+":"+String.valueOf(Integer.valueOf(res[0])%100);
        res[1]=String.valueOf(Integer.valueOf(res[1])/100)+":"+String.valueOf(Integer.valueOf(res[1])%100);
        res[2]=String.valueOf(Integer.valueOf(res[2])/100)+":"+String.valueOf(Integer.valueOf(res[2])%100);
        res[3]=String.valueOf(Integer.valueOf(res[3])/100)+":"+String.valueOf(Integer.valueOf(res[3])%100);
        return res;
    }

    private String[] getWakeTimes(Integer ti) {
        String res[] = new String[4];
        if(ti<1970){
            res[0]=String.valueOf(ti+900);
            res[1]=String.valueOf(ti+730);
            res[2]=String.valueOf(ti+600);
            res[3]=String.valueOf(ti+430);

        } else {
            ti = ti-2400;
            res[0]=String.valueOf(ti+900);
            res[1]=String.valueOf(ti+730);
            res[2]=String.valueOf(ti+600);
            res[3]=String.valueOf(ti+430);
        }
        if((Integer.valueOf(res[1])%100)>60){
            res[1] = String.valueOf(Integer.valueOf(res[1])+40);

        }
        if((Integer.valueOf(res[3])%100)>60){
            res[3] = String.valueOf(Integer.valueOf(res[3])+40);

        }
        if(Integer.valueOf(res[0])>2400){
            res[0] = String.valueOf(Integer.valueOf(res[0])-2400);
        }
        if(Integer.valueOf(res[1])>2400){
            res[1] = String.valueOf(Integer.valueOf(res[1])-2400);
        }
        if(Integer.valueOf(res[2])>2400){
            res[2] = String.valueOf(Integer.valueOf(res[2])-2400);
        }
        if(Integer.valueOf(res[3])>2400){
            res[3] = String.valueOf(Integer.valueOf(res[3])-2400);
        }
        if(Integer.valueOf(res[0])<60){
            res[0] = "00"+String.valueOf(Integer.valueOf(res[0]));
        }
        if(Integer.valueOf(res[1])<60){
            res[1] = "00"+String.valueOf(Integer.valueOf(res[1]));
        }
        if(Integer.valueOf(res[2])<60){
            res[2] = "00"+String.valueOf(Integer.valueOf(res[2]));
        }
        if(Integer.valueOf(res[3])<60){
            res[3] = "00"+String.valueOf(Integer.valueOf(res[3]));
        }
        if(Integer.valueOf(res[0])<1000){
            res[0] = "0"+String.valueOf(Integer.valueOf(res[0]));
        }
        if(Integer.valueOf(res[1])<1000){
            res[1] = "0"+String.valueOf(Integer.valueOf(res[1]));
        }
        if(Integer.valueOf(res[2])<1000){
            res[2] = "0"+String.valueOf(Integer.valueOf(res[2]));
        }
        if(Integer.valueOf(res[3])<1000){
            res[3] = "0"+String.valueOf(Integer.valueOf(res[3]));
        }
        res[0]=String.valueOf(Integer.valueOf(res[0])/100)+":"+String.valueOf(Integer.valueOf(res[0])%100);
        res[1]=String.valueOf(Integer.valueOf(res[1])/100)+":"+String.valueOf(Integer.valueOf(res[1])%100);
        res[2]=String.valueOf(Integer.valueOf(res[2])/100)+":"+String.valueOf(Integer.valueOf(res[2])%100);
        res[3]=String.valueOf(Integer.valueOf(res[3])/100)+":"+String.valueOf(Integer.valueOf(res[3])%100);
        return res;
    }
}
