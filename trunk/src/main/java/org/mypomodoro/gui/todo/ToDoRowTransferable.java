package org.mypomodoro.gui.todo;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Row Transferable
 *
 */
public class ToDoRowTransferable implements Transferable {

    public static final DataFlavor DATA_ROW = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Object.class.getName(), null);
    private final ArrayList<Object> value;
    private final int row;

    public ToDoRowTransferable(ArrayList<Object> a, int rowId) {
        this.value = a;
        this.row = rowId;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor == null) {
            throw new IOException();
        } else if (flavor.equals(DATA_ROW)) {
            Object[] o = {this.row, this.value};
            return o;
        } else if (flavor.equals(DataFlavor.stringFlavor)) {
            return this.value.toString();
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DATA_ROW, DataFlavor.stringFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.equals(DATA_ROW) || flavor.equals(DataFlavor.stringFlavor));
    }
}
