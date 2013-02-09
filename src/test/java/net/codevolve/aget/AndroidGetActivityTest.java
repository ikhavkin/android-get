package net.codevolve.aget;

import android.os.Bundle;
import android.widget.Button;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
/**
 * Integration tests for @see AndroidGetActivity .
 */
public class AndroidGetActivityTest {
    AndroidGetActivity activity;
    private Button goButton;
    private Button loadButton;

    @Before
    public void setUp() {
        activity = new AndroidGetActivity();
        activity.onCreate(Bundle.EMPTY);

        goButton = (Button) activity.findViewById(R.id.goButton);
        loadButton = (Button) activity.findViewById(R.id.loadListButton);
    }

    @Test
    public void shouldHaveHappySmiles() throws Exception {
        String appName = activity.getResources().getString(R.string.app_name);
        assertThat(appName, equalTo("aget"));
    }

    @Test
    public void When_clicking_Go_without_adding_files_it_should_not_throw() {
        goButton.performClick();
    }
}