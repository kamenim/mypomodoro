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
package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import static java.lang.Thread.sleep;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.mypomodoro.Main;
import org.mypomodoro.gui.ImageIcons;
import org.mypomodoro.gui.MainPanel;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * Panel that allows the merging of ToDos
 *
 */
public class MergingPanel extends CreatePanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private ActivityInputForm mergingInputFormPanel;
    private final ToDoPanel panel;

    public MergingPanel(ToDoPanel todoPanel) {
        this.panel = todoPanel;
        mergingInputFormPanel.setEstimatedPomodoro(1);
        setBorder(null); // remove create panel border
    }

    @Override
    protected void addInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        mergingInputFormPanel = new ActivityInputForm();
        mergingInputFormPanel.getNameField().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableSaveButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (mergingInputFormPanel.getNameField().getText().length() == 0) {
                    disableSaveButton();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                enableSaveButton();
            }
        });
        add(new JScrollPane(mergingInputFormPanel), gbc);
    }

    @Override
    protected void addSaveButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        disableSaveButton();
        add(saveButton, gbc);
    }

    @Override
    protected void addClearButton() {
    }

    @Override
    protected void addValidation() {
    }

    @Override
    protected void validActivityAction(final Activity newActivity) {
        StringBuilder comments = new StringBuilder();
        int actualPoms = 0;
        final int selectedRowCount = panel.getTable().getSelectedRowCount();
        if (selectedRowCount > 0) {
            int[] rows = panel.getTable().getSelectedRows();
            comments.append("<html><head></head><body>");
            for (int row : rows) {
                Activity selectedToDo = panel.getTable().getActivityFromRowIndex(row);
                if (panel.getPomodoro().inPomodoro() && selectedToDo.getId() == panel.getPomodoro().getCurrentToDo().getId()) {
                    continue;
                }
                // aggregate comments
                if (selectedToDo.getNotes().length() > 0) {
                    comments.append("<p style=\"margin-top: 0\">");
                    comments.append("<b>");
                    comments.append(selectedToDo.getName());
                    comments.append(" :");
                    comments.append("</b>");
                    comments.append("</p>");
                    // Parsing HTML
                    // Jsoup: parsing the html content without reformating (because JSoup is HTML 5 compliant only - not 3.2)
                    Document doc = Jsoup.parse(selectedToDo.getNotes(), "UTF-8", Parser.xmlParser());
                    //Document doc = Jsoup.parse(selectedToDo.getNotes());
                    Elements elements = doc.getElementsByTag(HTML.Tag.BODY.toString());
                    if (!elements.isEmpty()) {
                        comments.append(elements.html());
                    } else { // Backward compatility 3.0.X and imported data
                        comments.append(selectedToDo.getNotes());
                    }
                    comments.append("<p style=\"margin-top: 0\">");
                    comments.append("</p>");
                }
                actualPoms += selectedToDo.getActualPoms();
            }
            comments.append("</body>");
            // set comment
            newActivity.setNotes(comments.toString());
            // set estimate
            // make sure the estimate of the new activity is at least equals to the sum of pomodoros already done
            if (actualPoms > 0) {
                if (newActivity.getEstimatedPoms() < actualPoms) {
                    newActivity.setOverestimatedPoms(actualPoms - newActivity.getEstimatedPoms());
                }
                newActivity.setActualPoms(actualPoms);
            }
            final String title = Labels.getString("ToDoListPanel.Merge ToDos");
            new Thread() { // This new thread is necessary for updating the progress bar
                @Override
                public void run() {
                    if (!WaitCursor.isStarted()) {
                        // Start wait cursor
                        WaitCursor.startWaitCursor();
                        // Set progress bar
                        MainPanel.progressBar.setVisible(true);
                        MainPanel.progressBar.getBar().setValue(0);
                        MainPanel.progressBar.getBar().setMaximum(panel.getPomodoro().inPomodoro() ? selectedRowCount - 1 : selectedRowCount);
                        // only now we can remove the merged tasks
                        int[] rows = panel.getTable().getSelectedRows();
                        int increment = 0;
                        for (int row : rows) {
                            if (!MainPanel.progressBar.isStopped()) {
                                // removing a row requires decreasing the row index number
                                row = row - increment;                                
                                Activity selectedToDo = panel.getTable().getActivityFromRowIndex(row);
                                if (panel.getPomodoro().inPomodoro() && selectedToDo.getId() == panel.getPomodoro().getCurrentToDo().getId()) {
                                    continue;
                                }
                                panel.delete(selectedToDo);
                                panel.removeRow(row);
                                increment++;
                                final int progressValue = increment;
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainPanel.progressBar.getBar().setValue(progressValue); // % - required to see the progress                                    
                                        MainPanel.progressBar.getBar().setString(Integer.toString(progressValue) + " / " + Integer.toString(selectedRowCount)); // task
                                    }
                                });
                            }
                        }
                        // Indicate reordoring by priority in progress bar
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                MainPanel.progressBar.getBar().setValue(MainPanel.progressBar.getBar().getMaximum());
                                MainPanel.progressBar.getBar().setString(Labels.getString("ProgressBar.Updating priorities"));
                            }
                        });
                        // Reorder the priorities BEFORE adding the task to the ToDo list otherwise its priority will be wrong due to previous deletion of tasks
                        // When the list has a lot of tasks, the reorderByPriority method is very slow (probably) because there are now gaps in the index of the ToDo list due to previous deletion of tasks
                        panel.getTable().reorderByPriority();
                        if (mergingInputFormPanel.isDateToday() || Main.preferences.getAgileMode()) { // add new activity to ToDo list                                
                            panel.addActivity(newActivity);
                            panel.insertRow(newActivity);
                        } else { // add merged activity to activities list
                            ActivityList.getList().add(newActivity);
                            Main.gui.getActivityListPanel().insertRow(newActivity);
                            String message = Labels.getString("ToDoListPanel.Task added to Activity List");
                            JOptionPane.showConfirmDialog(Main.gui, message, title,
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageIcons.DIALOG_ICON);
                        }
                        // Close progress bar
                        final int progressCount = increment;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                MainPanel.progressBar.getBar().setString(Labels.getString("ProgressBar.Done") + " (" + progressCount + ")");
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(1000); // wait one second before hiding the progress bar
                                        } catch (InterruptedException ex) {
                                            logger.error("", ex);
                                        }
                                        // hide progress bar
                                        MainPanel.progressBar.getBar().setString(null);
                                        MainPanel.progressBar.setVisible(false);
                                        MainPanel.progressBar.setStopped(false);
                                    }
                                }.start();
                            }
                        });
                        // Stop wait cursor
                        WaitCursor.stopWaitCursor();
                        clearForm();
                    }
                }
            }.start();
        }
    }

    @Override
    protected void invalidActivityAction() {
        String title = Labels.getString("Common.Error");
        String message = Labels.getString("Common.Title is mandatory");
        JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, ImageIcons.DIALOG_ICON);
    }

    @Override
    public ActivityInputForm getFormPanel() {
        return mergingInputFormPanel;
    }

    @Override
    public void clearForm() {
        mergingInputFormPanel.setNameField("");
        mergingInputFormPanel.setEstimatedPomodoro(1);
        if (Main.preferences.getAgileMode()) {
            mergingInputFormPanel.setStoryPoints(0);
            mergingInputFormPanel.setIterations(0);
        }
        mergingInputFormPanel.setDescriptionField("");
        mergingInputFormPanel.setTypeField("");
        mergingInputFormPanel.setAuthorField("");
        mergingInputFormPanel.setPlaceField("");
        mergingInputFormPanel.setDate(new Date());
    }
}
