package com.homework.containers.dto;

import com.homework.containers.pcontainers.Event;

import java.text.SimpleDateFormat;

public class EventDTO {
    public String event_id;
    public String event_name;
    public String time;
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");

    public EventDTO(Event event) {
        this.event_id = event.event_id;
        this.event_name = event.event_name;
        this.time = sdf.format(event.time);
    }
}
