package co.nums.aem.blog.test.junit5;

import org.apache.sling.testing.mock.sling.ResourceResolverType;

public class JcrOakAemContext extends AemContext {

    JcrOakAemContext() {
        setResourceResolverType(ResourceResolverType.JCR_OAK);
    }

}
