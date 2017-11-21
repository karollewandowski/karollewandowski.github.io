package co.nums.aem.blog.core.models;

import co.nums.aem.blog.test.junit5.AemContextProvider;
import co.nums.aem.blog.test.junit5.JcrOakAemContext;
import co.nums.aem.blog.test.junit5.ResourceResolverMockAemContext;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextProvider.class)
class JUnit5HelloWorldModelTest {

    @RepeatedTest(value = 2)
    void shouldDoSomethingWithJcrOakAemContext(JcrOakAemContext context) {
        // anything
    }

    @RepeatedTest(value = 20)
    void shouldDoSomethingWithResourceResolverMockAemContext(ResourceResolverMockAemContext context) {
        // anything
    }

    @RepeatedTest(value = 10)
    void shouldDoSomethingWithoutAemContext() {
        // anything
    }

}
