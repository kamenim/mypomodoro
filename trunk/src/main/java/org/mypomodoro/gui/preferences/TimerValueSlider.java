/* 
 * Copyright (C) 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.gui.preferences;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mypomodoro.Main;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.Labels;

public class TimerValueSlider extends JPanel {

    private final JSlider slider;
    private final JLabel label = new JLabel();
    private String text = "";

    public TimerValueSlider(final PreferencesPanel controlPanel, int min, int max,
            int val, String name, final int recommendedMin,
            final int recommendedMax, final int unit) {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        add(new FormLabel(name));
        c.gridx = 1;
        slider = new JSlider(min, max, val);
        setSliderColor(recommendedMin, recommendedMax);
        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int sliderValue = getSliderValue();
                setSliderColor(recommendedMin, recommendedMax);
                text = unit == 0 ? Labels.getString("PreferencesPanel.minutes")
                        : Labels.getString("PreferencesPanel.pomodoros");
                label.setText(" " + sliderValue + " " + text);
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        add(slider, c);
        c.gridx = 2;
        int sliderValue = slider.getValue();
        text = unit == 0 ? Labels.getString("PreferencesPanel.minutes")
                : Labels.getString("PreferencesPanel.pomodoros");
        label.setText(" " + sliderValue + " " + text);
        add(label, c);
    }

    public int getSliderValue() {
        return slider.getValue();
    }

    public void setSliderValue(int value) {
        slider.setValue(value);
    }

    public void setSliderColor(int recommendedMin, int recommendedMax) {
        if (getSliderValue() < recommendedMin
                || getSliderValue() > recommendedMax) {
            slider.setBackground(Color.orange);
        } else {
            slider.setBackground(Main.taskFinishedColor);
        }
    }

    public void changeSlider(int max) {
        slider.setMaximum(max);
        slider.repaint();
    }
}
