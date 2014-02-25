package org.mypomodoro.gui.manager;

import static com.sun.java.accessibility.util.SwingEventMonitor.addPopupMenuListener;
import javax.swing.JComboBox;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Combo box of types
 *
 */
public class IterationComboBox extends JComboBox {

    public IterationComboBox() {

        addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                //removeAllItems();
                for (int i = 0; i <= 101; i++) {
                    addItem(i); //starting at iteration 0 (not -1)
                }
//                //setSelectedItem(0);
                //setSelectedItem(selectedItem);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }

        });
    }
}
