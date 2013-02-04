package net.codevolve.aget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AndroidGetActivity extends Activity {
    private static final int SELECTED_FILE_TO_LOAD = 1000;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
