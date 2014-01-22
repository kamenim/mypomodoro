package org.mypomodoro.gui.manager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.mypomodoro.buttons.MoveButton;

public class ListMoverMouseListener extends MouseAdapter {

    private final ListPane from;
    private final ListPane to;

    public ListMoverMouseListener(ListPane from, ListPane to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2) {
            MoveButton.move(from, to);
        }
    }
}
