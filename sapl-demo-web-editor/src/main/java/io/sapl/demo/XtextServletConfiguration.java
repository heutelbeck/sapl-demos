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
import io.sapl.api.functions.FunctionBroker;
import io.sapl.attributes.CachingAttributeBroker;
import io.sapl.attributes.InMemoryAttributeRepository;
import io.sapl.attributes.libraries.TimePolicyInformationPoint;
import io.sapl.functions.DefaultFunctionBroker;
import io.sapl.functions.DefaultLibraries;
import io.sapl.grammar.web.SAPLServlet;
import io.sapl.test.grammar.web.SAPLTestServlet;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@ComponentScan("io.sapl.grammar.ide.contentassist")
public class XtextServletConfiguration {

    @Bean
    ServletRegistrationBean<SAPLServlet> xTextRegistrationBean() {
        var registration = new ServletRegistrationBean<>(new SAPLServlet(), "/xtext-service/*");
        registration.setName("XtextServices");
        registration.setAsyncSupported(true);
        return registration;
    }

    @Bean
    ServletRegistrationBean<SAPLTestServlet> xTextTestRegistrationBean() {
        var registration = new ServletRegistrationBean<>(new SAPLTestServlet(), "/sapl-test/xtext-service/*");
        registration.setName("SaplTestXtextServices");
        registration.setAsyncSupported(true);
        return registration;
    }

    @Bean
    FilterRegistrationBean<OrderedFormContentFilter> registration1(OrderedFormContentFilter filter) {
        var registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    FunctionBroker functionBroker() {
        var broker = new DefaultFunctionBroker();
        for (var libraryClass : DefaultLibraries.STATIC_LIBRARIES) {
            broker.loadStaticFunctionLibrary(libraryClass);
        }
        broker.loadStaticFunctionLibrary(DemoLib.class);
        return broker;
    }

    @Bean
    AttributeBroker attributeBroker() {
        var repository = new InMemoryAttributeRepository(Clock.systemUTC());
        var broker = new CachingAttributeBroker(repository);
        broker.loadPolicyInformationPointLibrary(new TimePolicyInformationPoint(Clock.systemUTC()));
        broker.loadPolicyInformationPointLibrary(new DemoPip());
        return broker;
    }

}
