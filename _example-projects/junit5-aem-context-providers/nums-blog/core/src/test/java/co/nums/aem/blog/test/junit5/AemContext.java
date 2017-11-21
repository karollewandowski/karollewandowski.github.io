package co.nums.aem.blog.test.junit5;

import io.wcm.testing.mock.aem.context.AemContextImpl;

public class AemContext extends AemContextImpl {

    protected void setUpContext() {
        super.setUp();
    }

    protected void tearDownContext() {
        super.tearDown();
    }

}
