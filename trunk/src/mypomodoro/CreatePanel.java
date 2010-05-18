package mypomodoro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
 * GUI for creating a new Activity and store to data layer.
 *
 * @author Brian Wetzel
 */
public class CreatePanel extends JPanel
{
    protected JTextField placeTF = new JTextField();
    protected JTextField authorTF = new JTextField();
    protected JTextField nameTF = new JTextField();
    protected JTextArea descriptionTA = new JTextArea();
    protected JTextField typeTF = new JTextField();
    protected JTextField estimatedPomsTF = new JTextField();
    protected JPanel inputFormPanel = new InputFormPanel();
    protected JLabel validation = new JLabel("");
    protected SaveButton sbutton = new SaveButton();
    protected GridBagConstraints c = new GridBagConstraints();

    public CreatePanel()
    {
        //setMinimumSize(new Dimension(MyPomodoroView.FRAME_WIDTH,
                             // MyPomodoroView.FRAME_WIDTH));
        setLayout(new GridBagLayout());
        
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.80;
        c.fill = GridBagConstraints.BOTH;
        add(inputFormPanel, c);
        c.gridx =0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTH;
        c.weighty = 0.2;
        add(sbutton, c);
        validation.setForeground(Color.red);
        c.gridy = 2;
        add(validation, c);
    }

    public void clearForm()
    {
        placeTF.setText("");
        authorTF.setText("");
        nameTF.setText("");
        descriptionTA.setText("");
        typeTF.setText("");
        estimatedPomsTF.setText("");
    }

    class InputFormPanel extends JPanel
    {
        public InputFormPanel()
        {
            setBorder(new EtchedBorder());
            Dimension textFieldDimension = new Dimension(300, 25);
            Dimension textAreaDimension = new Dimension(300, 50);
            Dimension panelDimension = new Dimension(400,200);
            setMinimumSize(panelDimension);
            setPreferredSize(panelDimension);
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            
            
            //Place label and TextField
            c.gridx = 0;
            c.gridy = 0;
            c.weighty = 0.5;
            c.weightx = 0.0;
            c.anchor = GridBagConstraints.NORTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(new FormLabel("Place: "), c);
            placeTF.setMinimumSize(textFieldDimension);
            placeTF.setPreferredSize(textFieldDimension);
            c.gridx = 1;
            c.gridy =0;
            c.weighty = 0.5;
            add(placeTF, c);

            //Author Label and TextField
            c.gridx = 0;
            c.gridy = 1;
            c.weighty = 0.5;
            c.anchor = GridBagConstraints.NORTH;
            add(new FormLabel("Author"), c);
            c.gridx = 1;
            c.gridy = 1;
            c.weighty = 0.5;
            authorTF.setMinimumSize(textFieldDimension);
            authorTF.setPreferredSize(textFieldDimension);
            add(authorTF, c);

            //Name Label and Text Field
            c.gridx = 0;
            c.gridy = 2;
            c.weighty = 0.5;
            c.anchor = GridBagConstraints.NORTH;
            add(new FormLabel("Name:"), c);
            c.gridx = 1;
            c.gridy = 2;
            c.weighty = 0.5;
            nameTF.setMinimumSize(textFieldDimension);
            nameTF.setPreferredSize(textFieldDimension);
            add(nameTF, c);

            //Description Label and TextArea
            c.gridx = 0;
            c.gridy = 3;
            c.weighty = 0.5;
            c.anchor = GridBagConstraints.NORTH;
            add(new FormLabel("Description:"), c);
            c.gridx = 1;
            c.gridy = 3;
            c.weighty = 0.5;
            JScrollPane description = new JScrollPane(descriptionTA);
            description.setMinimumSize(textAreaDimension);
            description.setPreferredSize(textAreaDimension);
            add(description, c);

            //Type Label and TextField
            c.gridx = 0;
            c.gridy = 4;
            c.weighty = 0.5;
            c.anchor = GridBagConstraints.NORTH;
            add(new FormLabel("Type:"), c);
            c.gridx = 1;
            c.gridy = 4;
            c.weighty = 0.5;
            typeTF.setMinimumSize(textFieldDimension);
            typeTF.setPreferredSize(textFieldDimension);
            add(typeTF, c);

            //Estimated Poms Description and TextField
            c.gridx = 0;
            c.gridy = 5;
            c.weighty = 0.5;
            c.anchor = GridBagConstraints.NORTH;
            add(new FormLabel("Estimated Pomodoros:"), c);
            c.gridx = 1;
            c.gridy = 5;
            c.weighty = 0.5;
            estimatedPomsTF.setMinimumSize(textFieldDimension);
            estimatedPomsTF.setPreferredSize(textFieldDimension);
            add(estimatedPomsTF, c);
        }

        class FormLabel extends JLabel
        {
            public FormLabel(String str)
            {
                super(str);

                Dimension labelDimension = new Dimension (150, 25);
                setPreferredSize(labelDimension);
                setMinimumSize(labelDimension);
                setMaximumSize(labelDimension);
                setAlignmentX(LEFT_ALIGNMENT);
            }
        }
    }

    class SaveButton extends JButton
    {
        public SaveButton()
        {
            super("Save");
            addActionListener(new ButtonListener());
        }

        class ButtonListener implements ActionListener
        {

            public void actionPerformed(ActionEvent event)
            {
                saveData();
                clearForm();
            }
        }
    }

    public void saveData()
    {
        String place = placeTF.getText();
        String author = authorTF.getText();
        String name = nameTF.getText();
        String description = descriptionTA.getText();
        String type = typeTF.getText();
        int estimatedPoms = 0;
        try
        {
            estimatedPoms =(!estimatedPomsTF.getText().equals("")) ?
                Integer.parseInt(estimatedPomsTF.getText()) : 0;
        } catch (NumberFormatException e)
        {
            
        }

        Activity newActivity =
                new Activity(place, author, name, description, type, estimatedPoms);
        System.out.println(newActivity.toString());
        System.out.println(newActivity.isValid());
        if(newActivity.isValid())
        {
            ActivityList.getList().add(newActivity);
            validation.setForeground(Color.green);
            newActivity.databaseInsert();
            validation.setText("Activity Added.");
        }
        else
        {
            validation.setForeground(Color.red);
            validation.setText("Invalid Input.");
        }
        System.out.println(ActivityList.getList().toString());
    }
}