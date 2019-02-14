package org.demo;

import java.util.Arrays;
import java.util.List;

import org.demo.domain.UserRepo;
import org.demo.shared.marshalling.AuthenticationMapper;
import org.demo.shared.marshalling.HttpServletRequestMapper;
import org.demo.shared.marshalling.PatientMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import io.sapl.api.pdp.mapping.SaplMapper;
import io.sapl.api.pdp.obligation.ObligationHandlerService;
import io.sapl.pep.SAPLAuthorizer;
import io.sapl.pep.pdp.mapping.SimpleSaplMapper;
import io.sapl.pep.pdp.obligation.SimpleObligationHandlerService;
import io.sapl.spring.SaplBasedVoter;

@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter(AccessDecisionManager accessDecisionManager) {
		return new ConfigAdapter(accessDecisionManager);
	}

	@Bean
	public AuthenticationManager authManager(UserRepo userRepo) {
		return new AuthManager(userRepo);
	}

	@Bean
	public SaplBasedVoter saplBasedVoter(SAPLAuthorizer saplAuthorizer) {
		return new SaplBasedVoter(saplAuthorizer);
	}

	@Bean
	public AccessDecisionManager getAccessDecisionManager(SaplBasedVoter saplBasedVoter) {

		List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(
				// The WebExpressionVoter enables us to use SpEL (Spring Expression Language) to
				// authorize the requests using the @PreAuthorize annotation.
				new WebExpressionVoter(),
				// The RoleVoter votes if any of the configuration attributes starts with the
				// String “ROLE_”.
				new RoleVoter(),
				// The AuthenticatedVoter will cast a vote based on the Authentication object’s
				// level of authentication – specifically looking for either a fully
				// authenticated pricipal
				new AuthenticatedVoter(),
				// this one is our own, we use SAPL and PDP as base here

				saplBasedVoter);

		// there are basically three different choices:
		// - AffirmativeBased – grants access if any of the AccessDecisionVoters return
		// an affirmative vote
		// - ConsensusBased – grants access if there are more affirmative votes than
		// negative (ignoring users who abstain)
		// - UnanimousBased – grants access if every voter either abstains or returns an
		// affirmative vote

		return new UnanimousBased(decisionVoters);
	}

	@Bean
	public ObligationHandlerService getObligationHandlers() {
		ObligationHandlerService sohs = new SimpleObligationHandlerService();
		sohs.register(new EmailObligationHandler());
		sohs.register(new CoffeeObligationHandler());
		sohs.register(new SimpleLoggingObligationHandler());
		return sohs;
	}

	@Bean
	public SaplMapper getSaplMapper() {
		SaplMapper saplMapper = new SimpleSaplMapper();
		saplMapper.register(new AuthenticationMapper());
		saplMapper.register(new HttpServletRequestMapper());
		saplMapper.register(new PatientMapper());
		return saplMapper;
	}
}
