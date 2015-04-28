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
package org.mypomodoro.gui;

import java.util.Date;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.jdesktop.swingx.JXTable;
import org.mypomodoro.gui.todo.ToDoTable;
import org.mypomodoro.model.Activity;

public interface IListPanel {

    void refresh();

    void refresh(boolean fromDatabase);

    void move(Activity activity);

    //void moveAll();
    void removeRow(int row);

    void insertRow(Activity activity);

    AbstractTable getTable();

    int getIdKey();

    Activity getActivityById(int id);

    void delete(Activity activity);

    void deleteAll();

    void complete(Activity activity);

    //void completeAll();
    void addActivity(Activity activity);

    void addActivity(Activity activity, Date date, Date dateCompleted);

    void saveComment(String comment);

    JSplitPane getSplitPane();

    JPanel getListPane();

    JScrollPane getSubTableScrollPane();

    void addTableTitlePanel();

    void addTable();

    void addSubTableTitlePanel();

    JScrollPane getTableScrollPane();

    TableTitlePanel getTableTitlePanel();
    
    void setCurrentTable(AbstractTable table);
    
    AbstractTable getCurrentTable(); // TODO to be removed or remove getTable()
}
