package com.homework.containers;

import java.text.SimpleDateFormat;
import java.util.List;

public class WindowPerMinuteContainer {
    public String month;
    public int day_of_the_month;
    public int hour;
    public int minute;
    public String[] cities;
    SimpleDateFormat sdf_month = new SimpleDateFormat("MMMM");
    SimpleDateFormat sdf_d = new SimpleDateFormat("d");
    SimpleDateFormat sdf_h = new SimpleDateFormat("h");
    SimpleDateFormat sdf_m = new SimpleDateFormat("mm");

    public WindowPerMinuteContainer(long timestamp, List<String> cities) {
        this.cities = cities.toArray(new String[0]);
        this.month = sdf_month.format(timestamp);
        this.hour = Integer.parseInt(sdf_h.format(timestamp));
        this.minute = Integer.parseInt(sdf_m.format(timestamp));
        this.day_of_the_month = Integer.parseInt(sdf_d.format(timestamp));
    }
}
