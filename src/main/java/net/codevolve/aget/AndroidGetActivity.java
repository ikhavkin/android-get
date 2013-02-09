package net.codevolve.aget;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.*;
import java.util.ArrayList;

public class AndroidGetActivity extends Activity {
    private static final int SELECTED_FILE_TO_LOAD = 1000;
    private Button loadListButton;
    private Button goButton;
    private ListView fileListView;
    private ArrayAdapter<String> fileArrayAdapter;

    private FileDownloader downloader;
    private ArrayList<String> fileUris;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        fileUris = new ArrayList<>();
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloader = new FileDownloader(downloadManager);

        loadListButton = (Button) findViewById(R.id.loadListButton);
        goButton = (Button) findViewById(R.id.goButton);
        fileListView = (ListView) findViewById(R.id.fileListView);

        fileArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileUris);
        fileListView.setAdapter(fileArrayAdapter);
    }

    /**
     * Opens an activity to load list of files to download.
     */
    public void openFilesToLoadActivity(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(Uri.parse("file://"), "file/*");
        startActivityForResult(intent, SELECTED_FILE_TO_LOAD);
    }

    public void launchDownloads(View view) {
        downloader.enqueueFiles(fileUris);
        fileUris.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTED_FILE_TO_LOAD) {
                String result = data.getData().getPath();

                if (result != null) {
                    try {
                        loadFilesList(result);
                    } catch (IOException exc) {
                        // todo: proper error handling
                        throw new RuntimeException(exc);
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

            fileArrayAdapter.add(line);
        }

        goButton.setEnabled(!fileUris.isEmpty());
    }
}
