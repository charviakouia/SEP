package dao.query.statements;

import dao.query.statements.CreateColumn;


public class CreateColumn {
    private final CreateTable parent;

    private final String nameAndType;

    private boolean primaryKey;

    private boolean notNull;

    CreateColumn(CreateTable parent, String name, String type) {
        this.nameAndType = name + " " + type;
        this.parent = parent;
    }

    public CreateColumn primaryKey() {
        primaryKey = true;

        return this;
    }

    public CreateColumn notNull() {
        notNull = true;

        return this;
    }

    public CreateTable createColumn() {
        return parent.addColumn(this);
    }

    @Override
    public String toString() {
        String statement = nameAndType;

        if (primaryKey) {
            statement = statement + " primary key";
        }

        if (notNull) {
            statement = statement + " not null";
        }

        return statement;
    }
}
