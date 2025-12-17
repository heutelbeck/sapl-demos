package io.sapl.demo.testing.dsl.plain.storage;

import java.util.Map;

/**
 * minimal working example to represent some kind of storage for SAPLTest
 * definitions with a static accessor
 */
public class TestStorage {
    private TestStorage() {
    }

    private static final String SINGLE_REQUIREMENT = """
            //top level definition of the requirement, each .sapltest file can contain 1:n requirement definitions. Start and End of the requirment block is marked by curly braces. The requirement name needs to be unique within one file.
            requirement "Policy Simple should grant read access for willi on something" {
                scenario "willi tries to read something" //define a scenario with a specific name. This scenario represents a single isolated test case within the requirment. A requirment can contain 1:n scenario definitions. The scenario name needs to be unique within a requirement.
                given //marks the start of the "given" section which is used to define the policy under test (and a lot more which is shown in the other examples)
                    - policy "policySimple" //define the name of the policy to test, has to be located in src/main/resources (or in a folder structure below this folder)
                when subject "willi" attempts action "read" on resource "something" //defines the AuthorizationSubscription that should be used for the test
                expect permit; // defines the expected AuthorizationDecision to compare against the actual outcome. scenario is finished with an ";"

                scenario "not_willi tries to read something" //second scenario for negative test
                given
                    - policy "policySimple"
                when "not_willi" attempts "read" on "something" //short form to define the AuthorizationSubscription omitting the keywords "subject", "action" and "resource"
                expect deny; // expect deny here since the policy only permits read access for "willi" and not for "not_willi"
            }""";

    private static final String STREAMING = """
            requirement "ROLE_DOCTOR can access when secondOf time.Now is larger than 4" {
                given
                    - policy "policyStreaming"
                    - static-function-library "temporal"

                scenario "subject ROLE_DOCTOR can access 2 times without attribute timing"
                given
                    - attribute "time.now" emits "2021-02-08T16:16:01.000Z", "2021-02-08T16:16:02.000Z", "2021-02-08T16:16:03.000Z", "2021-02-08T16:16:04.000Z", "2021-02-08T16:16:05.000Z", "2021-02-08T16:16:06.000Z"
                when subject "ROLE_DOCTOR" attempts action "read" on resource "heartBeatData"
                // expect block is a list of expected AuthorizationDecision in order
                expect
                - not-applicable 4 times
                - permit 2 times;

                scenario "subject ROLE_DOCTOR can access 2 times with attribute timing"
                given
                    - virtual-time // use virtual-time feature of project reactor to be able to provide timing for attribute
                    - attribute "time.now" emits "2021-02-08T16:16:01.000Z", "2021-02-08T16:16:02.000Z", "2021-02-08T16:16:03.000Z", "2021-02-08T16:16:04.000Z", "2021-02-08T16:16:05.000Z", "2021-02-08T16:16:06.000Z" with timing "PT1S" //specify Duration in Java Duration format
                when subject "ROLE_DOCTOR" attempts action "read" on resource "heartBeatData"
                //then block where you can adjust attribute behaviour or specify wait time in between expects
                //then/expect blocks can define multiple steps and must alternate
                //also it is required that the last block is an expect block
                then
                    - wait "PT1S"
                expect
                    - not-applicable once
                then
                    - wait "PT1S"
                expect
                    - not-applicable once
                then
                    - wait "PT1S"
                expect
                    - not-applicable once
                then
                    - wait "PT1S"
                expect
                    - not-applicable once
                then
                    - wait "PT1S"
                expect
                    - permit once
                then
                    - wait "PT1S"
                expect
                    - permit once;

                scenario "subject ROLE_DOCTOR can access once without attribute timing and with time.secondOf mocking"
                given
                    - attribute "time.now" emits "2021-02-08T16:16:01.000Z", "2021-02-08T16:16:02.000Z"
                    //create 2 mock entries here, first invocation will return 4, second one 5 and will verify that there a 2 calls in total for time.secondOf
                    - function "time.secondOf" maps to stream 4
                    - function "time.secondOf" maps to stream 5
                when subject "ROLE_DOCTOR" attempts action "read" on resource "heartBeatData"
                expect
                    - not-applicable once
                    - permit once;

                scenario "subject ROLE_DOCTOR can access once without attribute timing and with time.secondOf mocking with multiple values"
                given
                    - attribute "time.now" emits "foo", "bar", "baz"
                    //mock time.secondOf to return values in sequence
                    - function "time.secondOf" maps to stream 3, 4, 5
                when subject "ROLE_DOCTOR" attempts action "read" on resource "heartBeatData"
                expect
                    - not-applicable once
                    - not-applicable once
                    - permit once;

                scenario "subject ROLE_DOCTOR can access 3 times without attribute timing and with time.secondOf mocking mocking with times called verification"
                given
                    - attribute "time.now" emits "foo", "bar", "baz"
                    //mock time.secondOf to return 5 and verify it is called 3 times
                    - function "time.secondOf" maps to 5 is called 3 times
                when subject "ROLE_DOCTOR" attempts action "read" on resource "heartBeatData"
                expect
                    - permit 3 times;
            }""";

    private static final String INTEGRATION = """
            //structure follow exactly the available structure for unit tests.
            //the only difference is, how the used policies are defined
            //see unit tests for details/explanations of allowed syntax besides policy definition
            requirement "should grant read access for WILLI on foo depending on policy set and PDP Config" {
                scenario "WILLI tries to read foo using single identifier"
                given
                    - set "demoSet" // refer to the identifier in your test setup to define how this is resolved to a list of Policies and a PDP Configuration
                when "WILLI" attempts "read" on "foo" //defines the AuthorizationSubscription that should be used for the test
                expect permit;

                scenario "WILLI tries to read foo using concrete policies definition with default pdp configuration"
                given
                    // if the pdp Configuration path is ommited, the default Strategy DENY_OVERRIDES is used
                    - policies "policy_A", "policy_B", "policy_C"
                when "WILLI" attempts "read" on "foo"
                expect deny;

                scenario "WILLI tries to read foo using concrete policies definition with concrete pdp configuration"
                given
                    - policies "policy_A", "policy_B", "policy_C" with pdp configuration "demoPDPConfig" //search policiesIT folder for a pdp.json file to load configuration from in this case same behavior as first scenario
                when "WILLI" attempts "read" on "foo"
                expect permit;

                scenario "WILLI tries to read foo using single identifier and variable override"
                given
                    - set "demoSet"
                    // additionally you can specify the used pdp variables manually, there overwriting the ones defined in pdp.json
                    - pdp variables {}
                when "WILLI" attempts "read" on "foo"
                expect permit;

                scenario "WILLI tries to read foo using single identifier and variable + combining algorithm override"
                //given has to follow order:
                    // - policy definiton
                    // - pdp variables (optional)
                    // - pdp combining-algorithm (optional)
                    // - environment (optional)
                    // - Imports / Mock Definitions (optional)
                given
                    - set "demoSet"
                    // additionally you can specify the used pdp variables manually, there overwriting the ones defined in pdp.json
                    - pdp variables {}
                    // additionally you can specify the used combining-algorithm manually, there overwriting the one defined in pdp.json
                    - pdp combining-algorithm deny-overrides
                    - environment {}
                    - static-function-library "filter"
                when "WILLI" attempts "read" on "foo" //defines the AuthorizationSubscription that should be used for the test
                expect deny;
            }
            """;

    public static Map<String, String> getTestStore() {
        return Map.of("singleRequirement", SINGLE_REQUIREMENT, "streaming", STREAMING, "integration", INTEGRATION);
    }
}
