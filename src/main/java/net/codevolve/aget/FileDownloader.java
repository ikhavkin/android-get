package net.codevolve.aget;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import com.google.inject.Inject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FileDownloader {
    private final Logger logger = Logger.getLogger("FileDownloader");
    private final DownloadManager downloadManager;

    @Inject
    public FileDownloader(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    public void enqueueFiles(Iterable<String> fileUrls) {
        for (String file : fileUrls) {
            Uri fileUri;
            try {
                fileUri = Uri.parse(file);
            } catch (Exception exc) {
                logger.log(Level.SEVERE, String.format("Failed to parse URI: '%s'", file), exc);
                continue;
            }
            String scheme = fileUri.getScheme();
            if ("http".equals(scheme) || "https".equals(scheme)) {
                enqueueFile(fileUri);
            }
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
