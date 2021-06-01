package dao.query.statements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreateTable {

    private final String tableName;

    private final List<CreateColumn> columns = new ArrayList<>();

    public CreateTable(String tableName) {
        this.tableName = tableName;
    }

    public CreateColumn integerCol(String column) {
        return new CreateColumn(this, column, "INTEGER");
    }

    public CreateColumn varcharCol(String column, int size) {
        return new CreateColumn(this, column, "VARCHAR(" + size + ")");
    }

    public CreateColumn dateCol(String column) {
        return new CreateColumn(this, column, "DATE");
    }

    CreateTable addColumn(CreateColumn column) {
        columns.add(column);

        return this;
    }

    public String createStatement() {
        Optional<String> columnsReduced = columns.stream()
        		.map(CreateColumn::toString).reduce((c1, c2) -> c1 + ", " + c2);

        return "CREATE TABLE IF NOT EXISTS " + 
        tableName + "(" + columnsReduced.orElse("") + ")";
    }
}
