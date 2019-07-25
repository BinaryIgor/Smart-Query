package com.iprogrammerr.smart.query.mapping;

import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OrderedNestedMapMatcher<T, R, P> extends TypeSafeMatcher<Map<T, Map<R, List<P>>>> {

    private final Map<T, Map<R, List<P>>> expected;

    public OrderedNestedMapMatcher(Map<T, Map<R, List<P>>> expected) {
        this.expected = expected;
    }

    @Override
    protected boolean matchesSafely(Map<T, Map<R, List<P>>> item) {
        Iterator<Map.Entry<T, Map<R, List<P>>>> expectedOuter = expected.entrySet().iterator();
        Iterator<Map.Entry<T, Map<R, List<P>>>> actualOuter = item.entrySet().iterator();
        while (expectedOuter.hasNext()) {
            Map.Entry<T, Map<R, List<P>>> expectedNext = expectedOuter.next();
            Map.Entry<T, Map<R, List<P>>> actualNext = actualOuter.next();

            MatcherAssert.assertThat(expectedNext.getKey(), Matchers.equalTo(actualNext.getKey()));
            compareInner(expectedNext.getValue().entrySet().iterator(), actualNext.getValue().entrySet().iterator());
        }
        return true;
    }

    private void compareInner(Iterator<Map.Entry<R, List<P>>> expectedInner,
        Iterator<Map.Entry<R, List<P>>> actualInner) {
        while (expectedInner.hasNext()) {
            Map.Entry<R, List<P>> ee = expectedInner.next();
            Map.Entry<R, List<P>> ae = actualInner.next();

            MatcherAssert.assertThat(ee.getKey(), Matchers.equalTo(ae.getKey()));
            for (int i = 0; i < ee.getValue().size(); i++) {
                MatcherAssert.assertThat(ee.getValue().get(i), Matchers.equalTo(ae.getValue().get(i)));
            }
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getClass().getSimpleName() + " that deep compares each entry and ensures proper order");
    }
}
