package net.codevolve.aget;

import android.app.Activity;
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

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        loadListButton = (Button) findViewById(R.id.loadListButton);
        goButton = (Button) findViewById(R.id.goButton);
        fileListView = (ListView) findViewById(R.id.fileListView);

        fileArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        fileListView.setAdapter(fileArrayAdapter);
    }

    /**
     * Opens an activity to load list of files to download.
     *
     * @param view view.
     */
    public void openFilesToLoadActivity(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(Uri.parse("file://"), "file/*");
        startActivityForResult(intent, SELECTED_FILE_TO_LOAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTED_FILE_TO_LOAD) {
                String result = data.getDataString();

                if (result != null) {
                    Uri uri = Uri.parse(result);

                    try {
                        loadFilesList(uri);
                    } catch (IOException exc) {
                        // todo: proper error handling
                        throw new RuntimeException(exc);
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadFilesList(Uri contentUri) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(contentUri);
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader bufReader = new BufferedReader(reader);

            // Read input file line by line into list view.
            while (true) {
                String line = bufReader.readLine();
                if (line == null) {
                    break;
                }

                fileArrayAdapter.add(line);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
