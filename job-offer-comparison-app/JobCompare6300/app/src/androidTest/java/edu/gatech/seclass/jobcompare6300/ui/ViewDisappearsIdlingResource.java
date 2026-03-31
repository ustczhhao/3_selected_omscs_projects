package edu.gatech.seclass.jobcompare6300.ui;

import android.util.Log;
import android.view.View;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.ViewMatchers;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.not;
import org.hamcrest.Matcher;
import androidx.test.espresso.NoMatchingViewException;

public class ViewDisappearsIdlingResource implements IdlingResource {
    private final Matcher<View> viewMatcher;
    private ResourceCallback resourceCallback;
    private boolean viewGone = false;

    public ViewDisappearsIdlingResource(Matcher<View> viewMatcher) {
        this.viewMatcher = viewMatcher;
    }

    @Override
    public String getName() {
        return ViewDisappearsIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        try {
            onView(viewMatcher).check(matches(not(isDisplayed())));
            viewGone = true;
            Log.d("EspressoDebug", "View disappeared: " + viewMatcher.toString());
        } catch (NoMatchingViewException e) {
            viewGone = true;
            Log.d("EspressoDebug", "View was not found in hierarchy, assuming disappeared: " + viewMatcher.toString());
        } catch (AssertionError e) {
            viewGone = false;
            Log.d("EspressoDebug", "View is still visible: " + viewMatcher.toString());
        }

        if (viewGone && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }

        return viewGone;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    public static void waitForViewToDisappear(Matcher<View> viewMatcher, long timeoutMs) {
        Log.d("EspressoDebug", "Waiting for view to disappear: " + viewMatcher.toString());

        try {
            onView(viewMatcher).check(matches(not(isDisplayed())));
            Log.d("EspressoDebug", "View already disappeared, skipping wait.");
            return;
        } catch (NoMatchingViewException e) {
            Log.d("EspressoDebug", "View not in hierarchy, skipping wait.");
            return;
        } catch (AssertionError ignored) {
            Log.d("EspressoDebug", "View still visible, waiting...");
        }

        ViewDisappearsIdlingResource idlingResource = new ViewDisappearsIdlingResource(viewMatcher);
        IdlingRegistry.getInstance().register(idlingResource);

        try {
            Thread.sleep(timeoutMs);
        } catch (InterruptedException ignored) {
        } finally {
            IdlingRegistry.getInstance().unregister(idlingResource);
            Log.d("EspressoDebug", "Finished waiting for view to disappear.");
        }
    }
}
