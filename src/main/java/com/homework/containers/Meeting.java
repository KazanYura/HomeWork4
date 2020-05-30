package com.homework.containers;

import com.homework.containers.pcontainers.Event;
import com.homework.containers.pcontainers.Group;
import com.homework.containers.pcontainers.Member;
import com.homework.containers.pcontainers.Venue;

import java.math.BigInteger;

public class Meeting {
    public Venue venue;
    public String visibility;
    public String response;
    public int guests;
    public Member member;
    public int rsvp_id;
    public BigInteger mtime;
    public Event event;
    public Group group;
}
