package io.sapl.vaadindemo.security;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.spring.security.RequestUtil;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategy;
import com.vaadin.flow.spring.security.VaadinDefaultRequestCache;
import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;

import io.sapl.vaadin.base.VaadinAuthorizationSubscriptionBuilderService;
import io.sapl.vaadindemo.views.LoginView;
import lombok.AllArgsConstructor;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

  private VaadinDefaultRequestCache vaadinDefaultRequestCache;
  private RequestUtil requestUtil;
  protected ObjectFactory<ObjectMapper> objectMapperFactory;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    SecurityContextHolder.setStrategyName(
            VaadinAwareSecurityContextHolderStrategy.class.getName());

    http.csrf().ignoringRequestMatchers(
            requestUtil::isFrameworkInternalRequest);
    http.requestCache().requestCache(vaadinDefaultRequestCache);

    ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry = http
            .authorizeRequests();

    urlRegistry.requestMatchers(requestUtil::isFrameworkInternalRequest)
            .permitAll();
    urlRegistry.requestMatchers(requestUtil::isAnonymousEndpoint)
            .permitAll();
    urlRegistry.requestMatchers(requestUtil::isAnonymousRoute).permitAll();
    urlRegistry.requestMatchers(getDefaultHttpSecurityPermitMatcher())
            .permitAll();
    urlRegistry.anyRequest().authenticated();

    setLoginView(http, LoginView.class);
  }

  /**
   * Allows access to static resources, bypassing Spring security.
   */
  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/images/**");
    super.configure(web);
  }

  /**
   * Demo UserDetailService which only provide two hardcoded in memory users and
   * their roles. NOTE: This should not be used in real world applications.
   */
  @Bean
  @Override
  public UserDetailsService userDetailsService() {
    UserDetails admin = User.withUsername("admin").password("{noop}admin").roles("Admin").build();
    UserDetails user = User.withUsername("user").password("{noop}user").roles("USER").build();
    return new InMemoryUserDetailsManager(admin, user);
  }

  @Bean
  protected VaadinAuthorizationSubscriptionBuilderService vaadinAuthorizationSubscriptionBuilderService() {
    var expressionHandler = new DefaultMethodSecurityExpressionHandler();
    return new VaadinAuthorizationSubscriptionBuilderService(expressionHandler, objectMapperFactory.getObject());
  }
}