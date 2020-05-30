package com.homework.containers;

import com.homework.containers.dto.EventDTO;
import com.homework.containers.pcontainers.GroupTopic;
import com.homework.enums.StatesEnum;

import java.util.ArrayList;

public class ProgrammingContainer {
    public EventDTO event;
    public String[] group_topics;
    public String group_city;
    public String group_country;
    public int group_id;
    public String group_name;
    public String group_state;

    public ProgrammingContainer(Meeting meeting) {
        this.event = new EventDTO(meeting.event);
        this.group_city = meeting.group.group_city;
        this.group_country = meeting.group.group_country;
        this.group_name = meeting.group.group_name;
        this.group_state = StatesEnum.valueOfAbbreviation(meeting.group.group_state.toUpperCase()).getName();
        this.group_id = meeting.group.group_id;
        ArrayList<String> g_topic = new ArrayList<>();
        for (GroupTopic g : meeting.group.group_topics) {
            g_topic.add(g.topic_name);
        }
        this.group_topics = g_topic.toArray(new String[0]);
    }
}
