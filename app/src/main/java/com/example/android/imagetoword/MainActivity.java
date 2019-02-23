package com.example.android.imagetoword;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    int Text_Reco_Request_Code=100;
    int Barcode_Reader_request_Code=200;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void textrec(View view)
    {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,Text_Reco_Request_Code);
    }
    public void Barcode(View view)
    {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,Barcode_Reader_request_Code);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Text_Reco_Request_Code)
        {
            if(resultCode==RESULT_OK)
            {
                Bitmap photo=(Bitmap)data.getExtras().get("data");
                textRecognition(photo);
            }
            else if(requestCode==RESULT_CANCELED)
            {
                Toast.makeText(this,"Operation Cancelled",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,"Failed To Take Image",Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==Barcode_Reader_request_Code)
        {
            if(resultCode==RESULT_OK)
            {
                Bitmap photo=(Bitmap)data.getExtras().get("data");
                BarCodeRecognition(photo);
            }
            else if(requestCode==RESULT_CANCELED)
            {
                Toast.makeText(this,"Operation Cancelled",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,"Failed To Take Image",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void BarCodeRecognition(Bitmap photo)
    {
        FirebaseVisionImage image=FirebaseVisionImage.fromBitmap(photo);

        FirebaseVisionBarcodeDetector detector= FirebaseVision.getInstance().getVisionBarcodeDetector();

        Task<List<FirebaseVisionBarcode>> result=detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> Barcodes)
                    {
                        for (FirebaseVisionBarcode barcode: Barcodes) {
                            String rawValue = barcode.getRawValue();

                            Intent i=new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(rawValue));
                            Intent choser=Intent.createChooser(i,"Web Browser");
                            startActivity(choser);

                            Toast.makeText(MainActivity.this,rawValue,Toast.LENGTH_SHORT).show();
                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            /*switch (valueType) {
                                case FirebaseVisionBarcode.TYPE_WIFI:
                                    String ssid = barcode.getWifi().getSsid();
                                    String password = barcode.getWifi().getPassword();
                                    int type = barcode.getWifi().getEncryptionType();
                                    break;
                                case FirebaseVisionBarcode.TYPE_URL:
                                    String title = barcode.getUrl().getTitle();
                                    String url = barcode.getUrl().getUrl();
                                    break;
                            }*/
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Failed To Recognize",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void textRecognition(Bitmap photo)
    {
        FirebaseVisionImage image=FirebaseVisionImage.fromBitmap(photo);

        FirebaseVisionTextRecognizer detector= FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        final Task<FirebaseVisionText> result=detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText)
                    {
                        for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()) {
                            String blockText = block.getText();
                            Toast.makeText(MainActivity.this,"Recognized Text Is : "+blockText,Toast.LENGTH_SHORT).show();

                            for (FirebaseVisionText.Line line: block.getLines()) {
                                String lineText = line.getText();
                               // Toast.makeText(MainActivity.this,"Lines Are : "+lineText,Toast.LENGTH_SHORT).show();
                                for (FirebaseVisionText.Element element: line.getElements()) {
                                    String elementText = element.getText();
                                   // Toast.makeText(MainActivity.this,"Elements are : "+elementText,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(MainActivity.this,"Failed to Recognize Image",Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
