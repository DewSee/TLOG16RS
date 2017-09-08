package com.gytoth.tlog16rs.core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;

public class WorkDay {

	@Getter
	private List<Task> tasks = new ArrayList<>();
	@Getter
	private long requiredMinPerDay;
	@Getter
	private LocalDate actualDay;

	private long sumPerDay;

	private long DEFAULT_MIN_PER_DAY = 450;
	private LocalDate DEFAULT_ACTUAL_DAY = LocalDate.now();

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

	protected LocalTime getLastEndTime() {
		Task latest = tasks.stream().max(Comparator.comparing(Task::getEndTime)).get();

		return latest.getEndTime();
	}

	@Override
	public String toString() {
		return "requiredMinPerDay: " + requiredMinPerDay + ", actualDay: " + actualDay + ", sumPerDay: " + sumPerDay;
	}

}
