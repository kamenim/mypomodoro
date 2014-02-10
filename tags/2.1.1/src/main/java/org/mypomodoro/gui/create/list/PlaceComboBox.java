package org.mypomodoro.gui.create.list;

import java.util.ArrayList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Combo box of places
 *
 * @author Phil Karoo
 */
public class PlaceComboBox extends AbstractComboBox {

    public PlaceComboBox() {

        addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                removeAllItems();
                for (String place : PlaceList.getPlaces()) {
                    addItem(place);
                }
                tooltipRenderer.setTooltips((ArrayList) PlaceList.getPlaces());
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
