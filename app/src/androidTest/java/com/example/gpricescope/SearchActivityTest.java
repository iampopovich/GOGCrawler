package com.example.gpricescope;

import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.KeyEvent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SearchActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void searchViewIsAboveRecyclerView() {
        onView(withId(R.id.searchView)).check(isCompletelyAbove(withId(R.id.priceRecyclerView)));
    }

    @Test
    public void recyclerViewIsBelowSearchView() {
        onView(withId(R.id.priceRecyclerView)).check(isCompletelyBelow(withId(R.id.searchView)));
    }

    @Test
    public void searchPriceForExistingProduct() {
        onView(withId(R.id.searchView)).perform(typeText("https://www.gog.com/en/game/cyberpunk_2077"));
        onView(withId(R.id.searchView)).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withId(R.id.priceRecyclerView)).check(matches(isDisplayed()));
        onIdle();
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        activityRule.getScenario().onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.priceRecyclerView);
            assert (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() > 0);
        });
    }

    @Test
    public void searchPriceForNonExistingProduct() {
        onView(withId(R.id.searchView)).perform(typeText("https://www.gog.com/en/game/cyberpukk_2088"));
        onView(withId(R.id.searchView)).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withId(R.id.priceRecyclerView)).check(matches(isDisplayed()));
        onIdle();
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        activityRule.getScenario().onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.priceRecyclerView);
            assert (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() == 0);
        });
    }

    @Test
    public void searchPriceForInvalidUrl() {
        onView(withId(R.id.searchView)).perform(typeText("hippo://www.gog.com/en/game/cyberpunk_2077"));
        onView(withId(R.id.searchView)).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withText("Invalid URL error")).inRoot(isDialog()).check(matches(isDisplayed()));

    }

    @Test
    public void searchPriceForEmptyUrl() {
        onView(withId(R.id.searchView)).perform(typeText(""));
        onView(withId(R.id.searchView)).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withText("Invalid URL error")).inRoot(isDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void clearSearchResult(){
    }

}