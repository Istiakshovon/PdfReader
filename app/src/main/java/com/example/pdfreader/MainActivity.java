package com.example.pdfreader;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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

        permission();


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

                Uri uri = data.getData();

                InputStream is = getContentResolver().openInputStream(uri);



                copyInputStreamToFile(is, new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/file.pdf")));
            }
            else {
                Toast.makeText(this, "You haven't picked any file", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            Log.d("EXCEPTION_ERROR",e.toString());
            e.printStackTrace();
        }

    }

    private void copyInputStreamToFile(InputStream in, FileOutputStream out ) {
        try {
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
            getText();
        } catch (Exception e) {
            e.printStackTrace();
        }}

    private void getText(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String parsedText="";
                    StringBuilder builder = new StringBuilder();
                    PdfReader reader = new PdfReader(Environment.getExternalStorageDirectory().getAbsolutePath()+"/file.pdf");
                    int n = reader.getNumberOfPages();
                    for (int i = 10; i <n ; i++) {
                        String finalParsedText = parsedText;
                        runOnUiThread(() -> {
                            txt.setText(finalParsedText);
                        });
                        parsedText   = parsedText+ PdfTextExtractor.getTextFromPage(reader, i).trim()+"\n";
                        Log.d("for_loop", String.valueOf(i));
                        Log.d("PARSED_TEXT",parsedText+" ");
                    }
                    builder.append(parsedText);

                    reader.close();

//    System.out.println("TEXT FROM PDF : "+builder.toString());
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }).start();
    }
    private void permission(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(MainActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Toast.makeText(MainActivity.this, "Permission rational should be shown", Toast.LENGTH_SHORT).show();
                    }
                }).check();
        Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(MainActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Toast.makeText(MainActivity.this, "Permission rational should be shown", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }




}