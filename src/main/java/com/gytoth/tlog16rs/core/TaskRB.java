package com.gytoth.tlog16rs.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
public class TaskRB {
    
    private int year;
    private int month;
    private int day;
    private String taskId;
    private String startTime;
    private String comment;

}
