package com.gytoth.tlog16rs.resources;

import com.gytoth.tlog16rs.core.DeleteTaskRB;
import com.gytoth.tlog16rs.core.EmptyTimeFieldException;
import com.gytoth.tlog16rs.core.FinishingTaskRB;
import com.gytoth.tlog16rs.core.FutureWorkDayException;
import com.gytoth.tlog16rs.core.InvalidTaskIdException;
import com.gytoth.tlog16rs.core.ModifyTaskRB;
import com.gytoth.tlog16rs.core.NoTaskIdException;
import com.gytoth.tlog16rs.core.NotExpectedTimeOrderException;
import com.gytoth.tlog16rs.core.NotNewDateException;
import com.gytoth.tlog16rs.core.NotNewMonthException;
import com.gytoth.tlog16rs.core.NotSameMonthException;
import com.gytoth.tlog16rs.core.NotSeparatedTimeException;
import com.gytoth.tlog16rs.core.Task;
import com.gytoth.tlog16rs.core.TaskRB;
import com.gytoth.tlog16rs.core.TimeLogger;
import com.gytoth.tlog16rs.core.WeekendNotEnabledException;
import com.gytoth.tlog16rs.core.WorkDay;
import com.gytoth.tlog16rs.core.WorkDayRB;
import com.gytoth.tlog16rs.core.WorkMonth;
import com.gytoth.tlog16rs.core.WorkMonthRB;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/timelogger/workmonths")
public class TLOG16RSResource {

    private TimeLogger timeLogger = new TimeLogger();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkMonth> getMonths() {
        return timeLogger.getMonths();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkMonth addNewMonth(WorkMonthRB month) {
        WorkMonth workMonth = new WorkMonth(month.getYear(), month.getMonth());
        try {
            timeLogger.addMonth(workMonth);
        } catch (NotNewMonthException ex) {
            Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex);
        }
        return workMonth;

    }

    @POST
    @Path("/workdays")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkDay addNewWorkDay(WorkDayRB day) {
        WorkDay workDay = null;

        try {

            workDay = new WorkDay(day.getYear(), day.getMonth(), day.getDay());
            WorkMonth month = timeLogger.getMonths().stream().filter(f -> f.getDate().getYear() == day.getYear() && f.getDate().getMonthValue() == day.getMonth()).findFirst().get();
            month.addWorkDay(workDay);

        } catch (FutureWorkDayException | WeekendNotEnabledException | NotNewDateException | NotSameMonthException ex) {
            Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex);
        } catch (NoSuchElementException ex) {

            WorkMonth notExistingMonth = new WorkMonth(day.getYear(), day.getMonth());
            try {
                timeLogger.addMonth(notExistingMonth);
                notExistingMonth.addWorkDay(workDay);
            } catch (NotNewMonthException | WeekendNotEnabledException | NotNewDateException | NotSameMonthException ex1) {
                Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex1);
            }
        }
        return workDay;
    }

    @GET
    @Path("/{year}/{month}")
    @Produces(MediaType.APPLICATION_JSON)
    public WorkMonth getSpecificMonth(@PathParam(value = "year") int year, @PathParam(value = "month") int month) {
        WorkMonth selectedMonth = null;
        try {
            selectedMonth = timeLogger.getMonths().stream().filter(f -> f.getDate().getYear() == year
                    && f.getDate().getMonthValue() == month)
                    .findFirst().get();
        } catch (NoSuchElementException ex) {
            Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex);
        }
        return selectedMonth;
    }

    @POST
    @Path("/workdays/tasks/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task startNewTask(TaskRB task) {

        Task workTask = null;
        WorkMonth month = null;
        WorkDay day = null;

        try {
            workTask = new Task(task.getTaskId(), task.getStartTime(), task.getStartTime(), task.getComment());
        } catch (NotExpectedTimeOrderException | InvalidTaskIdException | NoTaskIdException | EmptyTimeFieldException ex) {
            Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex);
        }

        try {
            month = timeLogger.getMonths().stream().filter(f -> f.getDate().getYear() == task.getYear() && f.getDate().getMonthValue() == task.getMonth()).findFirst().get();
        } catch (NoSuchElementException ex) {
            month = new WorkMonth(task.getYear(), task.getMonth());
            try {
                timeLogger.addMonth(month);
            } catch (NotNewMonthException ex1) {
                Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex1);
            }
        }

        try {
            day = month.getWorkDays().stream().filter(f -> f.getActualDay().getYear() == task.getYear() && f.getActualDay().getMonthValue() == task.getMonth()).findFirst().get();
        } catch (NoSuchElementException ex) {
            try {
                day = new WorkDay(task.getYear(), task.getMonth(), task.getDay());
                month.addWorkDay(day);
            } catch (FutureWorkDayException | WeekendNotEnabledException | NotNewDateException | NotSameMonthException ex1) {
                Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex1);
            }
        }

        try {
            day.addTask(workTask);
        } catch (NotSeparatedTimeException ex) {
            Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex);
        }
        return workTask;
    }

    @GET
    @Path("/{year}/{month}/{day}")
    @Produces(MediaType.APPLICATION_JSON)
    public WorkDay getSpecificWorkDay(@PathParam(value = "year") int year, @PathParam(value = "month") int month, @PathParam(value = "day") int day) {
        WorkDay selectedDay = null;

        try {
            WorkMonth selectedMonth = timeLogger.getMonths().stream().filter(f -> f.getDate().getYear() == year && f.getDate().getMonthValue() == month).findFirst().get();

            selectedDay = selectedMonth.getWorkDays().stream().filter(
                    f -> f.getActualDay().getYear() == selectedMonth.getDate().getYear()
                    && f.getActualDay().getMonthValue() == selectedMonth.getDate().getMonthValue()).
                    findFirst().get();

        } catch (NoSuchElementException ex) {
            Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex);
        }
        return selectedDay;
    }

    @PUT
    @Path("/workdays/tasks/finish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task finishTask(FinishingTaskRB task
    ) {

        Task selectedTask = null;
        WorkMonth month = null;
        WorkDay day = null;

        try {
            month = timeLogger.getMonths().stream().filter(f -> f.getDate().getYear() == task.getYear() && f.getDate().getMonthValue() == task.getMonth()).findFirst().get();
        } catch (NoSuchElementException ex) {
            month = new WorkMonth(task.getYear(), task.getMonth());
            try {
                timeLogger.addMonth(month);
            } catch (NotNewMonthException ex1) {
                Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex1);
            }
        }

        try {
            day = month.getWorkDays().stream().filter(f -> f.getActualDay().getYear() == task.getYear() && f.getActualDay().getMonthValue() == task.getMonth()).findFirst().get();
        } catch (NoSuchElementException ex) {
            try {
                day = new WorkDay(task.getYear(), task.getMonth(), task.getDay());
                month.addWorkDay(day);
            } catch (FutureWorkDayException | WeekendNotEnabledException | NotNewDateException | NotSameMonthException ex1) {
                Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex1);
            }
        }

        try {
            selectedTask = day.getTasks().stream().filter(f -> f.getTaskId().equals(task.getTaskId())).findFirst().get();
            selectedTask.setEndTime(task.getEndTime());
        } catch (NotExpectedTimeOrderException | EmptyTimeFieldException ex) {
            Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex);
        } catch (NoSuchElementException ex) {
            try {
                selectedTask = new Task(task.getTaskId(), task.getStartTime(), task.getEndTime(), "");
                day.addTask(selectedTask);
            } catch (NotExpectedTimeOrderException | InvalidTaskIdException | NoTaskIdException | EmptyTimeFieldException | NotSeparatedTimeException ex1) {
                Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return selectedTask;
    }

    @PUT
    @Path("/workdays/tasks/modify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task modifyTask(ModifyTaskRB task) {
        Task selectedTask = null;
        WorkMonth month = null;
        WorkDay day = null;

        try {
            month = timeLogger.getMonths().stream().filter(f -> f.getDate().getYear() == task.getYear() && f.getDate().getMonthValue() == task.getMonth()).findFirst().get();
        } catch (NoSuchElementException ex) {
            month = new WorkMonth(task.getYear(), task.getMonth());
            try {
                timeLogger.addMonth(month);
            } catch (NotNewMonthException ex1) {
                Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex1);
            }
            Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex);
        }

        try {
            day = month.getWorkDays().stream().filter(f -> f.getActualDay().getYear() == task.getYear() && f.getActualDay().getMonthValue() == task.getMonth()).findFirst().get();
        } catch (NoSuchElementException ex) {
            try {
                day = new WorkDay(task.getYear(), task.getMonth(), task.getDay());
                month.addWorkDay(day);
            } catch (FutureWorkDayException | WeekendNotEnabledException | NotNewDateException | NotSameMonthException ex1) {
                Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex1);
            }
            Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex);
        }

        try {
            selectedTask = day.getTasks().stream().filter(f -> f.getTaskId().equals(task.getTaskId())).findFirst().get();
            selectedTask.setEndTime(task.getNewEndTime());
            selectedTask.setTaskId(task.getNewTaskId());
            selectedTask.setStartTime(task.getNewStartTime());
            selectedTask.setComment(task.getNewComment());
        } catch (NoSuchElementException ex) {
            try {
                selectedTask = new Task(task.getNewTaskId(), task.getNewStartTime(), task.getNewEndTime(), task.getNewComment());
                day.addTask(selectedTask);
            } catch (NotExpectedTimeOrderException | InvalidTaskIdException | NoTaskIdException | EmptyTimeFieldException | NotSeparatedTimeException ex1) {
                Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex1);
            }
        } catch (InvalidTaskIdException | NoTaskIdException | NotExpectedTimeOrderException | EmptyTimeFieldException ex) {
            Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.CONFIG, null, ex);
        }

        return selectedTask;
    }

    @PUT
    @Path("workdays/tasks/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task deleteTask(DeleteTaskRB task
    ) {
        WorkMonth month = timeLogger.getMonths().stream().filter(f -> f.getDate().getYear() == task.getYear() && f.getDate().getMonthValue() == task.getMonth()).findFirst().get();
        WorkDay day = month.getWorkDays().stream().filter(f -> f.getActualDay().getYear() == task.getYear() && f.getActualDay().getMonthValue() == task.getMonth()).findFirst().get();
        Task selectedTask = day.getTasks().stream().filter(f -> f.getStartTime().toString().equals(task.getStartTime()) && f.getTaskId().equals(task.getTaskId())).findFirst().get();

        day.getTasks().remove(selectedTask);
        return selectedTask;
    }

    @PUT
    @Path("/deleteall")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkMonth> deleteAll() {
        timeLogger.getMonths().removeAll(getMonths());

        return timeLogger.getMonths();
    }
}
