package co.nums.aem.blog.test.junit5;

import org.apache.sling.testing.mock.sling.ResourceResolverType;

public class JcrMockAemContext extends AemContext {

    JcrMockAemContext() {
        setResourceResolverType(ResourceResolverType.JCR_MOCK);
    }

}
