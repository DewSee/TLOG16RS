package com.gytoth.tlog16rs.entities;

import com.gytoth.tlog16rs.core.FutureWorkDayException;
import com.gytoth.tlog16rs.core.NegativeMinutesOfWorkException;
import com.gytoth.tlog16rs.core.NotSeparatedTimeException;
import com.gytoth.tlog16rs.core.Util;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.Entity;
import static javax.persistence.FetchType.LAZY;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "work_day")
public class WorkDay {

    @Id
    @GeneratedValue
    int id;

    @OneToMany(cascade = ALL, fetch = LAZY)
    private List<Task> tasks = new ArrayList<>();

    long requiredMinPerDay;
    long extraMinPerDay;
    long sumPerDay;
    LocalDate actualDay;

    private static final long DEFAULT_MIN_PER_DAY = 450;
    private static final LocalDate DEFAULT_ACTUAL_DAY = LocalDate.now();

    public WorkDay(long requiredMinPerDay, int year, int month, int day) throws NegativeMinutesOfWorkException, FutureWorkDayException {
        this.requiredMinPerDay = requiredMinPerDay;
        this.actualDay = LocalDate.of(year, month, day);
        if (this.requiredMinPerDay < 0) {
            throw new NegativeMinutesOfWorkException("Amount of working time can't be negative!");
        }
        if (this.actualDay.isAfter(LocalDate.now())) {
            throw new FutureWorkDayException("Workday can't be created in the future");
        }
    }

    public WorkDay(long requiredMinPerDay) throws NegativeMinutesOfWorkException {
        this.requiredMinPerDay = requiredMinPerDay;
        this.actualDay = DEFAULT_ACTUAL_DAY;
        if (this.requiredMinPerDay < 0) {
            throw new NegativeMinutesOfWorkException("Amount of working time can't be negative!");
        }
    }

    public WorkDay(int year, int month, int day) throws FutureWorkDayException {
        this.requiredMinPerDay = DEFAULT_MIN_PER_DAY;
        this.actualDay = LocalDate.of(year, month, day);
        if (this.actualDay.isAfter(LocalDate.now())) {
            throw new FutureWorkDayException("Workday can't be created in the future");
        }
    }

    public WorkDay() {
        this.requiredMinPerDay = DEFAULT_MIN_PER_DAY;
        this.actualDay = DEFAULT_ACTUAL_DAY;
    }

    public long getSumPerDay() {
        return tasks.stream().mapToLong(o -> o.getMinPerTask()).sum();
    }

    public void setRequiredMinPerDay(long requiredMinPerDay) throws NegativeMinutesOfWorkException {
        this.requiredMinPerDay = requiredMinPerDay;
        if (requiredMinPerDay < 0) {
            throw new NegativeMinutesOfWorkException("Amount of working time can't be negative!");
        }
    }

    public void setActualDay(int year, int month, int day) throws FutureWorkDayException {
        this.actualDay = LocalDate.of(year, month, day);
        if (this.actualDay.isAfter(LocalDate.now())) {
            throw new FutureWorkDayException("Amount of working time can't be negative!");
        }
    }

    protected long getExtraMinPerDay() {
        return getSumPerDay() - requiredMinPerDay;
    }

    public void addTask(Task task) throws NotSeparatedTimeException {
        if (Util.isMultipleQuarterHour(task) && Util.isSeparatedTime(task, tasks)) {
            tasks.add(task);
        }
        if (!Util.isSeparatedTime(task, tasks)) {
            throw new NotSeparatedTimeException("Tasks' times are overlapping!");
        }
    }

    @Override
    public String toString() {
        return "requiredMinPerDay: " + requiredMinPerDay + ", actualDay: " + actualDay + ", sumPerDay: " + sumPerDay;
    }

}
