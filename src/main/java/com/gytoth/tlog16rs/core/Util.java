package com.gytoth.tlog16rs.core;

import java.util.Calendar;
import java.util.List;
import com.gytoth.tlog16rs.entities.Task;
import com.gytoth.tlog16rs.entities.WorkDay;

public class Util {

	private Util() {
	}

	public static final int QUARTER_HOUR = 15;

	public static boolean isWeekday(WorkDay workDay) {
		return workDay.getActualDay().getDayOfWeek().getValue() < Calendar.SATURDAY;
	}

	/**
	 * Checks whether 2 tasks' times are overlapping or not. 3 cases give false (overlapping) result: The task's startTime is between the existingTask's startTime and endTime The task's endTime is between the existingTask's startTime and endTime The task's startTime AND endTime are both between the existingTask's startTime and endTime
	 *
	 * @param task Task to be examined
	 * @param tasks List of tasks to be compared with
	 * @return
	 */
	public static boolean isSeparatedTime(Task task, List<Task> tasks) {
		for (Task existingTask : tasks) {
			if ((task.getStartTime().isAfter(existingTask.getStartTime()) && task.getStartTime().isBefore(existingTask.getEndTime()))
			|| (task.getEndTime().isAfter(existingTask.getStartTime()) && task.getEndTime().isBefore(existingTask.getEndTime()))
			|| (task.getStartTime().isBefore(existingTask.getStartTime()) && task.getEndTime().isAfter(existingTask.getEndTime()))) {
				return false;
			}
		}
		return true;
	}
}
