package de.dedede.model.logic.util;

import de.dedede.model.persistence.util.ConnectionPool;

import java.util.ArrayList;

public class CSSReader {

    private static volatile CSSReader INSTANCE = null;

    private ArrayList<String> styleNames;


    private CSSReader() {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
    }


    public static CSSReader getInstance() {
        if (INSTANCE == null) {
            synchronized (CSSReader.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CSSReader();
                }
            }
        }

        return INSTANCE;
    }


    public ArrayList<String> getStyleNames() {
        return styleNames;
    }

    public void setStyle(){

    }

}
