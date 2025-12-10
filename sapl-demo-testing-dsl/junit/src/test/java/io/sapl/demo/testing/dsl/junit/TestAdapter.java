package io.sapl.demo.testing.dsl.junit;

import java.util.Map;

import io.sapl.functions.libraries.FilterFunctionLibrary;
import io.sapl.functions.libraries.TemporalFunctionLibrary;
import io.sapl.test.grammar.sapltest.ImportType;
import io.sapl.test.junit.JUnitTestAdapter;

/**
 * Default TestAdapter that is used to execute the tests. the sapl-test-junit
 * module provides the JUnitTestAdapter as the default way to execute tests
 * defined in the SAPLTest language. the minimum setup requires an empty class
 * body (just extending JUnitTestAdapter) for the Junit Runner of Eclipse,
 * IntelliJ IDEA and Maven Surefire to discover tests By default all files with
 * the .sapltest file extensions located in src/test/resources and all
 * sub-folders are discovered
 */
public class TestAdapter extends JUnitTestAdapter {
    /**
     * override this method to register FunctionLibraries and PIPs they are then
     * used in test execution when the test definition has an import of the
     * corresponding type Static registrations expect the Class type
     * (STATIC_FUNCTION_LIBRARY and STATIC_PIP) Non-Static registrations expect the
     * concrete object to be passed this enables using PIPs and FunctionLibraries
     * that require special setup see pipImport.sapltest and
     * staticFunctionLibraryImport.sapltest for an example how to import
     * src/test/resources/unit
     * 
     * @return the complete Map of fixtureRegistrations to be used in tests executed
     *         with this TestAdapter.
     */
    @Override
    protected Map<ImportType, Map<String, Object>> getFixtureRegistrations() {
        return Map.of(ImportType.STATIC_FUNCTION_LIBRARY,
                Map.of("filter", FilterFunctionLibrary.class, "temporal", TemporalFunctionLibrary.class),
                ImportType.PIP, Map.of("upper", new TestPIP()));
    }
}
