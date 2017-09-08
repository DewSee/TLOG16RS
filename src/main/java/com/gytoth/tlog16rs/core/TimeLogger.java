package com.gytoth.tlog16rs.core;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class TimeLogger {

	private List<WorkMonth> months = new ArrayList<>();

	protected boolean isNewMonth(WorkMonth month) {
		return months.stream().noneMatch(o -> o.getDate().equals(month.getDate()));
	}

	public void addMonth(WorkMonth month) throws NotNewMonthException {
		if (isNewMonth(month)) {
			months.add(month);
		} else {
			throw new NotNewMonthException("This month is already added!");
		}
	}

}
