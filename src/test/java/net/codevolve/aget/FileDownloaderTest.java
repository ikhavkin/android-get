package net.codevolve.aget;

import android.app.DownloadManager;
import android.net.Uri;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(InjectedTestRunner.class)
public class FileDownloaderTest {
    @Inject
    private FileDownloader downloader;
    private FileDownloader mockedDownloader;
    @Inject
    private DownloadManager downloadManager;

    @Before
    public void setUp() throws Exception {
        mockedDownloader = mock(FileDownloader.class);
    }

    @Test
    public void When_enqueueing_3_files_it_will_enqueue_each_file() {
        // Arrange.
        doCallRealMethod().when(mockedDownloader).enqueueFiles(Matchers.anyListOf(Uri.class));
        Uri file1 = Uri.parse("http://file1");
        Uri file2 = Uri.parse("http://file2");
        Uri file3 = Uri.parse("http://file3");

        // Act.
        mockedDownloader.enqueueFiles(Arrays.asList(file1, file2, file3));

        // Assert.
        verify(mockedDownloader).enqueueFile(file1);
        verify(mockedDownloader).enqueueFile(file2);
        verify(mockedDownloader).enqueueFile(file3);
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

    @Test
    public void When_enqueuing_not_HTTP_file_it_should_ignore_file() {
        // Arrange.
        when(downloadManager.enqueue(any(DownloadManager.Request.class))).
                thenReturn(123L);

        // Act.
        downloader.enqueueFile(Uri.parse("file://file1"));

        // Assert.
        verify(downloadManager, never()).enqueue(any(DownloadManager.Request.class));
    }
}
