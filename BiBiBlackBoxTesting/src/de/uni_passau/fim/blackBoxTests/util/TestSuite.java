package de.uni_passau.fim.blackBoxTests.util;

import de.uni_passau.fim.blackBoxTests.tests.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({T08.class, T09.class, T10.class, T11.class, T20.class, T30.class, T40.class,
        T50.class, T60.class, T70.class, T80.class, T90.class, T100.class, T110.class,
        T120.class, T130.class, DatabaseCleaner.class})
public class TestSuite {

    @BeforeClass
    public static void setUp() {
        Driver.getDriver();
    }

    @AfterClass
    public static void tearDown() {
        Driver.shutdown();
    }

}