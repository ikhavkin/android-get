package net.codevolve.aget;

import android.app.DownloadManager;
import android.net.Uri;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class FileDownloaderTest {
    private FileDownloader downloader;
    private FileDownloader mockedDownloader;
    private DownloadManager downloadManager;

    @Before
    public void setUp() throws Exception {
        downloadManager = mock(DownloadManager.class);
        downloader = new FileDownloader(downloadManager);
        mockedDownloader = mock(FileDownloader.class);
    }

    @Test
    public void When_enqueueing_3_files_it_will_enqueue_each_file() {
        // Arrange.
        doCallRealMethod().when(mockedDownloader).enqueueFiles(Matchers.anyListOf(String.class));

        // Act.
        mockedDownloader.enqueueFiles(Arrays.asList("http://file1", "http://file2", "http://file3"));

        // Assert.
        verify(mockedDownloader).enqueueFile(Uri.parse("http://file1"));
        verify(mockedDownloader).enqueueFile(Uri.parse("http://file2"));
        verify(mockedDownloader).enqueueFile(Uri.parse("http://file3"));
    }

    @Test
    public void When_enqueuing_file_it_will_start_download() {
        // Arrange.
        when(downloadManager.enqueue(any(DownloadManager.Request.class))).
                thenReturn(123L);

        // Act.
        downloader.enqueueFile(Uri.parse("http://file1"));

        // Assert.
        verify(downloadManager, only()).enqueue(any(DownloadManager.Request.class));
    }
}
