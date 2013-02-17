package net.codevolve.aget;

import android.app.DownloadManager;
import android.net.Uri;
import android.util.Log;
import com.google.inject.Inject;

import java.io.File;

public class FileDownloader {
    private final DownloadManager downloadManager;
    private static final String TAG = "FileDownloader";

    @Inject
    public FileDownloader(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    public void enqueueFiles(Iterable<Uri> files, String destDirectory) {
        for (Uri file : files) {
            enqueueFile(file, destDirectory);
        }
    }

    public void enqueueFile(Uri fileUri, String destDirectory) {
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

        Uri.Builder destUriBuilder = Uri.fromFile(new File(destDirectory)).buildUpon();
        destUriBuilder.appendPath(localFileName);
        request.setDestinationUri(destUriBuilder.build());

        downloadManager.enqueue(request);
    }
}
