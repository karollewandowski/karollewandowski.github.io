---
layout: post
author: Karol Lewandowski
title:  "Faster Sling/AEM tests with JUnit5"
date:   2017-11-19 12:00:00 +0100
categories:
---
#### `SlingContext`/`AemContext`
- allow to create repository and populate it with test data
- provided as JUnit4 `@Rule`, so context instance is created before every test in class even if it's unnecessary
- the same `ResourceResolverType` (eg. `JCR_OAK`) is used for all tests, even if the faster one (eg. `RESOURCERESOLVER_MOCK`) is sufficient for some tests

#### JUnit5
- modern testing framework released in 2017
- allows to write JUnit3, JUnit4 and JUnit5 tests in the same project
- easily extendable by implementing [interfaces allowing to inject logic into test lifecycle][junit5-extensions-lifecycle-callbacks]
- can be used to inject parameter directly to test method:
{% highlight java %}
@Test
void shouldVerifySomething(JcrOakAemContext context) {
    context.addModelsForPackage("co.nums.aem.blog.models");
    context.load().json("co/nums/aem/blog/data/test.json", "/content/blog");
    Resource testResource = context.resourceResolver().getResource("/content/blog/test-node");

    TestModel sut = testResource.adaptTo(TestModel.class);

    // assertions
}
{% endhighlight %}

#### Extension
{% highlight java %}
import io.wcm.testing.mock.aem.context.AemContextImpl;

public class AemContext extends AemContextImpl {
    protected void setUpContext() {
        super.setUp();
    }
    protected void tearDownContext() {
        super.tearDown();
    }
}
{% endhighlight %}
{% highlight java %}
import org.apache.sling.testing.mock.sling.ResourceResolverType;

public class ResourceResolverMockAemContext extends AemContext {
    ResourceResolverMockAemContext() {
        setResourceResolverType(ResourceResolverType.RESOURCERESOLVER_MOCK);
    }
}
{% endhighlight %}
{% highlight java %}
import org.apache.sling.testing.mock.sling.ResourceResolverType;

public class JcrMockAemContext extends AemContext {
    JcrMockAemContext() {
        setResourceResolverType(ResourceResolverType.JCR_MOCK);
    }
}
{% endhighlight %}
{% highlight java %}
import org.apache.sling.testing.mock.sling.ResourceResolverType;

public class JcrOakAemContext extends AemContext {
    JcrOakAemContext() {
        setResourceResolverType(ResourceResolverType.JCR_OAK);
    }
}
{% endhighlight %}
{% highlight java %}
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
{% endhighlight %}

#### Speed comparison
- compared JUnit4 and JUnit5 test classes with:
  - 2 empty test methods using `JCR_OAK` context
  - 20 empty test methods using `RESOURCERESOLVER_MOCK` context
  - 10 empty test methods without using context
- JUnit4:
  - run from IntelliJ IDEA: **9.276s**
  - run with `mvn clean test`: **8.696s**
- JUnit5:
  - run from IntelliJ IDEA: **2.116s**
  - run with `mvn clean test`: **1.892s**
- only one class per JUnit4/JUnit5 was tested and ~1.5s is the time of creating AEM context for the first time, so once it is ready, next tests will be faster by about 15-20x (assuming that tests will use similar resource resolvers)
- build speed improvement is significant

#### Source code
- [here you go][source-code]

#### Notes
- if project has JUnit3/JUnit4 and JUnit5 tests, then 2 engines (Jupiter and Vintage) are run during the build, so time earned on tests level can be stolen by second engine execution (few seconds of overhead) - it's recommended to write only JUnit5 tests in new projects and migrate tests in existing ones
- the same approach can be used for Sling Mocks (and other Sling-related mocks)


[junit5-extensions-lifecycle-callbacks]: http://junit.org/junit5/docs/current/user-guide/#extensions-lifecycle-callbacks
[source-code]: https://github.com/karollewandowski/karollewandowski.github.io/tree/master/_example-projects/junit5-aem-context-providers/nums-blog
