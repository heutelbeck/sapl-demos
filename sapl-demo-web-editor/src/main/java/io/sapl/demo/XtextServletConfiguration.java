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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sapl.attributes.broker.api.AttributeStreamBroker;
import io.sapl.attributes.broker.impl.AnnotationPolicyInformationPointLoader;
import io.sapl.attributes.broker.impl.CachingAttributeStreamBroker;
import io.sapl.attributes.broker.impl.InMemoryPolicyInformationPointDocumentationProvider;
import io.sapl.attributes.pips.time.TimePolicyInformationPoint;
import io.sapl.functions.DefaultLibraries;
import io.sapl.grammar.web.SAPLServlet;
import io.sapl.interpreter.InitializationException;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.functions.FunctionContext;
import io.sapl.test.grammar.web.SAPLTestServlet;
import io.sapl.validation.ValidatorFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

@Configuration
@ComponentScan("io.sapl.grammar.ide.contentassist")
public class XtextServletConfiguration {

    @Bean
    ServletRegistrationBean<SAPLServlet> xTextRegistrationBean() {
        ServletRegistrationBean<SAPLServlet> registration = new ServletRegistrationBean<>(new SAPLServlet(),
                "/xtext-service/*");
        registration.setName("XtextServices");
        registration.setAsyncSupported(true);
        return registration;
    }

    @Bean
    ServletRegistrationBean<SAPLTestServlet> xTextTestRegistrationBean() {
        ServletRegistrationBean<SAPLTestServlet> registration = new ServletRegistrationBean<>(new SAPLTestServlet(),
                "/sapl-test/xtext-service/*");
        registration.setName("SaplTestXtextServices");
        registration.setAsyncSupported(true);
        return registration;
    }

    @Bean
    FilterRegistrationBean<OrderedFormContentFilter> registration1(OrderedFormContentFilter filter) {
        FilterRegistrationBean<OrderedFormContentFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    FunctionContext functionContext() throws InitializationException {
        return new AnnotationFunctionContext(List::of, () -> DefaultLibraries.STATIC_LIBRARIES);
    }

    @Bean
    AttributeStreamBroker attributeStreamBroker() {
        final var mapper                = new ObjectMapper();
        final var validatorFactory      = new ValidatorFactory(mapper);
        final var attributeStreamBroker = new CachingAttributeStreamBroker();
        final var docsProvider          = new InMemoryPolicyInformationPointDocumentationProvider();
        final var pipLoader             = new AnnotationPolicyInformationPointLoader(attributeStreamBroker,
                docsProvider, validatorFactory);
        pipLoader.loadPolicyInformationPoint(new TimePolicyInformationPoint(Clock.systemUTC()));
        return attributeStreamBroker;
    }
    
}
