package com.gytoth.tlog16rs.entities;

import com.gytoth.tlog16rs.core.NotNewDateException;
import com.gytoth.tlog16rs.core.NotSameMonthException;
import com.gytoth.tlog16rs.core.Util;
import com.gytoth.tlog16rs.core.WeekendNotEnabledException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.Entity;
import static javax.persistence.FetchType.LAZY;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "work_month")
@NoArgsConstructor
public class WorkMonth {

    @Id
    @GeneratedValue
    int id;

    @OneToMany(cascade = ALL, fetch = LAZY)
    private List<WorkDay> workDays = new ArrayList<>();

    long sumPerMonth;

    long requiredMinPerMonth;

    long extraMinPerMonth;

    YearMonth date;

    public WorkMonth(int year, int month) {
        this.date = YearMonth.of(year, month);
    }

    public long getSumPerMonth() {
        return workDays.stream().mapToLong(o -> o.getSumPerDay()).sum();
    }

    public long getRequiredMinPerMonth() {
        return workDays.stream().mapToLong(o -> o.getRequiredMinPerDay()).sum();
    }

    protected boolean isNewDate(WorkDay workDay) {
        return workDays.stream().noneMatch(o -> o.getActualDay().equals(workDay.getActualDay()));
    }

    protected long getExtraMintPerMonth() {
        return getSumPerMonth() - getRequiredMinPerMonth();
    }

    private boolean isSameMonth(WorkDay workDay) {
        return this.date.getMonthValue() == workDay.getActualDay().getMonthValue();
    }

    protected void addWorkDay(WorkDay workDay, boolean isWeekendEnabled) throws WeekendNotEnabledException, NotNewDateException, NotSameMonthException {
        if ((isWeekendEnabled || Util.isWeekday(workDay)) && isNewDate(workDay) && isSameMonth(workDay)) {
            workDays.add(workDay);
        } else if (!isSameMonth(workDay)) {
            throw new NotSameMonthException("The given day's month does not match!");
        } else if (!isNewDate(workDay)) {
            throw new NotNewDateException("Date already exists!");
        } else if (!isWeekendEnabled && !Util.isWeekday(workDay)) {
            throw new WeekendNotEnabledException("Can't add a weekend!");
        }
    }

    public void addWorkDay(WorkDay workDay) throws WeekendNotEnabledException, NotNewDateException, NotSameMonthException {
        if (Util.isWeekday(workDay) && isNewDate(workDay) && isSameMonth(workDay)) {
            workDays.add(workDay);
        } else if (!isSameMonth(workDay)) {
            throw new NotSameMonthException("The given day's month does not match! (" + workDay.getActualDay().getMonth() + "/" + this.date.getMonth() + ")");
        } else if (!isNewDate(workDay)) {
            throw new NotNewDateException("Date already exists!");
        } else if (!Util.isWeekday(workDay)) {
            throw new WeekendNotEnabledException("Can't add a weekend!");
        }
    }

}
