package io.sapl.pdp.multitenant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "io.sapl.pdp.multitenant")
public class MultiTenantPolicyDecisionPointProperties {
	String path;
}
