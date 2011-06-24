package org.mypomodoro.gui.activities;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.Activity;

/**
 * Panel that displays information on the current Pomodoro
 * 
 */
public class DetailsPane extends JPanel implements ActivityInformation {
	private final JTextArea informationArea = new JTextArea();
	private final GridBagConstraints gbc = new GridBagConstraints();

	public DetailsPane(JTable table) {
		setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addInformationArea();
		addDeleteButton(table);
	}

    private void addDeleteButton(JTable table) {
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.1;
		gbc.fill = GridBagConstraints.NONE;
		add(new DeleteButton(table), gbc);
	}

	private void addInformationArea() {
		// add the information area
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridheight = GridBagConstraints.REMAINDER;		
		informationArea.setEditable(false);        
		add(new JScrollPane(informationArea), gbc);
	}

    @Override
	public void showInfo(Activity activity) {
        String pattern = "dd MMM yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String activityDate = format.format(activity.getDate());
		String text = "Date: ";
        if (activity.isUnplanned()) {
            text += "U [";
        }
		text += activityDate;
        if (activity.isUnplanned()) {
            text += "]";
        }
        text += "\nTitle: " + activity.getName()
				+ "\nEstimated Pomodoros: " + activity.getEstimatedPoms();
                if (activity.getOverestimatedPoms() > 0) {
                    text +=  " + " + activity.getOverestimatedPoms();
                }
		text += "\nType: " + activity.getType()
                + "\nAuthor: "	+ activity.getAuthor()
                + "\nPlace: " + activity.getPlace()
				+ "\nDescription: " + activity.getDescription();
		informationArea.setText(text);
	}

    @Override
	public void clearInfo() {
		informationArea.setText("");
	}
}