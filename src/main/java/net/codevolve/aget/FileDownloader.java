package net.codevolve.aget;

import android.net.Uri;

public class FileDownloader {
    public void enqueueFiles(Iterable<String> fileUrls) {
        for(String file : fileUrls) {
            enqueueFile(Uri.parse(file));
        }
    }

    public void enqueueFile(Uri fileUri) {
        throw new UnsupportedOperationException();
    }

    public void close() {
        throw new UnsupportedOperationException("Not implemented method!");
    }
}
