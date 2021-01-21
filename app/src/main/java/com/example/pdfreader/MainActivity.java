package com.example.pdfreader;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
                String path = data.getStringExtra(mediaPath);
                Log.d("PATH",path+" ");
                String data1 = data.toString();
                Log.d("DATA1",data1);

                Uri PathHolder = data.getData();
                File file = new File(PathHolder.toString());
                Log.v("###", "yo " + PathHolder);
                Log.d("PATH",PathUtils.getPath(getApplicationContext(),PathHolder));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String parsedText="";
                            StringBuilder builder = new StringBuilder();
                            PdfReader reader = new PdfReader(PathUtils.getPath(getApplicationContext(),PathHolder));
                            int n = reader.getNumberOfPages();
                            for (int i = 10; i <n ; i++) {
                                parsedText   = parsedText+ PdfTextExtractor.getTextFromPage(reader, i).trim()+"\n";
                                Log.d("for_loop", String.valueOf(i));
                                Log.d("PARSED_TEXT",parsedText+" ");
                            }
                            builder.append(parsedText);

                            reader.close();
                            runOnUiThread(() -> {
                                txt.setText(builder.toString());
                            });

//    System.out.println("TEXT FROM PDF : "+builder.toString());
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }).start();
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