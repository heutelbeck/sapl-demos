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
package io.sapl.demo.rag.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Service;

import io.sapl.api.model.ArrayValue;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.spring.pep.constraints.ConstraintHandler.Mapper;
import io.sapl.spring.pep.constraints.ConstraintHandlerProvider;
import io.sapl.spring.pep.constraints.ScopedConstraintHandler;
import io.sapl.spring.pep.constraints.Signal.InputSignal;
import io.sapl.spring.pep.constraints.SignalType;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.publisher.Mono;

/**
 * Mutates the protected method's first argument (a
 * {@code Mono<SearchRequest>}) to apply SAPL-derived filters before the
 * vector-store call. Triggered by SAPL obligations of type
 * {@code filterDocuments} on the {@link InputSignal} (the legacy
 * MethodInvocation handler phase).
 * </p>
 * Migrated from the legacy {@code MethodInvocationConstraintHandlerProvider}
 * to the unified {@link ConstraintHandlerProvider} interface returning a
 * {@link Mapper}{@code <MethodInvocation>}: the mapper mutates the live
 * arguments array on the invocation and returns the same invocation, so the
 * downstream reactive chain sees the rewritten request when subscribed.
 */
@Slf4j
@Service
class DocumentFilterConstraintHandlerProvider implements ConstraintHandlerProvider {

    private static final String CONSTRAINT_TYPE  = "filterDocuments";
    private static final int    DEFAULT_PRIORITY = 50;

    @Override
    public List<ScopedConstraintHandler> getConstraintHandlers(Value constraint, Set<SignalType> supportedSignals) {
        var signalOpt = ConstraintHandlerProvider.constraintTypeAndSignal(constraint, CONSTRAINT_TYPE,
                supportedSignals, InputSignal.SIGNAL_TYPE);
        if (signalOpt.isEmpty()) {
            return List.of();
        }
        // constraintTypeAndSignal already verified the value is a typed ObjectValue.
        var obligation = (ObjectValue) constraint;
        Mapper<MethodInvocation> mapper = invocation -> rewriteFirstArgument(invocation, obligation);
        return List.of(new ScopedConstraintHandler(mapper, signalOpt.get(), DEFAULT_PRIORITY));
    }

    @SuppressWarnings("unchecked")
    private static MethodInvocation rewriteFirstArgument(MethodInvocation invocation, ObjectValue obligation) {
        val args              = invocation.getArguments();
        val searchRequestMono = (Mono<SearchRequest>) args[0];
        if (searchRequestMono == null) {
            return invocation;
        }
        args[0] = searchRequestMono.map(request -> applyFilters(request, obligation));
        if (invocation instanceof ReflectiveMethodInvocation reflective) {
            reflective.setArguments(args);
        }
        return invocation;
    }

    private static SearchRequest applyFilters(SearchRequest request, ObjectValue obligation) {
        Filter.Expression filter = null;

        val excludeTypes = extractStringList(obligation, "excludeTypes");
        if (!excludeTypes.isEmpty()) {
            log.info("SAPL filtering: excluding document types {}", excludeTypes);
            val builder = new FilterExpressionBuilder();
            for (val type : excludeTypes) {
                val ne = builder.ne("type", type).build();
                filter = filter != null ? new Filter.Expression(Filter.ExpressionType.AND, filter, ne) : ne;
            }
        }

        if (obligation.containsKey("filterSite") && obligation.get("filterSite") instanceof TextValue(String value)) {
            log.info("SAPL filtering: restricting to site '{}' (and 'all')", value);
            val builder = new FilterExpressionBuilder();
            val siteEq  = builder.eq("site", value).build();
            val allEq   = builder.eq("site", "all").build();
            if (filter != null) {
                // Distribute AND over OR to avoid jsonpath precedence bug in PgVectorStore:
                // OR(AND(filter, site=x), AND(filter, site=all))
                val finalFilter = filter;
                filter = new Filter.Expression(Filter.ExpressionType.OR,
                        new Filter.Expression(Filter.ExpressionType.AND, finalFilter, siteEq),
                        new Filter.Expression(Filter.ExpressionType.AND, finalFilter, allEq));
            } else {
                filter = new Filter.Expression(Filter.ExpressionType.OR, siteEq, allEq);
            }
        }

        if (filter == null) {
            log.info("SAPL filtering: no filters applied, full access granted");
            return request;
        }

        val existingFilter = request.getFilterExpression();
        val combinedFilter = existingFilter != null
                ? new Filter.Expression(Filter.ExpressionType.AND, existingFilter, filter)
                : filter;

        return SearchRequest.builder().query(request.getQuery()).topK(request.getTopK())
                .similarityThreshold(request.getSimilarityThreshold()).filterExpression(combinedFilter).build();
    }

    private static List<String> extractStringList(ObjectValue ov, String key) {
        val result = new ArrayList<String>();
        if (ov.containsKey(key) && ov.get(key) instanceof ArrayValue array) {
            for (val element : array) {
                if (element instanceof TextValue(String value)) {
                    result.add(value);
                }
            }
        }
        return result;
    }
}
