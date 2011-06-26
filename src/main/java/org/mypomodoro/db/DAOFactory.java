package org.mypomodoro.db;

public class DAOFactory {

    private static final DAOFactory instance = new DAOFactory();

    public DAOFactory getInstance() {
        return instance;
    }
}
