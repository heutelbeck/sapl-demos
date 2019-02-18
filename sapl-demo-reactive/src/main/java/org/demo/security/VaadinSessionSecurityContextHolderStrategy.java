package org.demo.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;

import com.vaadin.server.VaadinSession;

/**
 * A custom {@link SecurityContextHolderStrategy} that stores the {@link SecurityContext} in the Vaadin Session.
 */
public class VaadinSessionSecurityContextHolderStrategy implements SecurityContextHolderStrategy {

    private static boolean isUsedInUnitTest;

    private static SecurityContext securityContextForUnitTest;

    public static void initForUnitTest() {
        isUsedInUnitTest = true;
        securityContextForUnitTest = new SecurityContextImpl();
    }

    @Override
    public void clearContext() {
        if (!isUsedInUnitTest) {
            getSession().setAttribute(SecurityContext.class, null);
        }
    }

    @Override
    public SecurityContext getContext() {
        if (isUsedInUnitTest) {
            return securityContextForUnitTest;
        }

        final VaadinSession session = getSession();
        SecurityContext context = session.getAttribute(SecurityContext.class);
        if (context == null) {
            context = createEmptyContext();
            session.setAttribute(SecurityContext.class, context);
        }
        return context;
    }

    @Override
    public void setContext(SecurityContext context) {
        if (!isUsedInUnitTest) {
            getSession().setAttribute(SecurityContext.class, context);
        }
    }

    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }

    private static VaadinSession getSession() {
        final VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            throw new IllegalStateException("No VaadinSession bound to current thread");
        }
        return session;
    }
}
