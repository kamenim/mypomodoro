package org.mypomodoro.gui.create.list;

import java.util.ArrayList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Combo box of types
 *
 * @author Phil Karoo
 */
public class TypeComboBox extends AbstractComboBox {

    public TypeComboBox() {

        addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                removeAllItems();
                for (String type : TypeList.getTypes()) {
                    addItem(type);
                }
                tooltipRenderer.setTooltips((ArrayList) TypeList.getTypes());
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