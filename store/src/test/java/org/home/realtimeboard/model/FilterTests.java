package org.home.realtimeboard.model;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FilterTests extends AbstractTestNGSpringContextTests {
    @DataProvider(name = "isValidTestData")
    public Object[][] getIsValidTestDate() {
        return new Object[][]{
                {Filter.builder().build(), true},
                {Filter.builder().top(0).bottom(10).left(0).right(10).build(), true},
                {Filter.builder().bottom(10).left(0).right(10).build(), false},
                {Filter.builder().top(0).left(0).right(10).build(), false},
                {Filter.builder().top(0).bottom(10).right(10).build(), false},
                {Filter.builder().top(0).bottom(10).left(0).build(), false}
        };
    }

    @Test(dataProvider = "isValidTestData")
    public void isValidTest(Filter filter, boolean result) {
        Assert.assertEquals(filter.isValid(), result);
    }

    @DataProvider(name = "isEmptyTestData")
    public Object[][] getIsEmptyTestDate() {
        return new Object[][]{
                {Filter.builder().build(), true},
                {Filter.builder().top(0).bottom(10).left(0).right(10).build(), false},
                {Filter.builder().top(0).build(), false},
                {Filter.builder().bottom(0).build(), false},
                {Filter.builder().left(0).build(), false},
                {Filter.builder().right(0).build(), false}
        };
    }

    @Test(dataProvider = "isEmptyTestData")
    public void isEmptyTest(Filter filter, boolean result) {
        Assert.assertEquals(filter.isEmpty(), result);
    }

    @DataProvider(name = "predicateTestData")
    public Object[][] getPredicateTestData() {
        return new Object[][]{
                {
                        Filter.builder().top(0).bottom(10).left(0).right(10).build(),
                        Widget.builder().x(0).y(0).width(5).height(5).build(),
                        true
                },
                {
                        Filter.builder().top(0).bottom(10).left(0).right(10).build(),
                        Widget.builder().x(5).y(0).width(5).height(5).build(),
                        true
                },
                {
                        Filter.builder().top(0).bottom(10).left(0).right(10).build(),
                        Widget.builder().x(0).y(5).width(5).height(5).build(),
                        true
                },
                {
                        Filter.builder().top(0).bottom(10).left(0).right(10).build(),
                        Widget.builder().x(5).y(5).width(5).height(5).build(),
                        true
                },
                {
                        Filter.builder().top(0).bottom(10).left(0).right(10).build(),
                        Widget.builder().x(2).y(2).width(5).height(5).build(),
                        true
                },
                {
                        Filter.builder().top(0).bottom(10).left(0).right(10).build(),
                        Widget.builder().x(-1).y(0).width(5).height(5).build(),
                        false
                },
                {
                        Filter.builder().top(0).bottom(10).left(0).right(10).build(),
                        Widget.builder().x(6).y(0).width(5).height(5).build(),
                        false
                },
                {
                        Filter.builder().top(0).bottom(10).left(0).right(10).build(),
                        Widget.builder().x(0).y(-1).width(5).height(5).build(),
                        false
                },
                {
                        Filter.builder().top(0).bottom(10).left(0).right(10).build(),
                        Widget.builder().x(0).y(6).width(5).height(5).build(),
                        false
                },
                {
                        Filter.builder().top(0).bottom(10).left(0).right(10).build(),
                        Widget.builder().x(100).y(100).width(5).height(5).build(),
                        false
                }
        };
    }

    @Test(dataProvider = "predicateTestData")
    public void predicateTest(Filter filter, Widget widget, boolean result) {
        Assert.assertEquals(filter.toPredicate().test(widget), result);
    }
}
