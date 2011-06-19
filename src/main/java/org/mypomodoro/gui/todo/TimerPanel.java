package org.mypomodoro.gui.todo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mypomodoro.Main;

public class TimerPanel extends JPanel {
	private static final Dimension PREFERED_SIZE = new Dimension(250, 175);
	private final GridBagConstraints gbc = new GridBagConstraints();

	TimerPanel(Pomodoro pomodoro, JLabel pomodoroTimerLabel) {
		try {
			pomodoroTimerLabel.setFont(Font.createFont(Font.TRUETYPE_FONT,
					Main.class.getResourceAsStream("/TIMER.TTF")));
		} catch (FontFormatException e) {
			System.out.println("TrueType not supported " + e);
		} catch (IOException e) {
			System.out.println("TTF file not found " + e);
		}
		pomodoroTimerLabel.setForeground(Color.DARK_GRAY);
		setPreferredSize(PREFERED_SIZE);
		setLayout(new GridBagLayout());

		addPomodoroTimerLabel(pomodoroTimerLabel, gbc);
		addStartButton(pomodoro);
	}

	private void addStartButton(final Pomodoro pomodoro) {
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 0.165;
		gbc.anchor = GridBagConstraints.SOUTH;
		final JButton startButton = new JButton("Start");
        startButton.setBackground(Color.WHITE);
		startButton.setForeground(Color.BLACK);
		startButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
				if ("Start".equals(startButton.getText())) {
					pomodoro.start();
					startButton.setText("Stop");
				} else if (pomodoro.stopWithWarning()){
                    startButton.setText("Start");                    
				}
			}
		});
		startButton.setFont(startButton.getFont().deriveFont(20f));
		add(startButton, gbc);
	}

	private void addPomodoroTimerLabel(JLabel pomodoroTimerLabel,
			GridBagConstraints gbc) {
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = .3;
		gbc.anchor = GridBagConstraints.SOUTH;
		pomodoroTimerLabel.setFont(pomodoroTimerLabel.getFont().deriveFont(40f));
		add(pomodoroTimerLabel, gbc);
	}
}