package xyz.crowdedgeek.dormir;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.AlarmClock;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private String time;
    private String base64EncodedPublicKey = "YOUR_KEY_HERE";
    private String sku;
    private IInAppBillingService mService;
    public static final int REQUEST_CODE = 23423;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.action_bar);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        findViewById(R.id.fab_donate).setOnClickListener(v -> getDonations());
        findViewById(R.id.tv_wake_up).setOnClickListener(v -> selectTimeDialog(true));
        findViewById(R.id.tv_sleep).setOnClickListener(v -> selectTimeDialog(false));

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void getDonations() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View v = View.inflate(MainActivity.this, R.layout.alert_donate, null);
        builder.setView(v);
        builder.setPositiveButton(R.string.donate, (dialog, which) -> {
            sku = "";
            boolean go = true;
            RadioGroup radioGroup = v.findViewById(R.id.which_food);
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.coffee:
                    sku = getString(R.string.coffee);
                    break;
                case R.id.lunch:
                    sku = getString(R.string.lunch);
                    break;
                case R.id.meal:
                    sku = getString(R.string.meal);
                    break;
                default:
                    go = false;
                    Toast.makeText(MainActivity.this, getString(R.string.please_select_an_amount), Toast.LENGTH_SHORT).show();
                    getDonations();
                    break;
            }
            if (go)
                if (isNetworkAvailable()) {
                    Bundle buyIntentBundle = null;
                    try {
                        buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                                sku, "inapp", base64EncodedPublicKey);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if (buyIntentBundle != null) {
                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                        try {
                            startIntentSenderForResult(pendingIntent.getIntentSender(),
                                    REQUEST_CODE, new Intent(), 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(MainActivity.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void selectTimeDialog(final boolean t) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.alert_time_picker, null);
        final TimePicker picker = view.findViewById(R.id.time_picker);
        builder.setView(view);
        builder.setPositiveButton(R.string.calculate, (dialog, which) -> {
            time = String.valueOf(picker.getHour()).concat(":").concat(String.valueOf(picker.getMinute()));
            String times[];
            if (t)
                times = getWakeTimes(time);
            else
                times = getSleepTimes(time);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            View view1 = View.inflate(MainActivity.this, R.layout.alert_times, null);
            final TextView time = view1.findViewById(R.id.alert_tv_time);
            final TextView nCycles = view1.findViewById(R.id.alert_tv_cycles);
            final TextView timeTaken = view1.findViewById(R.id.alert_tv_time_taken);
            final ImageView addAlarm = view1.findViewById(R.id.alert_img_add_alarm);
            final ImageView nextTime = view1.findViewById(R.id.alert_img_next_time);
            final ImageView prevTime = view1.findViewById(R.id.alert_img_prev_time);
            time.setText(times[0]);
            nCycles.setText(getString(R.string.cycles, 6));
            timeTaken.setText(getString(R.string.time_taken, 9, 15));
            prevTime.setVisibility(View.GONE);
            prevTime.setEnabled(false);
            addAlarm.setOnClickListener(v1 -> {
                String tm = time.getText().toString();
                if (time.getText().toString().contains("AM") || time.getText().toString().contains("PM"))
                    tm = convertTo24Hr(time.getText().toString());
                addAlarm(tm);
            });

            nextTime.setOnClickListener(v1 -> {
                if (time.getText().toString().equals(times[0])) {
                    time.setText(times[1]);
                    prevTime.setVisibility(View.VISIBLE);
                    prevTime.setEnabled(true);
                    nCycles.setText(getString(R.string.cycles, 5));
                    timeTaken.setText(getString(R.string.time_taken, 7, 45));
                } else if (time.getText().toString().equals(times[1])) {
                    time.setText(times[2]);
                    nCycles.setText(getString(R.string.cycles, 4));
                    timeTaken.setText(getString(R.string.time_taken, 6, 15));
                } else if (time.getText().toString().equals(times[2])) {
                    time.setText(times[3]);
                    nCycles.setText(getString(R.string.cycles, 3));
                    timeTaken.setText(getString(R.string.time_taken, 4, 45));
                    nextTime.setEnabled(false);
                    nextTime.setVisibility(View.GONE);
                }
            });

            prevTime.setOnClickListener(v1 -> {
                if (time.getText().toString().equals(times[1])) {
                    prevTime.setVisibility(View.GONE);
                    time.setText(times[0]);
                    nCycles.setText(getString(R.string.cycles, 6));
                    timeTaken.setText(getString(R.string.time_taken, 9, 15));
                    prevTime.setEnabled(false);
                } else if (time.getText().toString().equals(times[2])) {
                    time.setText(times[1]);
                    nCycles.setText(getString(R.string.cycles, 5));
                    timeTaken.setText(getString(R.string.time_taken, 7, 45));
                } else if (time.getText().toString().equals(times[3])) {
                    time.setText(times[2]);
                    nCycles.setText(getString(R.string.cycles, 4));
                    timeTaken.setText(getString(R.string.time_taken, 6, 15));
                    nextTime.setEnabled(true);
                    nextTime.setVisibility(View.VISIBLE);
                }
            });

            View view2 = View.inflate(MainActivity.this, R.layout.alert_title, null);
            if (t)
                ((TextView) view2.findViewById(R.id.alert_tv_title)).setText(getString(R.string.wake_up_at_));
            else
                ((TextView) view2.findViewById(R.id.alert_tv_title)).setText(getString(R.string.sleep_at_));
            builder1.setCustomTitle(view2);
            builder1.setView(view1);
            builder1.show();
        });
        View view1 = View.inflate(MainActivity.this, R.layout.alert_title, null);
        if (t)
            ((TextView) view1.findViewById(R.id.alert_tv_title)).setText(getString(R.string.sleep_at));
        else
            ((TextView) view1.findViewById(R.id.alert_tv_title)).setText(getString(R.string.wake_up_at));
        builder.setCustomTitle(view1);
        builder.show();
    }

    private String convertTo24Hr(String s) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = displayFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return displayFormat.format(date);
    }

    private void addAlarm(String time) {
        int hours = Integer.valueOf(time.split(":")[0]);
        int minutes = Integer.valueOf(time.split(":")[1].substring(0,2));
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_HOUR, hours);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        startActivity(i);
    }

    private String[] getSleepTimes(String time) {
        String res[] = new String[4];
        int hour = Integer.valueOf(time.split(":")[0]);
        int min = Integer.valueOf(time.split(":")[1]);
        TimeArithmetic arithmetic = new TimeArithmetic(hour, min, false);

        res[0] = arithmetic.addOrSub(-9, -15);
        res[1] = arithmetic.addOrSub(-7, -45);
        res[2] = arithmetic.addOrSub(-6, -15);
        res[3] = arithmetic.addOrSub(-4, -45);

        return res;
    }

    private String[] getWakeTimes(String time) {
        String res[] = new String[4];
        int hour = Integer.valueOf(time.split(":")[0]);
        int min = Integer.valueOf(time.split(":")[1]);
        TimeArithmetic arithmetic = new TimeArithmetic(hour, min, false);

        res[0] = arithmetic.addOrSub(9, 15);
        res[1] = arithmetic.addOrSub(7, 45);
        res[2] = arithmetic.addOrSub(6, 15);
        res[3] = arithmetic.addOrSub(4, 45);

        return res;
    }

}
