package com.gytoth.tlog16rs.core;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.gytoth.tlog16rs.entities.Task;
import com.gytoth.tlog16rs.entities.WorkDay;
import com.gytoth.tlog16rs.entities.WorkMonth;

public class Util {

    public static int quarterHour = 15;

    public static boolean isMultipleQuarterHour(Task task) {
        return task.getMinPerTask() % quarterHour == 0;
    }

    public static boolean isWeekday(WorkDay workDay) {
        return workDay.getActualDay().getDayOfWeek().getValue() < Calendar.SATURDAY;
    }

    /**
     * If a task !isMultipleQuarterHour, changes the endTime, so the getMinPerTask will be a multiple of 15.
     *
     * @param task
     */
    public static void roundToMultipleQuarterHour(Task task) {
        if (!isMultipleQuarterHour(task)) {
            int halfQuarter = 7;

            long remainder = task.getMinPerTask() % quarterHour;
            if (remainder <= halfQuarter) {
                try {
                    task.setEndTime(task.getEndTime().minusMinutes(remainder));
                } catch (NotExpectedTimeOrderException | EmptyTimeFieldException ex) {
                    Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    task.setEndTime(task.getEndTime().plusMinutes(quarterHour - remainder));
                } catch (NotExpectedTimeOrderException | EmptyTimeFieldException ex) {
                    Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

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

    /**
     * Chooses the month from a list, which the user wants to work with
     *
     * @param monthNumber Value assigned through user input. The user wants the month which's monthValue equals monthNumber.
     * @param months List of the months the user can select from.
     * @param selectedMonth
     * @return selectedMonth Equals the month which's number equals monthNumber.
     */
    public static WorkMonth selectMonth(List<WorkMonth> months, WorkMonth selectedMonth, int monthNumber) {
        for (int i = 0; i < months.size(); i++) {
            if (months.get(i).getDate().getMonthValue() == monthNumber) {
                selectedMonth = months.get(i);
            }
        }
        return selectedMonth;
    }

    /**
     * Chooses the day from a list, which the user wants to work with
     *
     * @param dayNumber Value assigned through user input. The user wants the day which's dayOfMonth equals monthNumber.
     * @param days List of the months the user can select from.
     * @param selectedDay
     * @return selectedDay Equals the day which's number equals monthNumber
     */
    public static WorkDay selectDay(List<WorkDay> days, WorkDay selectedDay, int dayNumber) {
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).getActualDay().getDayOfMonth() == dayNumber) {
                selectedDay = days.get(i);
            }
        }
        return selectedDay;
    }

    /**
     * Chooses the task from a list, which the user wants to work with
     *
     * @param selectedId Value assigned through user input. The user wants the task which's ID equals selectedId.
     * @param tasks List of the tasks the user can choose from
     * @param selectedTask
     * @return selectedTask Equals the task which's taskId equals selectedId
     */
    public static Task selectTask(Task selectedTask, String selectedId, List<Task> tasks) {

        for (Task task : tasks) {
            if (task.getTaskId().equals(selectedId)) {
                selectedTask = task;
                break;
            }
        }
        return selectedTask;

    }

}
