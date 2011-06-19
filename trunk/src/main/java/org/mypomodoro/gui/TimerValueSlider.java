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
	private JSlider slider;
	private JLabel label = new JLabel();

	public TimerValueSlider(final ControlPanel controlPanel, int min, int max, int val, String name, final int recommendedMin, final int recommendedMax, final String text) {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel(name));
		c.gridx = 1;

		slider = new JSlider(min, max, val);
        setSliderColor(recommendedMin, recommendedMax);
		slider.addChangeListener(new ChangeListener() {        
            @Override
			public void stateChanged(ChangeEvent e) {
                int sliderValue = getSliderValue();
                setSliderColor(recommendedMin, recommendedMax);
				label.setText(" " + sliderValue + " " + text + (sliderValue>1?"s":""));
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
			}
		});
		add(slider, c);
		c.gridx = 2;
        int sliderValue = slider.getValue();
		label.setText(" " + sliderValue + " " + text + (sliderValue>1?"s":""));
		add(label, c);
	}

	public int getSliderValue() {
		return slider.getValue();
	}

    public void setSliderValue(int value) {
		slider.setValue(value);
	}
    
    public void setSliderColor(int recommendedMin, int recommendedMax) {
        if (getSliderValue() < recommendedMin || getSliderValue() > recommendedMax) {
            slider.setBackground(Color.orange);
        } else {
            slider.setBackground(Color.green);
        }
    }
}