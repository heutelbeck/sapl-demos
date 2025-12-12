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
package io.sapl.demo;

import io.sapl.api.attributes.AttributeBroker;
import io.sapl.api.documentation.DocumentationBundle;
import io.sapl.api.documentation.LibraryDocumentation;
import io.sapl.api.functions.FunctionBroker;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.SourceLocation;
import io.sapl.api.model.Value;
import io.sapl.attributes.libraries.TimePolicyInformationPoint;
import io.sapl.compiler.CompilationContext;
import io.sapl.compiler.SaplCompiler;
import io.sapl.compiler.SaplCompilerException;
import io.sapl.documentation.LibraryDocumentationExtractor;
import io.sapl.functions.DefaultLibraries;
import io.sapl.grammar.ide.contentassist.ContentAssistConfigurationSource;
import io.sapl.grammar.ide.contentassist.ContentAssistPDPConfiguration;
import io.sapl.vaadin.Issue;
import org.eclipse.xtext.diagnostics.Severity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@Configuration
public class SaplConfiguration {

    @Bean
    ContentAssistConfigurationSource contentAssistConfigurationSource(
            FunctionBroker functionBroker, AttributeBroker attributeBroker) {

        var functionDocs = extractFunctionLibraryDocumentation();
        var pipDocs = extractPolicyInformationPointDocumentation();

        var allDocs = new ArrayList<LibraryDocumentation>();
        allDocs.addAll(functionDocs);
        allDocs.addAll(pipDocs);

        var documentationBundle = new DocumentationBundle(List.copyOf(allDocs));

        var innerA = ObjectValue.builder()
                .put("x", Value.of(0))
                .put("y", Value.of(1))
                .build();
        var abbaValue = ObjectValue.builder()
                .put("a", innerA)
                .put("b", Value.of("y"))
                .build();

        Map<String, Value> variables = Map.of("abba", abbaValue);

        var configuration = new ContentAssistPDPConfiguration(
                "demoConfig",
                "1",
                variables,
                documentationBundle,
                functionBroker,
                attributeBroker
        );

        return configId -> Optional.of(configuration);
    }

    private List<LibraryDocumentation> extractFunctionLibraryDocumentation() {
        var docs = new ArrayList<LibraryDocumentation>();
        for (var libraryClass : DefaultLibraries.STATIC_LIBRARIES) {
            try {
                docs.add(LibraryDocumentationExtractor.extractFunctionLibrary(libraryClass));
            } catch (Exception exception) {
                // Skip libraries that fail to extract
            }
        }
        try {
            docs.add(LibraryDocumentationExtractor.extractFunctionLibrary(DemoLib.class));
        } catch (Exception exception) {
            // Skip if extraction fails
        }
        return docs;
    }

    private List<LibraryDocumentation> extractPolicyInformationPointDocumentation() {
        var docs = new ArrayList<LibraryDocumentation>();
        try {
            docs.add(LibraryDocumentationExtractor.extractPolicyInformationPoint(TimePolicyInformationPoint.class));
        } catch (Exception exception) {
            // Skip if extraction fails
        }
        try {
            docs.add(LibraryDocumentationExtractor.extractPolicyInformationPoint(DemoPip.class));
        } catch (Exception exception) {
            // Skip if extraction fails
        }
        return docs;
    }

    /**
     * Provides a compile validator for the SAPL editor that detects semantic errors
     * beyond what Xtext parsing catches (e.g., division by zero in constant expressions).
     *
     * @param functionBroker  broker for resolving functions during compilation
     * @param attributeBroker broker for resolving attributes during compilation
     * @return a function that validates SAPL source and returns any compile errors as Issues
     */
    @Bean
    BiFunction<String, String, List<Issue>> saplCompileValidator(
            FunctionBroker functionBroker, AttributeBroker attributeBroker) {
        return (configId, source) -> {
            var compilationContext = new CompilationContext(functionBroker, attributeBroker);
            try {
                compilationContext.resetForNextDocument();
                SaplCompiler.compile(source, compilationContext);
                return List.of();
            } catch (SaplCompilerException exception) {
                return List.of(convertExceptionToIssue(exception));
            }
        };
    }

    private static Issue convertExceptionToIssue(SaplCompilerException exception) {
        SourceLocation location = exception.getLocation();
        Integer line = location != null ? location.line() : null;
        Integer offset = location != null ? location.start() : null;
        Integer length = location != null ? location.end() - location.start() : null;
        return new Issue(exception.getMessage(), Severity.ERROR, line, null, offset, length);
    }

}
