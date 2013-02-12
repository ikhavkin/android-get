package net.codevolve.aget;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.google.inject.Inject;

public class FileDownloader {
    private final DownloadManager downloadManager;
    private static final String TAG = "FileDownloader";

    @Inject
    public FileDownloader(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    public void enqueueFiles(Iterable<Uri> files) {
        for (Uri file : files) {
            enqueueFile(file);
        }
    }

    public void enqueueFile(Uri fileUri) {
        String scheme = fileUri.getScheme();
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            Log.w(TAG, String.format("Ignoring not HTTP/HTTPS uri: %s", fileUri));
            return;
        }

        DownloadManager.Request request = new DownloadManager.Request(fileUri);
        String localFileName = fileUri.getLastPathSegment();
        request.setDescription(String.format("Downloading file %s...", localFileName));
        request.setTitle(localFileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, localFileName);

        downloadManager.enqueue(request);
    }
}
