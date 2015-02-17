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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.apache.commons.lang3.SystemUtils;
import org.mypomodoro.gui.ItemLocale;
import org.mypomodoro.gui.activities.AbstractComboBoxRenderer;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

/**
 * Preferences input form
 *
 */
public class PreferencesInputForm extends JPanel {

    private static final Dimension PANEL_DIMENSION = new Dimension(400, 200);
    protected final TimerValueSlider pomodoroSlider;
    protected final TimerValueSlider shortBreakSlider;
    protected final TimerValueSlider longBreakSlider;
    protected final TimerValueSlider maxNbPomPerActivitySlider;
    protected final TimerValueSlider maxNbPomPerDaySlider;
    protected final TimerValueSlider nbPomPerSetSlider;
    protected final JCheckBox tickingBox;
    protected final JCheckBox ringingBox;
    protected final JComboBox localesComboBox;
    protected final JCheckBox systemTrayBox;
    protected final JCheckBox systemTrayMessageBox;
    protected final JCheckBox alwaysOnTopBox;
    protected final JCheckBox recallBox;
    protected final JCheckBox agileModeBox;
    protected final JCheckBox pomodoroModeBox;
    protected final JCheckBox plainHoursBox;
    protected final JCheckBox effectiveHoursBox;
    protected final JComboBox themesComboBox;

    public PreferencesInputForm(final PreferencesPanel controlPanel) {
        TitledBorder titledborder = new TitledBorder(new EtchedBorder(), Labels.getString("PreferencesPanel.Preferences"));
        titledborder.setTitleFont(getFont().deriveFont(Font.BOLD));
        setBorder(titledborder);

        setMinimumSize(PANEL_DIMENSION);
        setPreferredSize(PANEL_DIMENSION);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.CENTER;
        int unitMinute = 0;
        int unitPomodoro = 1;
        pomodoroSlider = new TimerValueSlider(controlPanel, 10, 45,
                PreferencesPanel.preferences.getPomodoroLength(),
                Labels.getString("PreferencesPanel.Pomodoro Length") + ": ",
                25, 30, unitMinute);
        shortBreakSlider = new TimerValueSlider(controlPanel, 1, 15,
                PreferencesPanel.preferences.getShortBreakLength(),
                Labels.getString("PreferencesPanel.Short Break Length") + ": ",
                3, 5, unitMinute);
        longBreakSlider = new TimerValueSlider(controlPanel, 5, 45,
                PreferencesPanel.preferences.getLongBreakLength(),
                Labels.getString("PreferencesPanel.Long Break Length") + ": ",
                15, 30, unitMinute);
        final int maxNbPomPerActivity = 7;
        final int initMaxNbPomPerActivity = 5;
        final int maxNbPomPerActivityAgileMode = 24; // In the Agile world, a task may last up to 2 days (2 times the max nb of pom per day)
        final int initMaxNbPomPerActivityAgileMode = 20;
        final int maxNbPomPerDay = 12;
        final int initMaxNbPomPerDay = 10;
        maxNbPomPerActivitySlider = new TimerValueSlider(controlPanel, 1, maxNbPomPerActivityAgileMode,
                PreferencesPanel.preferences.getMaxNbPomPerActivity(),
                Labels.getString("PreferencesPanel.Max nb pom/activity") + ": ",
                1, initMaxNbPomPerActivityAgileMode, unitPomodoro);
        maxNbPomPerDaySlider = new TimerValueSlider(controlPanel, 1, maxNbPomPerDay,
                PreferencesPanel.preferences.getMaxNbPomPerDay(),
                Labels.getString("PreferencesPanel.Max nb pom/day") + ": ", 1,
                initMaxNbPomPerDay, unitPomodoro);
        nbPomPerSetSlider = new TimerValueSlider(controlPanel, 3, 5,
                PreferencesPanel.preferences.getNbPomPerSet(),
                Labels.getString("PreferencesPanel.Nb pom/set") + ": ", 4, 4,
                unitPomodoro);
        tickingBox = new JCheckBox(
                Labels.getString("PreferencesPanel.ticking"),
                PreferencesPanel.preferences.getTicking());
        ringingBox = new JCheckBox(
                Labels.getString("PreferencesPanel.ringing"),
                PreferencesPanel.preferences.getRinging());
        tickingBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        ringingBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        List<ItemLocale> locales = ItemLocale.getLocalesFromPropertiesTitlefiles();
        localesComboBox = new JComboBox(locales.toArray());
        for (int i = 0; i < locales.size(); i++) {
            if (locales.get(i).getLocale().equals(
                    PreferencesPanel.preferences.getLocale())) {
                localesComboBox.setSelectedIndex(i);
            }
        }
        // Setting the background color is required here for the Cross Platform Look And Feel (see Main)
        localesComboBox.setBackground(ColorUtil.WHITE);
        localesComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        systemTrayBox = new JCheckBox(
                Labels.getString("PreferencesPanel.System Tray"),
                PreferencesPanel.preferences.getSystemTray());
        systemTrayMessageBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Popup Message"),
                PreferencesPanel.preferences.getSystemTrayMessage());
        systemTrayBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                if (!systemTrayBox.isSelected()) {
                    systemTrayMessageBox.setSelected(false);
                }
            }
        });
        systemTrayMessageBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                if (systemTrayMessageBox.isSelected()) {
                    systemTrayBox.setSelected(true);
                }
            }
        });
        alwaysOnTopBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Always On Top"),
                PreferencesPanel.preferences.getAlwaysOnTop());
        alwaysOnTopBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        recallBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Recall"),
                PreferencesPanel.preferences.getRecall());
        recallBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        agileModeBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Agile.Agile Mode"),
                PreferencesPanel.preferences.getAgileMode());
        agileModeBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                // In the Agile world, a task may last up to 2 days (2 times the max nb of pom per day)
                maxNbPomPerActivitySlider.changeSlider(maxNbPomPerActivityAgileMode);
                maxNbPomPerActivitySlider.setSliderValue(maxNbPomPerDaySlider.getSliderValue() * 2);
                maxNbPomPerDaySlider.setVisible(false);
                agileModeBox.setSelected(true);
                pomodoroModeBox.setSelected(false);
            }
        });
        if (agileModeBox.isSelected()) {
            maxNbPomPerDaySlider.setVisible(false);
        }
        pomodoroModeBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Agile.Pomodoro Mode"),
                !PreferencesPanel.preferences.getAgileMode());
        pomodoroModeBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                maxNbPomPerActivitySlider.changeSlider(maxNbPomPerActivity);
                maxNbPomPerActivitySlider.setSliderValue(maxNbPomPerDaySlider.getSliderValue() < initMaxNbPomPerActivity ? maxNbPomPerDaySlider.getSliderValue() : initMaxNbPomPerActivity);
                maxNbPomPerDaySlider.setVisible(true);
                pomodoroModeBox.setSelected(true);
                agileModeBox.setSelected(false);
            }
        });
        plainHoursBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Plain hours"),
                PreferencesPanel.preferences.getPlainHours());
        plainHoursBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                plainHoursBox.setSelected(true);
                effectiveHoursBox.setSelected(false);

            }
        });
        effectiveHoursBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Effective hours"),
                !PreferencesPanel.preferences.getPlainHours());
        effectiveHoursBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                effectiveHoursBox.setSelected(true);
                plainHoursBox.setSelected(false);
            }
        });
        // Themes
        // http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
        // About custom laf: http://wiki.netbeans.org/FaqCustomLaf
        // To switch from one theme to another: SwingUtilities.updateComponentTreeUI(this); pack();
        // To load a theme: -Dswing.defaultlaf=net.sourceforge.napkinlaf.NapkinLookAndFeel
        // Lafs that work well with mAP
        // Napkin: UIManager.setLookAndFeel("net.sourceforge.napkinlaf.NapkinLookAndFeel"); (enable dependency in pom.xml)
        // (with Napkin, the play button does not work at all)
        // NimRod: UIManager.setLookAndFeel("com.nilo.plaf.nimrod.NimRODLookAndFeel"); (enable dependency in pom.xml)
        // Kunststoff: UIManager.setLookAndFeel(new KunststoffLookAndFeel()); (enable dependency in pom.xml)
        // (Kunststoff is an enhanced version of Metal but not quite as good)
        // JGoodies: UIManager.setLookAndFeel(new Plastic3DLookAndFeel()); (enable dependency in pom.xml)
        // TinyLaf: UIManager.setLookAndFeel("net.sf.tinylaf.TinyLookAndFeel"); (enable dependency in pom.xml)
        // Metal: UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); (same as cross platform)
        // GT: UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"); (same as System on Linux)        
        // Nimbus: UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); (if it exists in the jre)
        // InfoNode: UIManager.setLookAndFeel(new InfoNodeLookAndFeel());                 
        // Other laf that don't work too well with mAP
        // Substance: UIManager.setLookAndFeel(new SubstanceCremeLookAndFeel()); (enable dependency in pom.xml)
        // Motif: UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");                
        // Tonic: UIManager.setLookAndFeel("com.digitprop.tonic.TonicLookAndFeel"); (enable dependency in pom.xml)
        // WebLaf: UIManager.setLookAndFeel("com.alee.laf.WebLookAndFeel"); (enable dependency in pom.xml)
        /* Skin: (enable dependency in pom.xml)
         try {
         Skin theSkinToUse = SkinLookAndFeel.loadThemePack("themepack.zip");
         SkinLookAndFeel.setSkin(theSkinToUse);
         UIManager.setLookAndFeel(new SkinLookAndFeel());
         } catch (Exception ex) {
         }
         */
        ArrayList<String> themes = new ArrayList<String>();
        if (!UIManager.getSystemLookAndFeelClassName().equals(UIManager.getCrossPlatformLookAndFeelClassName())) {
            themes.add(UIManager.getSystemLookAndFeelClassName()); // Windows / GTK / Motif
        }
        themes.add(UIManager.getCrossPlatformLookAndFeelClassName()); // Metal
        /* Nimbus laf doesn't work well with mAP
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    themes.add(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Nimbus is not available
        }*/
        themes.add("com.nilo.plaf.nimrod.NimRODLookAndFeel"); // NimROD
        themes.add("com.jgoodies.looks.plastic.Plastic3DLookAndFeel"); // JGoodies
        //themes.add("com.jtattoo.plaf.smart.SmartLookAndFeel"); // JTatoo
        //themes.add("com.alee.laf.WebLookAndFeel"); // WebLAF
        // Quaqua
        // Due to copyright restrictions and technical constraints, Quaqua can be run on non-Mac OS X systems for development purposes only.
        //if (SystemUtils.IS_OS_MAC_OSX) {
        //themes.add("ch.randelshofer.quaqua.QuaquaLookAndFeel");
        //}
        themesComboBox = new JComboBox(themes.toArray());
        for (int i = 0; i < themes.size(); i++) {
            if (themes.get(i).equalsIgnoreCase(PreferencesPanel.preferences.getTheme())) {
                themesComboBox.setSelectedIndex(i);
            }
        }
        // Setting the background color is required here for the Cross Platform Look And Feel (see Main)
        themesComboBox.setBackground(ColorUtil.WHITE);
        themesComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                /* The following block has been commented because it doesn't work well, and doesn't work at all with Metal (cross platform) laf
                try {
                    // Show look and feel at run time
                    UIManager.setLookAndFeel((String)themesComboBox.getSelectedItem());
                    SwingUtilities.updateComponentTreeUI(Main.gui);
                    Main.gui.pack();*/
                    controlPanel.enableSaveButton();
                    controlPanel.clearValidation();
                /*} catch (ClassNotFoundException ignored) {
                } catch (InstantiationException ignored) {
                } catch (IllegalAccessException ignored) {
                } catch (UnsupportedLookAndFeelException ignored) {
                }*/
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        addAgileMode(gbc);
        gbc.gridy = 1;
        gbc.weighty = .5;
        gbc.fill = GridBagConstraints.BOTH;
        add(pomodoroSlider, gbc);
        gbc.gridy = 2;
        add(shortBreakSlider, gbc);
        gbc.gridy = 3;
        add(longBreakSlider, gbc);
        gbc.gridy = 4;
        add(maxNbPomPerActivitySlider, gbc);
        gbc.gridy = 5;
        add(maxNbPomPerDaySlider, gbc);
        gbc.gridy = 6;
        add(nbPomPerSetSlider, gbc);
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        addSounds(gbc);
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        addLocales(gbc);
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        if (SystemTray.isSupported()) {
            addSystemTray(gbc);
        }
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        addPlainHours(gbc);
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        addAlwaysOnTop(gbc);
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        addThemes(gbc);
    }

    private void addAgileMode(GridBagConstraints gbc) {
        JPanel agileMode = new JPanel();
        agileMode.setLayout(new GridBagLayout());
        GridBagConstraints gbcAgileMode = new GridBagConstraints();
        gbcAgileMode.fill = GridBagConstraints.HORIZONTAL;
        gbcAgileMode.anchor = GridBagConstraints.NORTH;
        gbcAgileMode.gridx = 0;
        gbcAgileMode.gridy = 0;
        agileMode.add(agileModeBox, gbcAgileMode);
        gbcAgileMode.gridx = 1;
        gbcAgileMode.gridy = 0;
        agileMode.add(pomodoroModeBox, gbcAgileMode);
        add(agileMode, gbc);
    }

    private void addSounds(GridBagConstraints gbc) {
        JPanel sounds = new JPanel();
        sounds.setLayout(new GridBagLayout());
        GridBagConstraints gbcSounds = new GridBagConstraints();
        gbcSounds.fill = GridBagConstraints.HORIZONTAL;
        gbcSounds.anchor = GridBagConstraints.NORTH;
        gbcSounds.gridx = 0;
        gbcSounds.gridy = 0;
        sounds.add(tickingBox, gbcSounds);
        gbcSounds.gridx = 1;
        gbcSounds.gridy = 0;
        sounds.add(ringingBox, gbcSounds);
        add(sounds, gbc);
    }

    private void addLocales(GridBagConstraints gbc) {
        JPanel locales = new JPanel();
        locales.setLayout(new GridBagLayout());
        GridBagConstraints gbcLocales = new GridBagConstraints();
        gbcLocales.fill = GridBagConstraints.HORIZONTAL;
        gbcLocales.anchor = GridBagConstraints.NORTH;
        gbcLocales.gridx = 0;
        gbcLocales.gridy = 0;
        localesComboBox.setRenderer(new AbstractComboBoxRenderer());
        locales.add(localesComboBox, gbcLocales);
        add(locales, gbc);
    }

    private void addSystemTray(GridBagConstraints gbc) {
        JPanel systemTray = new JPanel();
        systemTray.setLayout(new GridBagLayout());
        GridBagConstraints gbcSystemTray = new GridBagConstraints();
        gbcSystemTray.fill = GridBagConstraints.HORIZONTAL;
        gbcSystemTray.anchor = GridBagConstraints.NORTH;
        gbcSystemTray.gridx = 0;
        gbcSystemTray.gridy = 0;
        systemTray.add(systemTrayBox, gbcSystemTray);
        gbcSystemTray.gridx = 1;
        gbcSystemTray.gridy = 0;
        systemTray.add(systemTrayMessageBox, gbcSystemTray);
        add(systemTray, gbc);
    }

    private void addPlainHours(GridBagConstraints gbc) {
        JPanel plainHours = new JPanel();
        plainHours.setLayout(new GridBagLayout());
        GridBagConstraints gbcSystemTray = new GridBagConstraints();
        gbcSystemTray.fill = GridBagConstraints.HORIZONTAL;
        gbcSystemTray.anchor = GridBagConstraints.NORTH;
        gbcSystemTray.gridx = 0;
        gbcSystemTray.gridy = 0;
        plainHours.add(plainHoursBox, gbcSystemTray);
        gbcSystemTray.gridx = 1;
        gbcSystemTray.gridy = 0;
        plainHours.add(effectiveHoursBox, gbcSystemTray);
        add(plainHours, gbc);
    }

    private void addAlwaysOnTop(GridBagConstraints gbc) {
        JPanel alwaysOnTop = new JPanel();
        alwaysOnTop.setLayout(new GridBagLayout());
        GridBagConstraints gbcAlwaysOnTop = new GridBagConstraints();
        gbcAlwaysOnTop.fill = GridBagConstraints.HORIZONTAL;
        gbcAlwaysOnTop.anchor = GridBagConstraints.NORTH;
        gbcAlwaysOnTop.gridx = 0;
        gbcAlwaysOnTop.gridy = 0;
        alwaysOnTop.add(alwaysOnTopBox, gbcAlwaysOnTop);
        gbcAlwaysOnTop.gridx = 1;
        gbcAlwaysOnTop.gridy = 0;
        alwaysOnTop.add(recallBox, gbcAlwaysOnTop);
        add(alwaysOnTop, gbc);
    }

    private void addThemes(GridBagConstraints gbc) {
        JPanel themes = new JPanel();
        themes.setLayout(new GridBagLayout());
        GridBagConstraints gbcThemes = new GridBagConstraints();
        gbcThemes.fill = GridBagConstraints.HORIZONTAL;
        gbcThemes.anchor = GridBagConstraints.NORTH;
        gbcThemes.gridx = 0;
        gbcThemes.gridy = 0;
        themesComboBox.setRenderer(new ThemeComboBoxRenderer());
        themes.add(themesComboBox, gbcThemes);
        add(themes, gbc);
    }

    // Display the class name of the look and feel instead of the whole class path
    class ThemeComboBoxRenderer extends AbstractComboBoxRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String text = value.toString();
            setText(text.substring(text.lastIndexOf(".") + 1, text.length()));
            return this;
        }
    }
}
