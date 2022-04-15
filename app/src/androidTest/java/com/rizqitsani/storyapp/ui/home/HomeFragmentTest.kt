package com.rizqitsani.storyapp.ui.home

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.rizqitsani.storyapp.JsonConverter
import com.rizqitsani.storyapp.R
import com.rizqitsani.storyapp.data.remote.retrofit.ApiConfig
import com.rizqitsani.storyapp.utils.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class HomeFragmentTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun getPagedStories_Success() {
        launchFragmentInContainer<HomeFragment>(null, R.style.Theme_StoryApp)

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.rv_story))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_placeholder))
            .check(matches(not(isDisplayed())))
        onView(withText("jajajjkdos"))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rv_story))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText("last"))
                )
            )
    }

    @Test
    fun getPagedStories_Empty() {
        launchFragmentInContainer<HomeFragment>(null, R.style.Theme_StoryApp)

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("empty_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.tv_placeholder))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rv_story))
            .check(matches(not(isDisplayed())))
        onView(withText(R.string.not_found))
            .check(matches(isDisplayed()))
    }
}