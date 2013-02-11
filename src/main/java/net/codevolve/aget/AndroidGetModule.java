package net.codevolve.aget;

import android.app.DownloadManager;
import android.content.Context;
import com.google.inject.AbstractModule;
import roboguice.inject.SystemServiceProvider;

public class AndroidGetModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DownloadManager.class).toProvider(
                new SystemServiceProvider<DownloadManager>(Context.DOWNLOAD_SERVICE));
    }
}
