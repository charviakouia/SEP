package de.dedede.model.data.dtos;

/**
 * Enum class representing the columns in the {@link de.dedede.model.logic.managed_beans.LendingPeriodViolation}'s
 * main table. Is used to implement the functionality of sorting by column.
 *
 * @author Ivan Charviakou
 */
public enum LendingPeriodViolationColumns {

    MEDIUM("lendingPeriodViolations.mediumTitle"),
    USER("lendingPeriodViolations.userTitle"),
    COPY("lendingPeriodViolations.copyTitle"),
    OVERDRAFT("lendingPeriodViolations.overdraftTitle");

    private final String strRep;

    LendingPeriodViolationColumns(String strRep){
        this.strRep = strRep;
    }

    @Override
    public String toString() {
        return strRep;
    }
}
