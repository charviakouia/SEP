package dao.query.statements;


public class WhereExpression<T> {
    private final T parent;

    private final StringBuilder expression = new StringBuilder();

    WhereExpression(T parent, String column, String operation, String value) {
        this.parent = parent;
        addExpression(column, operation, value);
    }

    private void addExpression(String column, String operation,String value) {
        expression.append(column).append(" ").append(operation).append(" '").append(value).append("'");
    }

    public WhereExpression<T> or(String column, String operation, String value) {
        expression.append(" OR ");
        addExpression(column, operation, value);

        return this;
    }

    public WhereExpression<T> and(String column, String operation, String value) {
        expression.append(" AND ");
        addExpression(column, operation, value);
        return this;
    }

    public WhereExpression<T> and(String column, String operation, int value) {
        return and(column, operation, String.valueOf(value));
    }

    public T createWhere() {
        return parent;
    }

    @Override
    public String toString() {
        return " WHERE " + expression.toString();
    }
}
