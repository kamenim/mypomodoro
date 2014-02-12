package org.mypomodoro.gui.create.list;

import java.util.ArrayList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Combo box of authors
 *
 */
public class AuthorComboBox extends AbstractComboBox {

    public AuthorComboBox() {

        addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                removeAllItems();
                for (String author : AuthorList.getAuthors()) {
                    addItem(author);
                }
                tooltipRenderer.setTooltips((ArrayList) AuthorList.getAuthors());
                setSelectedItem(selectedItem);
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
