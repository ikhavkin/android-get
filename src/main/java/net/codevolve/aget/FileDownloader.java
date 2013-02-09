package net.codevolve.aget;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

public class FileDownloader {
    private final DownloadManager downloadManager;

    public FileDownloader(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    public void enqueueFiles(Iterable<String> fileUrls) {
        for(String file : fileUrls) {
            enqueueFile(Uri.parse(file));
        }
    }

    public void enqueueFile(Uri fileUri) {
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
