package co.nums.aem.blog.test.junit5;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableMap;

public class AemContextProvider implements ParameterResolver, AfterTestExecutionCallback {

    private static final Namespace AEM_CONTEXT_NAMESPACE = Namespace.create(AemContextProvider.class);

    private static final Map<Type, Supplier<? extends AemContext>> CONTEXT_SUPPLIERS;
    static {
        Map<Type, Supplier<? extends AemContext>> suppliers = new HashMap<>();
        suppliers.put(ResourceResolverMockAemContext.class, ResourceResolverMockAemContext::new);
        suppliers.put(JcrMockAemContext.class, JcrMockAemContext::new);
        suppliers.put(JcrOakAemContext.class, JcrOakAemContext::new);
        CONTEXT_SUPPLIERS = unmodifiableMap(suppliers);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return CONTEXT_SUPPLIERS.containsKey(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        AemContext aemContext = CONTEXT_SUPPLIERS.get(parameterContext.getParameter().getType()).get();
        aemContext.setUpContext();
        getStore(extensionContext).put(extensionContext.getRequiredTestMethod(), aemContext);
        return aemContext;
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        AemContext aemContext = getAemContext(extensionContext);
        if (aemContext != null) {
            aemContext.tearDownContext();
        }
    }

    private AemContext getAemContext(ExtensionContext extensionContext) {
        return getStore(extensionContext).get(extensionContext.getRequiredTestMethod(), AemContext.class);
    }

    private Store getStore(ExtensionContext context) {
        return context.getStore(AEM_CONTEXT_NAMESPACE);
    }

}
