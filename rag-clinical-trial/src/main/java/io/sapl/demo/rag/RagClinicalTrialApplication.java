/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.demo.rag;

import com.vaadin.flow.component.dependency.StyleSheet;
import io.sapl.api.SaplVersion;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Serial;

@Push
@SpringBootApplication
@StyleSheet(Lumo.UTILITY_STYLESHEET)
public class RagClinicalTrialApplication implements AppShellConfigurator {

    @Serial
    private static final long serialVersionUID = SaplVersion.VERSION_UID;

    public static void main(String[] args) {
        SpringApplication.run(RagClinicalTrialApplication.class, args);
    }

}
