/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gytoth.tlog16rs.core;

import com.gytoth.tlog16rs.entities.Task;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gyula
 */
public class UtilTest {

	public UtilTest() {
	}

	@Test
	public void testIsMultipleQuarterHour() {
	}

	@Test
	public void testIsWeekday() {
	}

	@Test
	public void testRoundToMultipleQuarterHour() {
		try {
			Task task = new Task("6172", "10:03", "10:38", "comment");

			assertEquals("10:00", task.getStartTime().toString());
			assertEquals("10:45", task.getEndTime().toString());

		} catch (NotExpectedTimeOrderException | InvalidTaskIdException | NoTaskIdException | EmptyTimeFieldException ex) {
			Logger.getLogger(UtilTest.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	@Test
	public void testIsSeparatedTime() {
	}

	@Test
	public void testSelectMonth() {
	}

	@Test
	public void testSelectDay() {
	}

	@Test
	public void testSelectTask() {
	}

}
