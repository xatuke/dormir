package xyz.crowdedgeek.dormir;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity {

    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.action_bar);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        findViewById(R.id.tv_wake).setOnClickListener(v -> selectTimeDialog(true));
        findViewById(R.id.tv_sleep).setOnClickListener(v -> selectTimeDialog(false));
    }

    private void selectTimeDialog(final boolean t) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.alert_time_picker, null);
        final TimePicker picker = view.findViewById(R.id.time_picker);
        picker.setIs24HourView(true);
        builder.setView(view);
        builder.setPositiveButton("calculate", (dialog, which) -> {
            if (picker.getMinute() < 10)
                time = String.valueOf(picker.getHour()).concat("0").concat(String.valueOf(picker.getMinute()));
            else
                time = String.valueOf(picker.getHour()).concat(String.valueOf(picker.getMinute()));
            String times[];
            if (t)
                times = getWakeTimes(Integer.valueOf(time));
            else
                times = getSleepTimes(Integer.valueOf(time));
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            View view1 = View.inflate(MainActivity.this, R.layout.alert_times, null);
            final TextView time1 = view1.findViewById(R.id.tv_time1);
            final TextView time2 = view1.findViewById(R.id.tv_time2);
            final TextView time3 = view1.findViewById(R.id.tv_time3);
            final TextView time4 = view1.findViewById(R.id.tv_time4);
            time1.setText(times[0]);
            time2.setText(times[1]);
            time3.setText(times[2]);
            time4.setText(times[3]);
            time1.setOnClickListener(v -> setAlarm((TextView) v));
            time2.setOnClickListener(v -> setAlarm((TextView) v));
            time3.setOnClickListener(v -> setAlarm((TextView) v));
            time4.setOnClickListener(v -> setAlarm((TextView) v));
            View title = View.inflate(MainActivity.this, R.layout.alert_title, null);
            if (t)
                ((TextView) title.findViewById(R.id.tv_alert_title)).setText(getString(R.string.wake).concat(" at:"));
            else
                ((TextView) title.findViewById(R.id.tv_alert_title)).setText(getString(R.string.sleep).concat(" at:"));
            builder1.setCustomTitle(title);
            builder1.setView(view1);
            builder1.show();
        });
        View title = View.inflate(MainActivity.this, R.layout.alert_title, null);
        if (t)
            ((TextView) title.findViewById(R.id.tv_alert_title)).setText(getString(R.string.sleep).concat(" at?"));
        else
            ((TextView) title.findViewById(R.id.tv_alert_title)).setText(getString(R.string.wake).concat(" at?"));
        builder.setCustomTitle(title);
        builder.show();
    }

    private String[] getSleepTimes(Integer time) {
        String res[] = new String[4];
        res[0] = String.valueOf(time - 900);
        res[1] = String.valueOf(time - 730);
        res[2] = String.valueOf(time - 600);
        res[3] = String.valueOf(time - 430);

        if (time < 900)
            for (int i = 0; i < res.length; i++)
                if (Integer.valueOf(res[i]) < 0)
                    res[i] = String.valueOf(Integer.valueOf(res[i]) + 2400);

        if ((Integer.valueOf(res[1]) % 100) > 60)
            res[1] = String.valueOf(Integer.valueOf(res[1]) - 40);

        if ((Integer.valueOf(res[3]) % 100) > 60)
            res[3] = String.valueOf(Integer.valueOf(res[3]) - 40);

        for (int i = 0; i < res.length; i++) {
            for (int j = 4 - res[i].length(); j > 0; j--)
                res[i] = "0".concat(res[i]);

            res[i] = res[i].substring(0, 2).concat(":").concat(res[i].substring(2));
        }

        return res;
    }

    private String[] getWakeTimes(Integer time) {
        if (time > 1970)
            time -= 2400;

        String res[] = new String[4];
        res[0] = String.valueOf(time + 900);
        res[1] = String.valueOf(time + 730);
        res[2] = String.valueOf(time + 600);
        res[3] = String.valueOf(time + 430);

        if ((Integer.valueOf(res[1]) % 100) > 60)
            res[1] = String.valueOf(Integer.valueOf(res[1]) + 40);

        if ((Integer.valueOf(res[3]) % 100) > 60)
            res[3] = String.valueOf(Integer.valueOf(res[3]) + 40);

        for (int i = 0; i < res.length; i++) {
            if (Integer.valueOf(res[i]) > 2400)
                res[i] = String.valueOf(Integer.valueOf(res[i]) - 2400);

            for (int j = 4 - res[i].length(); j > 0; j--)
                res[i] = "0".concat(res[i]);

            res[i] = res[i].substring(0, 2).concat(":").concat(res[i].substring(2));
        }

        return res;
    }

    private void setAlarm(TextView v) {
        String[] times;
        times = v.getText().toString().split(":");
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_HOUR, Integer.valueOf(times[0]));
        i.putExtra(AlarmClock.EXTRA_MINUTES, Integer.valueOf(times[1]));
        startActivity(i);
    }

}
