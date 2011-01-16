package org.mypomodoro.gui.todo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import javax.swing.JLabel;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mypomodoro.gui.todo.Pomodoro.UpdateAction;

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
		assertEquals(label.getText(), "24:59");
		Thread.sleep(1000);
		assertEquals(label.getText(), "24:58");
	}

	@Ignore
	@Test
	public void shouldShowFullScreen() throws Exception {
		UpdateAction updateAction = pomodoro.new UpdateAction();
		updateAction.breakAction(10);
		Thread.sleep(2 * 1000);
	}

}
