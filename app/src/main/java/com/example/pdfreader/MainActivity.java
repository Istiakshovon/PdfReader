package com.example.pdfreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button btnFile;
    TextView txt;


    String mediaPath, mediaPath1;
    String[] mediaColumns = {MediaStore.Video.Media._ID};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFile = findViewById(R.id.btn_file);
        txt = findViewById(R.id.txt);


        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setType("application/pdf");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), 3);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {


            // When an file is picked
            if (requestCode == 3 && resultCode == RESULT_OK && null != data) {

                // Get the file from data
//                String path = data.getStringExtra(mediaPath);
                File file = new File(String.valueOf(data.getData()));
                Uri selectedFile = Uri.fromFile(new File(file.getAbsolutePath()));
                String[] filePathColumn = {MediaStore.Files.FileColumns.MEDIA_TYPE};


//                Log.d("OPEN_STREAM", String.valueOf(getContentResolver().openInputStream(selectedFile)));
                Log.d("PATH",String.valueOf(data.getData()));
//                Log.d("URI_SELECTED_FILE", String.valueOf(selectedFile));

//                String path = getPath(getApplicationContext(),data.getData());

//                Log.d("PATH",path);

//                Cursor cursor = getContentResolver().query(selectedFile, filePathColumn, null, null, null);
//                assert cursor != null;
//                cursor.moveToFirst();


//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                mediaPath = cursor.getString(columnIndex);
//                txt.setText(String.valueOf(data.getData()));
//                cursor.close();



                try {
                    String parsedText="";
                    PdfReader reader = new PdfReader(String.valueOf(selectedFile));
                    int n = reader.getNumberOfPages();
                    for (int i = 0; i <n ; i++) {
                        parsedText   = parsedText+ PdfTextExtractor.getTextFromPage(reader, i+1).trim()+"\n"; //Extracting the content from the different pages
                    }
                    Log.d("PDF_TEXT",parsedText);
                    System.out.println(parsedText);
                    reader.close();
                } catch (Exception e) {
                    System.out.println(e);
                    Log.d("EXCEPTION_ERROR",e.toString());
                }
            }
            else {
                Toast.makeText(this, "You haven't picked any file", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            Log.d("EXCEPTION_ERROR",e.toString());
        }

    }


    // Getting Selected File ID
    public long getFileId(Activity context, Uri fileUri) {
        Cursor cursor = context.managedQuery(fileUri, mediaColumns, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            return cursor.getInt(columnIndex);
        }
        return 0;
    }


    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                System.out.println("getPath() uri: " + uri.toString());
                System.out.println("getPath() uri authority: " + uri.getAuthority());
                System.out.println("getPath() uri path: " + uri.getPath());

                // ExternalStorageProvider
                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    System.out.println("getPath() docId: " + docId + ", split: " + split.length + ", type: " + type);

                    // This is for checking Main Memory
                    if ("primary".equalsIgnoreCase(type)) {
                        if (split.length > 1) {
                            return Environment.getExternalStorageDirectory() + "/" + split[1] + "/";
                        } else {
                            return Environment.getExternalStorageDirectory() + "/";
                        }
                        // This is for checking SD Card
                    } else {
                        return "storage" + "/" + docId.replace(":", "/");
                    }

                }
            }
        }
        return null;
    }
}