package net.codevolve.aget;

import android.app.DownloadManager;
import com.google.inject.AbstractModule;

import static org.mockito.Mockito.mock;

public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DownloadManager.class).toInstance(mock(DownloadManager.class));
    }
}
