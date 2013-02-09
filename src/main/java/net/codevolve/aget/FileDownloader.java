package net.codevolve.aget;

import android.app.DownloadManager;
import android.net.Uri;

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

        downloadManager.enqueue(request);
    }

    public void close() {
    }
}
