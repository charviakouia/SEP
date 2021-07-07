package de.dedede.model.data.dtos;

public enum LendingPeriodViolationColumns {
    MEDIUM("lendingPeriodViolations.mediumTitle"),
    USER("lendingPeriodViolations.userTitle"),
    COPY("lendingPeriodViolations.copyTitle"),
    OVERDRAFT("lendingPeriodViolations.overdraftTitle");

    private String strRep;

    LendingPeriodViolationColumns(String strRep){
        this.strRep = strRep;
    }

    @Override
    public String toString() {
        return strRep;
    }
}
