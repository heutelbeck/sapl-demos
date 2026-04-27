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
package io.sapl.demo.webflux;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.spring.pep.http.reactive.SaplServerHttpSecurityConfigurer;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    MapReactiveUserDetailsService userDetailsService() {
        @SuppressWarnings("deprecation") // Demo Code
        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("user").roles("USER").build();
        return new MapReactiveUserDetailsService(user);
    }

    /*
     * The configurer customizer narrows the subscription to the three fields
     * the demo policies reference (subject..authority, action.method,
     * resource.requestedURI). The default factory ships the entire
     * serialized request which works but is verbose. To replace the factory
     * globally instead, declare a single
     * @Bean ReactiveAuthorizationSubscriptionFactory and the configurer
     * call below collapses to
     * SaplServerHttpSecurityConfigurer.apply(http, context).
     */
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ApplicationContext context,
            ObjectMapper mapper) {
        SaplServerHttpSecurityConfigurer.apply(http, context,
                c -> c.subscriptionFactory((auth, exchange) -> Mono.just(AuthorizationSubscription.of(auth,
                        Map.of("method", exchange.getRequest().getMethod().name()),
                        Map.of("requestedURI", exchange.getRequest().getURI().getPath()), mapper))));
        return http.formLogin(withDefaults()).httpBasic(withDefaults()).build();
    }
}
