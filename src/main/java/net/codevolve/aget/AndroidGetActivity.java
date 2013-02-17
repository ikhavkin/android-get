package net.codevolve.aget;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.inject.Inject;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AndroidGetActivity extends RoboActivity {
    private static final int SELECTED_FILE_TO_LOAD = 1000;
    private static final int SELECTED_DESTINATION_DIRECTORY = 1001;
    private static final String TAG = "AndroidGetActivity";

    @InjectView(R.id.loadListButton)
    private Button loadListButton;
    @InjectView(R.id.goButton)
    private Button goButton;
    @InjectView(R.id.fileListView)
    private ListView fileListView;
    @InjectView(R.id.destDirTextView)
    private TextView destDirTextView;
    private ArrayAdapter<Uri> fileArrayAdapter;

    @Inject
    private FileDownloader downloader;
    private ArrayList<Uri> fileUris;
    private String destDirectory;
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

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        setDestDirectory(downloadsDir.getAbsolutePath());
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

    public void chooseDestinationDir(View view) {
        Intent intent = new Intent("org.openintents.action.PICK_DIRECTORY");
        intent.setData(Uri.parse("file://"));

        startActivityForResult(intent, SELECTED_DESTINATION_DIRECTORY);
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
        downloader.enqueueFiles(fileUris, destDirectory);
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
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this).
                                    setTitle(failedToLoadListTitle).
                                    setMessage(failedToLoadListMessage).
                                    setPositiveButton(getString(R.string.OK), null);
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                }
            } else if (requestCode == SELECTED_DESTINATION_DIRECTORY) {
                Uri uri = data.getData();

                if (uri != null) {
                    String path = uri.getPath();

                    if (path != null) {
                        setDestDirectory(path);
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setDestDirectory(String path) {
        destDirTextView.setText(path);
        destDirectory = path;
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
