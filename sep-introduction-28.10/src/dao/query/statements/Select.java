package dao.query.statements;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class Select {

    private List<String> selectedColumns = new ArrayList<>();

    private final List<String> fromColumns = new ArrayList<>();

    private WhereExpression<Select> whereExpression;

    public Select select(String column) {
        selectedColumns.add(column);
        return this;
    }

    public Select selectAll() {
        selectedColumns.add("*");
        selectedColumns = Collections.unmodifiableList(selectedColumns);
        return this;
    }

    public Select from(String column) {
        fromColumns.add(column);
        return this;
    }

    public WhereExpression<Select> where(String column, String operation, String value) {
        whereExpression = new WhereExpression<>(this, column, operation, value);
        return whereExpression;
    }

    public String createStatement() {
        return "SELECT " + arrayToString(selectedColumns)
                + " FROM " + arrayToString(fromColumns)
                + (whereExpression != null ? whereExpression.toString() : "");
    }

    private String arrayToString(List<String> list) {
        StringJoiner stringJoiner = new StringJoiner(",");

        for (String string : list) {
            stringJoiner.add(string);
        }

        return stringJoiner.toString();
    }
}
