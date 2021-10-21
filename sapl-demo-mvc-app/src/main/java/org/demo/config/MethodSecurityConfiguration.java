/*
 * Copyright © 2017-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package org.demo.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.spring.config.SaplMethodSecurityConfiguration;
import io.sapl.spring.constraints.ConstraintEnforcementService;
import io.sapl.spring.subscriptions.AuthorizationSubscriptionBuilderService;

@Configuration
@EnableGlobalMethodSecurity
public class MethodSecurityConfiguration extends SaplMethodSecurityConfiguration {

	public MethodSecurityConfiguration(ObjectFactory<PolicyDecisionPoint> pdpFactory,
			ObjectFactory<ConstraintEnforcementService> constraintHandlerFactory,
			ObjectFactory<ObjectMapper> objectMapperFactory,
			ObjectFactory<AuthorizationSubscriptionBuilderService> subscriptionBuilderFactory) {
		super(pdpFactory, constraintHandlerFactory, objectMapperFactory, subscriptionBuilderFactory);
	}

}
