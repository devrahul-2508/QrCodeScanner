package com.example.qrcodescanner;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.VIBRATE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

public class ScannerActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    TextView resultTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        window.setStatusBarColor(getResources().getColor(R.color.black));
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        resultTxt=findViewById(R.id.resultTxt);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);

        //checking permission
        if (checkPermission()) {
            // if permission is already granted display a toast message
            Toast.makeText(this, "Scanner Started", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }


        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        resultTxt.setText(result.toString());
                        if (result.toString().contains("http")){
                            resultTxt.setTextColor(Color.parseColor("#0645AD"));
                        }
                        else{
                            resultTxt.setTextColor(Color.parseColor("#000000"));
                        }

                        resultTxt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (result.toString().contains("http")){
                                    //open google link
                                    Uri uri = Uri.parse(result.toString()); // missing 'http://' will cause crashed
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
    private boolean checkPermission() {
        // here we are checking two permission that is vibrate
        // and camera which is granted by user and not.
        // if permission is granted then we are returning
        // true otherwise false.
        int camera_permission = ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA);
        int vibrate_permission = ContextCompat.checkSelfPermission(getApplicationContext(),VIBRATE);
        return camera_permission == PackageManager.PERMISSION_GRANTED && vibrate_permission == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        // this method is to request
        // the runtime permission.
        int PERMISSION_REQUEST_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, VIBRATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // this method is called when user
        // allows the permission to use camera.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            boolean cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean vibrateaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (cameraaccepted && vibrateaccepted) {
                Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied \n You cannot use app without providing permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
