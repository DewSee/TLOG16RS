package com.gytoth.tlog16rs.core;

import java.time.Duration;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Task {

	private String taskId;
	private LocalTime startTime;
	private LocalTime endTime;
	@Setter
	private String comment;

	/**
	 * startTime and endTime are made from an hour and minute integers.
	 *
	 * @throws NotExpectedTimeOrderException
	 * @throws InvalidTaskIdException
	 * @throws NoTaskIdException
	 * @throws EmptyTimeFieldException
	 */
	public Task(String taskid, int startHour, int startMin, int endHour, int endMin, String comment) throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException {
		this.taskId = taskid;
		this.startTime = LocalTime.of(startHour, startMin);
		this.endTime = LocalTime.of(endHour, endMin);
		this.comment = comment;
		Util.roundToMultipleQuarterHour(this);
		if (this.startTime == null && this.endTime == null) {
			throw new EmptyTimeFieldException("Time fields are empty!");
		}
		if (this.endTime.isBefore(this.startTime)) {
			throw new NotExpectedTimeOrderException("End Time must be after Start Time!");
		}
		if (this.taskId == null) {
			throw new NoTaskIdException("No Task Id!");
		}
		if (!this.isValidTaskId()) {
			throw new InvalidTaskIdException("Invalid Task ID!");
		}

	}

	public Task(String taskId) throws NoTaskIdException, InvalidTaskIdException, EmptyTimeFieldException {
		this.taskId = taskId;

		if (this.startTime == null && this.endTime == null) {
			throw new EmptyTimeFieldException("Time fields are empty!");
		}
		if (this.taskId == null) {
			throw new NoTaskIdException("No Task Id!");
		}
		if (!this.isValidTaskId()) {
			throw new InvalidTaskIdException("Invalid Task ID!");
		}
	}

	/**
	 * startTime and endTime are made from strings split at the colon (HH:MM)
	 *
	 * @throws NotExpectedTimeOrderException
	 * @throws InvalidTaskIdException
	 * @throws NoTaskIdException
	 * @throws EmptyTimeFieldException
	 */
	public Task(String taskId, String startTime, String endTime, String comment) throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException {
		String[] splitStartTime = startTime.split(":");
		String[] splitEndTime = endTime.split(":");
		this.taskId = taskId;
		this.startTime = LocalTime.of(Integer.parseInt(splitStartTime[0]), Integer.parseInt(splitStartTime[1]));
		this.endTime = LocalTime.of(Integer.parseInt(splitEndTime[0]), Integer.parseInt(splitEndTime[1]));
		this.comment = comment;
		Util.roundToMultipleQuarterHour(this);

		if (this.startTime == null && this.endTime == null) {
			throw new EmptyTimeFieldException("Time fields are empty!");
		}
		if (this.endTime.isBefore(this.startTime)) {
			throw new NotExpectedTimeOrderException("End Time must be after Start Time!");
		}
		if (this.taskId == null) {
			throw new NoTaskIdException("No Task Id!");

		}
		if (!this.isValidTaskId()) {
			throw new InvalidTaskIdException("Invalid Task ID!");
		}
	}

	public void setTaskId(String taskId) throws InvalidTaskIdException, NoTaskIdException {
		this.taskId = taskId;

		if (this.taskId == null) {
			throw new NoTaskIdException("No Task Id!");

		}
		if (!this.isValidTaskId()) {
			throw new InvalidTaskIdException("Invalid Task ID!");
		}

	}

	public void setStartTime(int startHour, int startMin) throws NotExpectedTimeOrderException, EmptyTimeFieldException {
		this.startTime = LocalTime.of(startHour, startMin);
		if (this.startTime == null && this.endTime == null) {
			throw new EmptyTimeFieldException("Time fields are empty!");
		}
		if (this.endTime.isBefore(this.startTime)) {
			throw new NotExpectedTimeOrderException("End Time must be after Start Time!");
		}
	}

	public void setStartTime(LocalTime startTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException {
		this.startTime = startTime;
		if (this.startTime == null && this.endTime == null) {
			throw new EmptyTimeFieldException("Time fields are empty!");
		}
		if (this.endTime.isBefore(this.startTime)) {
			throw new NotExpectedTimeOrderException("End Time must be after Start Time!");
		}
	}

	public void setEndTime(int endHour, int endMin) throws NotExpectedTimeOrderException, EmptyTimeFieldException {
		this.endTime = LocalTime.of(endHour, endMin);
		if (this.startTime == null && this.endTime == null) {
			throw new EmptyTimeFieldException("Time fields are empty!");
		}
		if (this.endTime.isBefore(this.startTime)) {
			throw new NotExpectedTimeOrderException("End Time must be after Start Time!");
		}

	}

	public void setEndTime(LocalTime endTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException {
		this.endTime = endTime;
		if (this.startTime == null && this.endTime == null) {
			throw new EmptyTimeFieldException("Time fields are empty!");
		}
		if (this.endTime.isBefore(this.startTime)) {
			throw new NotExpectedTimeOrderException("End Time must be after Start Time!");
		}
	}

	public void setStartTime(String startTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException {
		String[] splitStartTime = startTime.split(":");
		this.startTime = LocalTime.of(Integer.parseInt(splitStartTime[0]), Integer.parseInt(splitStartTime[1]));
		if (this.startTime == null && this.endTime == null) {
			throw new EmptyTimeFieldException("Time fields are empty!");
		}
		if (this.endTime.isBefore(this.startTime)) {
			throw new NotExpectedTimeOrderException("End Time must be after Start Time!");
		}
	}

	public void setEndTime(String endTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException {
		String[] splitEndTime = endTime.split(":");
		this.endTime = LocalTime.of(Integer.parseInt(splitEndTime[0]), Integer.parseInt(splitEndTime[1]));
		if (this.startTime == null && this.endTime == null) {
			throw new EmptyTimeFieldException("Time fields are empty!");
		}
		if (this.endTime.isBefore(this.startTime)) {
			throw new NotExpectedTimeOrderException("End Time must be after Start Time!");
		}
	}

	protected long getMinPerTask() {
		return (Duration.between(startTime, endTime).getSeconds()) / 60;
	}

	protected boolean isValidTaskId() {
		return (isValidLtId() || isValidRedmineId());
	}

	private boolean isValidRedmineId() {
		return ((taskId.matches("[0-9]+") && taskId.length() == 4));
	}

	private boolean isValidLtId() {
		return ((taskId.startsWith("LT-") && taskId.substring(3).matches("[0-9]+")));
	}

	@Override
	public String toString() {
		return "taskId=" + taskId + ", startTime=" + startTime + ", endTime=" + endTime + ", comment=" + comment;
	}

}
