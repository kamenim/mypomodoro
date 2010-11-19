package org.mypomodoro.gui.todo;

import javax.swing.JLabel;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class PomodoroTest {
	private String beginingText;
	private Pomodoro pomodoro;
	private JLabel label;

	@Before
	public void setUpPomodoro() throws InterruptedException {
		beginingText = "bla";
		label = new JLabel(beginingText);
		pomodoro = new Pomodoro(label);
		pomodoro.start();
		Thread.sleep(1200);
	}

	@Test
	public void shouldUpdateTheLabelEverySecond() {
		assertThat(label.getText(), not(equalTo(beginingText)));
	}

	@Test
	public void shouldUpdateTheLabelWithSecondsFormat() throws Exception {
		assertEquals(label.getText(), "24 : 59");
		Thread.sleep(1000);
		assertEquals(label.getText(), "24 : 58");
	}

}
