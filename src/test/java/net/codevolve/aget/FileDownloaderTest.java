package net.codevolve.aget;

import android.net.Uri;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class FileDownloaderTest {
    private FileDownloader downloader;

    @Before
    public void setUp() throws Exception {
        downloader = mock(FileDownloader.class);
    }

    @After
    public void tearDown() throws Exception {
        if (downloader != null) {
            downloader.close();
        }
    }

    @Test
    public void When_enqueueing_3_files_it_will_enqueue_each_file() {
        // Arrange.
        doCallRealMethod().when(downloader).enqueueFiles(Matchers.anyListOf(String.class));

        // Act.
        downloader.enqueueFiles(Arrays.asList("file://file1", "file://file2", "file://file3"));

        // Assert.
        verify(downloader).enqueueFile(Uri.parse("file://file1"));
        verify(downloader).enqueueFile(Uri.parse("file://file2"));
        verify(downloader).enqueueFile(Uri.parse("file://file3"));
    }
}
