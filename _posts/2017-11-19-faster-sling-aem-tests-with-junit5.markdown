---
layout: post
author: Karol Lewandowski
title:  "Faster Sling/AEM tests with JUnit5"
date:   2017-11-19 12:00:00 +0100
categories: jekyll update
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
// TODO
{% endhighlight %}

#### Speed comparison
- before:

<img alt="JUnit4 tests execution time: 16.82s" src="/assets/tests-execution-junit5.png" style="width: 500px;">
- after:

<img alt="JUnit5 tests execution time: 3.08s" src="/assets/tests-execution-junit5.png" style="width: 500px;">


#### Notes
- if project has JUnit3/JUnit4 and JUnit5 tests, then 2 engines (Jupiter and Vintage) are run during the build, so time earned on tests level can be stolen by second engine execution (overhead is low though) - it's recommended to write only JUnit5 tests in new projects and migrate tests in existing ones


[junit5-extensions-lifecycle-callbacks]: http://junit.org/junit5/docs/current/user-guide/#extensions-lifecycle-callbacks
[tests-execution-junit4]: /assets/tests-execution-junit4.png "JUnit4 AemContext tests execution time"
[tests-execution-junit5]: /assets/tests-execution-junit5.png "JUnit5 AemContext tests execution time"
