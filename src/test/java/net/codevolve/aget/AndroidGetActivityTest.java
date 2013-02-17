package net.codevolve.aget;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowActivity;
import com.xtremelabs.robolectric.shadows.ShadowAlertDialog;
import com.xtremelabs.robolectric.shadows.ShadowListView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Integration tests for @see AndroidGetActivity .
 */
@RunWith(InjectedTestRunner.class)
public class AndroidGetActivityTest {
    @Inject
    protected Injector injector;

    TestableAndroidGetActivity activity;

    private Button goButton;
    private Button loadButton;
    private Button changeDestButton;

    @Before
    public void setUp() {
        activity = new TestableAndroidGetActivity();
        activity.onCreate(Bundle.EMPTY);

        goButton = (Button) activity.findViewById(R.id.goButton);
        loadButton = (Button) activity.findViewById(R.id.loadListButton);
        changeDestButton = (Button) activity.findViewById(R.id.changeDestButton);
    }

    @Test
    public void When_no_files_are_loaded_Go_button_is_disabled() {
        assertThat(goButton.isEnabled(), equalTo(false));
    }

    @Test
    public void When_load_button_clicked_it_should_switch_to_get_content_activity() {
        // Arrange.
        activity.checkIntentStubResult = true;

        // Act.
        loadButton.performClick();

        // Assert.
        ShadowActivity.IntentForResult intentForResult =
                Robolectric.shadowOf(activity).getNextStartedActivityForResult();
        assertThat(intentForResult.intent.getAction(), equalTo(Intent.ACTION_GET_CONTENT));
    }

    @Test
    public void When_dest_dir_button_clicked_it_should_switch_to_pick_dir_activity() {
        // Arrange.
        activity.checkIntentStubResult = true;

        // Act.
        changeDestButton.performClick();

        // Assert.
        ShadowActivity.IntentForResult intentForResult =
                Robolectric.shadowOf(activity).getNextStartedActivityForResult();
        assertThat(intentForResult.intent.getAction(), equalTo("org.openintents.action.PICK_DIRECTORY"));
    }

    @Test
    public void When_load_clicked_and_cannot_get_content_it_should_show_error_dialog_and_not_switch_activity() {
        // Arrange.
        activity.checkIntentStubResult = null;

        // Act.
        loadButton.performClick();

        // Assert.
        ShadowAlertDialog alert = Robolectric.shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        String errorMessage = activity.getResources().getString(R.string.no_content_handler_message);
        assertThat(alert.getMessage(), equalTo(errorMessage));
        Intent intent = Robolectric.shadowOf(activity).getNextStartedActivity();
        assertThat(intent, equalTo(null));
    }

    @Test
    public void When_has_loaded_file_uri_Go_button_should_be_enabled() throws Exception {
        activity.loadFilesList(new StringReader("file://file1"));

        assertThat(goButton.isEnabled(), equalTo(true));
    }

    @Test
    public void When_has_loaded_damaged_uri_should_not_throw() throws Exception {
        activity.loadFilesList(new StringReader("sd % %# fsdfsdf sd "));
    }

    @Test
    public void When_has_loaded_3_file_uris_list_view_has_three_items() throws Exception {
        activity.loadFilesList(new StringReader("file://file1\nfile://file2\nfile://file3\n"));

        ListView listView = (ListView) activity.findViewById(R.id.fileListView);
        ShadowListView shadowListView = Robolectric.shadowOf(listView);
        int count = shadowListView.getAdapter().getCount();
        assertThat(count, equalTo(3));
    }

    @Test
    public void When_has_loaded_file_and_Go_clicked_should_clear_list() throws Exception {
        // Arrange.
        activity.loadFilesList(new StringReader("file://file1"));

        // Act.
        goButton.performClick();

        // Assert.
        ListView listView = (ListView) activity.findViewById(R.id.fileListView);
        ShadowListView shadowListView = Robolectric.shadowOf(listView);
        int count = shadowListView.getAdapter().getCount();
        assertThat(count, equalTo(0));
    }

    @Test
    public void When_has_loaded_file_and_Go_clicked_should_start_file_download() throws Exception {
        // Arrange.
        activity.loadFilesList(new StringReader("http://file1"));
        DownloadManager downloadManager = injector.getInstance(DownloadManager.class);
        when(downloadManager.enqueue(any(DownloadManager.Request.class))).
                thenReturn(123L);

        // Act.
        goButton.performClick();

        // Assert.
        verify(downloadManager, only()).enqueue(any(DownloadManager.Request.class));
    }

    @Test
    public void When_has_loaded_damaged_uri_and_Go_clicked_should_ignore_file() throws Exception {
        // Arrange.
        activity.loadFilesList(new StringReader("er fgd $$% W9 3"));
        DownloadManager downloadManager = injector.getInstance(DownloadManager.class);
        when(downloadManager.enqueue(any(DownloadManager.Request.class))).
                thenReturn(123L);

        // Act.
        goButton.performClick();

        // Assert.
        verify(downloadManager, never()).enqueue(any(DownloadManager.Request.class));
    }

    private static class TestableAndroidGetActivity extends AndroidGetActivity {
        Boolean checkIntentStubResult = null;

        @Override
        protected boolean checkIntent(Intent intent) {
            return checkIntentStubResult == null ?
                    super.checkIntent(intent) : checkIntentStubResult;
        }

        @Override
        public void loadFilesList(Reader reader) throws IOException {
            super.loadFilesList(reader);
        }
    }
}