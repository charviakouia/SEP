package dao.query.statements;


import java.util.StringJoiner;

public class Update {

    private final String tableName;

    private final StringJoiner stringJoiner = new StringJoiner(",");

    private WhereExpression<Update> whereExpression;

    public Update(String tableName) {
        this.tableName = tableName;
    }

    public Update column(String column, String value) {
        stringJoiner.add(column + "='" + value + "'");

        return this;
    }

    public WhereExpression<Update> where(String column, String operation, String value) {
        whereExpression = new WhereExpression<>(this, column, operation, value);
        return whereExpression;
    }

    public WhereExpression<Update> where(String column, String operation, int value) {
        return where(column, operation, String.valueOf(value));
    }

    public String createStatement() {
        return "UPDATE " + tableName + " SET " + stringJoiner.toString()
                + (whereExpression != null ? whereExpression.toString() : "");
    }
}
