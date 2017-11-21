package co.nums.aem.blog.test.junit5;

import org.apache.sling.testing.mock.sling.ResourceResolverType;

public class ResourceResolverMockAemContext extends AemContext {

    ResourceResolverMockAemContext() {
        setResourceResolverType(ResourceResolverType.RESOURCERESOLVER_MOCK);
    }

}
