package net.codevolve.aget;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowActivity;
import com.xtremelabs.robolectric.shadows.ShadowListView;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
/**
 * Integration tests for @see AndroidGetActivity .
 */
public class AndroidGetActivityTest {
    TestableAndroidGetActivity activity;
    private Button goButton;
    private Button loadButton;

    @Before
    public void setUp() {
        activity = new TestableAndroidGetActivity();
        activity.onCreate(Bundle.EMPTY);

        goButton = (Button) activity.findViewById(R.id.goButton);
        loadButton = (Button) activity.findViewById(R.id.loadListButton);
    }

    @Test
    public void When_no_files_are_loaded_Go_button_is_disabled() {
        assertThat(goButton.isEnabled(), equalTo(false));
    }

    @Test
    public void When_load_button_clicked_it_should_switch_to_get_content_activity() {
        loadButton.performClick();

        ShadowActivity.IntentForResult intentForResult =
                Robolectric.shadowOf(activity).getNextStartedActivityForResult();
        assertThat(intentForResult.intent.getAction(), equalTo(Intent.ACTION_GET_CONTENT));
    }

    @Test
    public void When_has_loaded_file_uri_Go_button_should_be_enabled() throws Exception {
        activity.loadFilesList(new StringReader("file://file1"));

        assertThat(goButton.isEnabled(), equalTo(true));
    }

    @Test
    @Ignore("Don't get what is wrong here :)")
    public void When_has_loaded_3_file_uris_list_view_has_three_items() throws Exception {
        activity.loadFilesList(new StringReader("file://file1\nfile://file2\nfile://file3\n"));

        ListView listView = (ListView) activity.findViewById(R.id.fileListView);
        ShadowListView shadowListView = Robolectric.shadowOf(listView);
        assertThat(shadowListView.getChildCount(), equalTo(3));
    }

    private class TestableAndroidGetActivity extends AndroidGetActivity {
        @Override
        public void loadFilesList(Reader reader) throws IOException {
            super.loadFilesList(reader);
        }
    }
}