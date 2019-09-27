package org.demo.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;

import com.vaadin.server.VaadinSession;

/**
 * A custom {@link SecurityContextHolderStrategy} that stores the {@link SecurityContext}
 * in the Vaadin Session if a Vaadin Session has already been established, or in a
 * ThreadLocal if no Vaadin Session is available.
 */
public class VaadinSessionSecurityContextHolderStrategy implements SecurityContextHolderStrategy {

	private static final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();

	@Override
	public void clearContext() {
		final VaadinSession session = VaadinSession.getCurrent();
		if (session != null) {
			session.setAttribute(SecurityContext.class, null);
		}
		contextHolder.remove();
	}

	@Override
	public SecurityContext getContext() {
		final VaadinSession session = VaadinSession.getCurrent();
		if (session != null) {
			SecurityContext ctx = session.getAttribute(SecurityContext.class);
			if (ctx == null) {
				ctx = createEmptyContext();
				session.setAttribute(SecurityContext.class, ctx);
			}
			return ctx;
		}
		else {
			SecurityContext ctx = contextHolder.get();
			if (ctx == null) {
				ctx = createEmptyContext();
				contextHolder.set(ctx);
			}
			return ctx;
		}
	}

	@Override
	public void setContext(SecurityContext context) {
		final VaadinSession session = VaadinSession.getCurrent();
		if (session != null) {
			session.setAttribute(SecurityContext.class, context);
		}
		contextHolder.set(context);
	}

	@Override
	public SecurityContext createEmptyContext() {
		return new SecurityContextImpl();
	}

}
