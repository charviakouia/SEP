package de.uni_passau.fim.blackBoxTests.test_suite;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_passau.fim.blackBoxTests.tests.T10;
import de.uni_passau.fim.blackBoxTests.tests.T11;
import de.uni_passau.fim.blackBoxTests.tests.T20;

@RunWith(Suite.class)
@SuiteClasses({
    T10.class, T11.class, T20.class
})
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
