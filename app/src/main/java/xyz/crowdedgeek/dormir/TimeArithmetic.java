package xyz.crowdedgeek.dormir;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeArithmetic {
    private int currHour;
    private int currMinute;
    private boolean is24Hour;

    TimeArithmetic(int currHour, int currMinute, boolean is24Hour){
        this.currHour = currHour;
        this.currMinute = currMinute;
        this.is24Hour = is24Hour;
    }

    public String addOrSub(int hours, int minutes){
        String res = "";
        Calendar calendar = new GregorianCalendar(2000, 0, 1, this.currHour, this.currMinute);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.MINUTE, minutes);
        if(this.is24Hour){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            res = sdf.format(calendar.getTime());
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aaa");
            res = sdf.format(calendar.getTime());
        }
        return res;
    }

}
