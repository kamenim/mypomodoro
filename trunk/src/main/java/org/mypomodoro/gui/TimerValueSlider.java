package org.mypomodoro.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TimerValueSlider extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSlider slider;
	private JLabel label = new JLabel();

	public TimerValueSlider(int min, int max, int val, String name) {
		super();
		setBackground(Color.white);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel(name));
		c.gridx = 1;

		slider = new JSlider(min, max, val);
		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				label.setText("" + slider.getValue() + " minutes");
			}
		});
		add(slider, c);
		c.gridx = 2;
		label.setText("" + slider.getValue() + " minutes");
		add(label, c);

	}

	public int getSliderValue() {
		return slider.getValue();
	}
}