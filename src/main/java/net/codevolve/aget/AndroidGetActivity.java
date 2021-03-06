package net.codevolve.aget;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.google.inject.Inject;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class AndroidGetActivity extends RoboActivity {
    private static final int SELECTED_FILE_TO_LOAD = 1000;
    private static final String TAG = "AndroidGetActivity";

    @InjectView(R.id.loadListButton)
    private Button loadListButton;
    @InjectView(R.id.goButton)
    private Button goButton;
    @InjectView(R.id.fileListView)
    private ListView fileListView;
    private ArrayAdapter<Uri> fileArrayAdapter;

    @Inject
    private FileDownloader downloader;
    private ArrayList<Uri> fileUris;
    private boolean hasCheckedGetContent = false;

    @InjectResource(R.string.no_content_handler_message)
    private String noContentHandlerMessage;
    @InjectResource(R.string.no_content_handler_title)
    private String noContentHandlerTitle;
    @InjectResource(R.string.failed_to_load_list_title)
    private String failedToLoadListTitle;
    @InjectResource(R.string.failed_to_load_list_message)
    private String failedToLoadListMessage;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // todo: proper UI dialog...
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable throwable) {
//                logger.log(Level.SEVERE, String.format("Unhandled exception in thread %s", thread.getId()), throwable);
//            }
//        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        fileUris = new ArrayList<Uri>();
        fileArrayAdapter = new ArrayAdapter<Uri>(this, android.R.layout.simple_list_item_1, fileUris);
        fileListView.setAdapter(fileArrayAdapter);
    }

    /**
     * Opens an activity to load list of files to download.
     */
    public void openFilesToLoadActivity(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(Uri.parse("file://"), "file/*");

        if (!checkIntent(intent)) {
            return;
        }

        startActivityForResult(intent, SELECTED_FILE_TO_LOAD);
    }

    protected boolean checkIntent(Intent intent) {
        if (!hasCheckedGetContent) {
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                    PackageManager.GET_ACTIVITIES);

            if (list == null || list.isEmpty()) {
                Log.e(TAG, "No ACTION_GET_CONTENT handler!");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(noContentHandlerTitle);
                alertDialogBuilder.setMessage(noContentHandlerMessage);
                alertDialogBuilder.setPositiveButton(getString(R.string.OK), null);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return false;
            } else {
                hasCheckedGetContent = true;
            }
        }
        return true;
    }

    public void launchDownloads(View view) {
        downloader.enqueueFiles(fileUris);
        fileArrayAdapter.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTED_FILE_TO_LOAD) {
                Uri uri = data.getData();

                if (uri != null) {
                    String path = uri.getPath();

                    if (path != null) {
                        try {
                            loadFilesList(path);
                        } catch (IOException exc) {
                            Log.e(TAG, "Failed to load list of files!", exc);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                            alertDialogBuilder.setTitle(failedToLoadListTitle);
                            alertDialogBuilder.setMessage(failedToLoadListMessage);
                            alertDialogBuilder.setPositiveButton(getString(R.string.OK), null);
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadFilesList(String filename) throws IOException {
        Reader reader = null;
        try {
            reader = new FileReader(filename);
            loadFilesList(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    protected void loadFilesList(Reader reader) throws IOException {
        BufferedReader bufReader = new BufferedReader(reader);

        // Read input file line by line into list view.
        while (true) {
            String line = bufReader.readLine();
            if (line == null) {
                break;
            }

            Uri fileUri;
            try {
                fileUri = Uri.parse(line);
                fileArrayAdapter.add(fileUri);
            } catch (Exception exc) {
                Log.w(TAG, String.format("failed to parse URI: %s", line), exc);
            }
        }

        goButton.setEnabled(!fileUris.isEmpty());
    }
}
