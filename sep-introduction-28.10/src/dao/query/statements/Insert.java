package dao.query.statements;

import java.util.StringJoiner;

public class Insert {
    private final String tableName;

    private final StringJoiner values = new StringJoiner(",");

    private final StringJoiner columns = new StringJoiner(",");

    public Insert(String tableName) {
        this.tableName = tableName;
    }

    public Insert column(String column, String value) {
        values.add("'" + value + "'");
        columns.add(column);

        return this;
    }

    public Insert column(String column, int value) {
        return column(column, String.valueOf(value));
    }

    public String createStatement() {
        return "INSERT INTO " + tableName + " (" + columns.toString() +
        		") VALUES (" + values.toString() + ")";
    }
}
