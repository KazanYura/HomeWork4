package com.homework.containers;

import com.homework.containers.dto.EventDTO;
import com.homework.enums.StatesEnum;

public class USMeetingContainer {
    public EventDTO event;
    public String group_city;
    public String group_country;
    public int group_id;
    public String group_name;
    public String group_state;

    public USMeetingContainer(Meeting meeting) {
        this.event = new EventDTO(meeting.event);
        this.group_city = meeting.group.group_city;
        this.group_id = meeting.group.group_id;
        this.group_country = meeting.group.group_country;
        this.group_state = StatesEnum.valueOfAbbreviation(meeting.group.group_state.toUpperCase()).getName();
        this.group_name = meeting.group.group_name;
    }
}
