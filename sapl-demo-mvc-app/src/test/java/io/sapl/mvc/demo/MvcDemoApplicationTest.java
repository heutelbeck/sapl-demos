/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.mvc.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
import com.gargoylesoftware.htmlunit.html.HtmlHeading3;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import io.sapl.mvc.demo.controller.UIController;
import io.sapl.mvc.demo.domain.DemoData;
import lombok.SneakyThrows;

@DirtiesContext
@AutoConfigureMockMvc
@SpringBootTest(classes = MvcDemoApplication.class)
class MvcDemoApplicationTest {

    static record IntegerAndString(int integer, String string) {
    }

    static Collection<String> userNameSource() {
        return DemoData.USER_NAMES;
    }

    static Collection<String> doctorNameSource() {
        return DemoData.DOCTOR_NAMES;
    }

    static Collection<String> nonDoctorNameSource() {
        return DemoData.NON_DOCTOR_NAMES;
    }

    static void assertPatientsList(HtmlPage page, IntegerAndString... idsAndNames) {

        var tableRows = page.<HtmlTableRow>getByXPath("//table/tbody/tr");
        assertThat(tableRows).hasSize(idsAndNames.length + 1);
        assertThat(tableRows.get(0).getCells()).hasSize(2);
        assertThat(tableRows.get(0).getCell(0).getVisibleText()).isEqualTo("ID");
        assertThat(tableRows.get(0).getCell(1).getVisibleText()).isEqualTo("NAME");

        var patientLinks = page.<HtmlAnchor>getByXPath("//table/tbody/tr/td[last()]/a");
        assertThat(patientLinks).hasSize(idsAndNames.length);

        var index = 0;
        for (var idAndName : idsAndNames) {
            assertThat(patientLinks.get(index).getVisibleText()).isEqualTo(idAndName.string());
            assertThat(patientLinks.get(index).getHrefAttribute()).isEqualTo("/patients/" + idAndName.integer());
            index++;
            assertThat(tableRows.get(index).getCells()).hasSize(2);
            assertThat(tableRows.get(index).getCell(0).getVisibleText())
                    .isEqualTo(Integer.toString(idAndName.integer()));
        }
    }

    WebClient mockClient;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    UIController uiController;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockClient = MockMvcWebClientBuilder.mockMvcSetup(mockMvc).build();
    }

    @Test
    void contextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("userNameSource")
    void when_home_then_showHome(String username) {
        final String url = "http://localhost:8080/";
        setAuthentication(username);
        HtmlPage page = mockClient.getPage(url);

        var header = page.<HtmlHeading1>getByXPath("//h1");
        assertThat(header).hasSize(1);
        assertThat(header.get(0).getVisibleText()).isEqualTo("Hello " + username + "!");

        var patientLink = page.<HtmlAnchor>getByXPath("//h3/a");
        assertThat(patientLink).hasSize(1);
        assertThat(patientLink.get(0).getVisibleText()).isEqualTo("Patient List");
        assertThat(patientLink.get(0).getHrefAttribute()).isEqualTo("/patients");

        var logoutForm = page.<HtmlForm>getByXPath("//form");
        assertThat(logoutForm).hasSize(1);
        assertThat(logoutForm.get(0).getActionAttribute()).isEqualTo("/logout");
        assertThat(logoutForm.get(0).getMethodAttribute()).isEqualTo("post");

        var logoutSubmit = page.<HtmlInput>getByXPath("//form/input[@type='submit']");
        assertThat(logoutSubmit).hasSize(1);
        assertThat(logoutSubmit.get(0).getValueAttribute()).isEqualTo("Sign Out");

    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("doctorNameSource")
    void when_patientsList_as_doctor_then_showPatientsList_and_showCreateButton(String username) {
        final String url = "http://localhost:8080/patients";
        setAuthentication(username);
        HtmlPage page = mockClient.getPage(url);

        var header = page.<HtmlHeading3>getByXPath("//h3");
        assertThat(header).hasSize(1);
        assertThat(header.get(0).getVisibleText()).isEqualTo("List of Patients");

        assertPatientsList(page, new IntegerAndString(1, "Lenny"), new IntegerAndString(2, "Karl"));

        var forms = page.<HtmlForm>getByXPath("//form");
        assertThat(forms).hasSize(2);

        var newPatientForm = page.<HtmlForm>getByXPath("//form[1]");
        assertThat(newPatientForm).hasSize(1);
        assertThat(newPatientForm.get(0).getActionAttribute()).isEqualTo("/patients/new");
        var newPatientFormInput = page.<HtmlInput>getByXPath("//form[1]/input");
        assertThat(newPatientFormInput).hasSize(1);
        assertThat(newPatientFormInput.get(0).getTypeAttribute()).isEqualTo("submit");
        assertThat(newPatientFormInput.get(0).getValueAttribute()).isEqualTo("Register new patient...");

        var homeForm = page.<HtmlForm>getByXPath("//form[last()]");
        assertThat(homeForm).hasSize(1);
        assertThat(homeForm.get(0).getActionAttribute()).isEqualTo("/");
        var homeFormInput = page.<HtmlInput>getByXPath("//form[last()]/input");
        assertThat(homeFormInput).hasSize(1);
        assertThat(homeFormInput.get(0).getTypeAttribute()).isEqualTo("submit");
        assertThat(homeFormInput.get(0).getValueAttribute()).isEqualTo("Home");
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("nonDoctorNameSource")
    void when_patientsList_as_nonDoctor_then_showPatientsList(String username) {
        final String url = "http://localhost:8080/patients";
        setAuthentication(username);
        HtmlPage page = mockClient.getPage(url);

        var header = page.<HtmlHeading3>getByXPath("//h3");
        assertThat(header).hasSize(1);
        assertThat(header.get(0).getVisibleText()).isEqualTo("List of Patients");

        assertPatientsList(page, new IntegerAndString(1, "Lenny"), new IntegerAndString(2, "Karl"));

        var forms = page.<HtmlForm>getByXPath("//form");
        assertThat(forms).hasSize(1);

        var homeForm = page.<HtmlForm>getByXPath("//form[last()]");
        assertThat(homeForm).hasSize(1);
        assertThat(homeForm.get(0).getActionAttribute()).isEqualTo("/");
        var homeFormInput = page.<HtmlInput>getByXPath("//form[last()]/input");
        assertThat(homeFormInput).hasSize(1);
        assertThat(homeFormInput.get(0).getTypeAttribute()).isEqualTo("submit");
        assertThat(homeFormInput.get(0).getValueAttribute()).isEqualTo("Home");
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("doctorNameSource")
    void when_newPatient_as_doctor_then__(String username) {
        final String url = "http://localhost:8080/patients/new";
        setAuthentication(username);
        HtmlPage page = mockClient.getPage(url);

        var header = page.<HtmlHeading3>getByXPath("//h3");
        assertThat(header).hasSize(1);
        assertThat(header.get(0).getVisibleText()).isEqualTo("Create a new patient");

        var forms = page.<HtmlForm>getByXPath("//form");
        assertThat(forms).hasSize(2);

        var newPatientForm = page.<HtmlForm>getByXPath("//form[1]");
        assertThat(newPatientForm).hasSize(1);

        var homeForm = page.<HtmlForm>getByXPath("//form[last()]");
        assertThat(homeForm).hasSize(1);
        assertThat(homeForm.get(0).getActionAttribute()).isEqualTo("/");
        var homeFormInput = page.<HtmlInput>getByXPath("//form[last()]/input");
        assertThat(homeFormInput).hasSize(1);
        assertThat(homeFormInput.get(0).getTypeAttribute()).isEqualTo("submit");
        assertThat(homeFormInput.get(0).getValueAttribute()).isEqualTo("Back to Home");
    }

    private void setAuthentication(String username) {
        var user  = userDetailsService.loadUserByUsername(username);
        var authn = new UsernamePasswordAuthenticationToken(user, DemoData.DEFAULT_RAW_PASSWORD, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authn);
    }

}
