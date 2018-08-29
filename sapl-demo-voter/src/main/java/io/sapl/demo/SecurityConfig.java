package io.sapl.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import io.sapl.demo.domain.User;
import io.sapl.demo.domain.UserRepo;
import io.sapl.demo.shared.marshalling.AuthenticationMapper;
import io.sapl.demo.shared.marshalling.HttpServletRequestMapper;
import io.sapl.demo.shared.marshalling.PatientMapper;
import io.sapl.spring.SAPLAuthorizator;
import io.sapl.spring.SaplBasedVoter;
import io.sapl.spring.marshall.mapper.SaplMapper;
import io.sapl.spring.marshall.mapper.SimpleSaplMapper;
import io.sapl.spring.marshall.obligation.SimpleObligationHandlerService;

@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	SAPLAuthorizator saplAuthorizer;

	@Bean
	WebSecurityConfigurerAdapter webSecurityConfigurerAdapter() {
		return new ConfigAdapter(getAccessDecisionManager(saplBasedVoter()));

	}

	@Bean
	AuthenticationManager authManager() {
		return authentication -> {
			String username = authentication.getPrincipal().toString();

			User user = userRepo.findById(username).orElseThrow(() -> new BadCredentialsException("no valid user name provided"));
			if (user.isDisabled()) {
				throw new DisabledException("user disabled");
			}
			String password = authentication.getCredentials().toString();
			if (!password.equals(user.getPassword())) {
				throw new BadCredentialsException("user and/or password do not match");
			}
			List<GrantedAuthority> userAuthorities = new ArrayList<>();
			user.getFunctions().forEach(function -> userAuthorities.add(new SimpleGrantedAuthority(function)));
			return new UsernamePasswordAuthenticationToken(username, password, userAuthorities);
		};
	}

	@Bean
	public SaplBasedVoter saplBasedVoter() {
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
	public SimpleObligationHandlerService getObligationHandlers() {
		SimpleObligationHandlerService sohs = new SimpleObligationHandlerService();
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
