package io.sapl.argumentmodification.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringJUnitConfig
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = ArgumentModificationDemoApplication.class)
class ArgumentModificationDemoApplicationTests {

    private static final String BASIC_AUTH      = "Basic dGVzdFVzZXI6dGVzdFBhc3N3b3Jk";
    private static final String EXPECTED_RESULT = "if all text is lowercase the service was called. right to this message there is a lowercase 'hello modification' then the oblication successfully modified the method arguments->hello modification";

    @Autowired
    MockMvc mvc;

    @Test
    void testSomeStringMvc() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/string").header("Authorization", BASIC_AUTH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(EXPECTED_RESULT));
    }

}
