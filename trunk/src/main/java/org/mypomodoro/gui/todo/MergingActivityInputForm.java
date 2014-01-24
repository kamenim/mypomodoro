package org.mypomodoro.gui.todo;

import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.model.Activity;

public class MergingActivityInputForm extends ActivityInputForm {

    private static final long serialVersionUID = 20110814L;
    private final JTextArea toDosListTextArea = new JTextArea();

    public MergingActivityInputForm() {
        super(1);
        addSelectedToDos();
    }

    // ToDos to be merged
    private void addSelectedToDos() {
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.5;
        c.gridwidth = 2;
        toDosListTextArea.setFont(this.getFont());
        toDosListTextArea.setLineWrap(true);
        toDosListTextArea.setWrapStyleWord(true);
        toDosListTextArea.setEditable(false);
        // disable auto scrolling
        DefaultCaret caret = (DefaultCaret) toDosListTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        JScrollPane listTextArea = new JScrollPane(toDosListTextArea);
        listTextArea.setMinimumSize(TEXT_AREA_DIMENSION);
        listTextArea.setPreferredSize(TEXT_AREA_DIMENSION);
        listTextArea.setFont(getFont());
        add(listTextArea, c);
    }

    public void setToDoListTextArea(String value) {
        toDosListTextArea.setText(value);
    }

    public void displaySelectedToDos(List<Activity> selectedToDos) {
        toDosListTextArea.setText("");
        descriptionField.setText("");
        for (Activity selectedToDo : selectedToDos) {
            toDosListTextArea.append(selectedToDo.getName() + "\n");
            if (selectedToDo.getDescription() != null && selectedToDo.getDescription().length() > 0) {
                descriptionField.append(selectedToDo.getName() + ":\n" + selectedToDo.getDescription() + "\n\n");
            }
        }
    }
    
    public JTextArea getToDosListTextArea() {
        return toDosListTextArea;
    }
}
