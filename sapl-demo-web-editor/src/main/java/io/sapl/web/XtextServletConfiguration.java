package io.sapl.web;

import java.time.Clock;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.sapl.functions.FilterFunctionLibrary;
import io.sapl.functions.StandardFunctionLibrary;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.grammar.web.SAPLServlet;
import io.sapl.interpreter.InitializationException;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.functions.FunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import io.sapl.interpreter.pip.AttributeContext;
import io.sapl.pip.TimePolicyInformationPoint;

@Configuration
@ComponentScan("io.sapl.grammar.ide.contentassist")
public class XtextServletConfiguration {

	@Bean
	public static ServletRegistrationBean<SAPLServlet> xTextRegistrationBean() {
		ServletRegistrationBean<SAPLServlet> registration = new ServletRegistrationBean<>(new SAPLServlet(),
				"/xtext-service/*");
		registration.setName("XtextServices");
		registration.setAsyncSupported(true);
		return registration;
	}

	@Bean
	public static FilterRegistrationBean<OrderedFormContentFilter> registration1(OrderedFormContentFilter filter) {
		FilterRegistrationBean<OrderedFormContentFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FunctionContext functionContext() throws InitializationException {
		FunctionContext context = new AnnotationFunctionContext(new FilterFunctionLibrary(),
				new StandardFunctionLibrary(), new TemporalFunctionLibrary());
		return context;
	}

	@Bean
	public AttributeContext attributeContext() throws InitializationException {
		AnnotationAttributeContext context = new AnnotationAttributeContext();
		context.loadPolicyInformationPoint(new TimePolicyInformationPoint(Clock.systemUTC()));
		return context;
	}
}
