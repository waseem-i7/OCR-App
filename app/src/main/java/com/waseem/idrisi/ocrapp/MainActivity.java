package com.waseem.idrisi.ocrapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.waseem.idrisi.ocrapp.databinding.ActivityMainBinding;

import java.io.File;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Uri uriPdf = null;
    private File filePdf = null;
    private String pdfFileName = "";

    private final ActivityResultLauncher<Intent> getContent =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        uriPdf = data.getData();
                        String path = getRealPathFromURI(uriPdf);
                        extractTextPdfFile(path);
                        filePdf = new File(path);
                        pdfFileName = path.trim().substring(path.lastIndexOf("/") + 1);
                    }
                }
            });

    private String getRealPathFromURI(Uri uri) {
        String filePath = null;
        Cursor cursor = null;
        try {
            if (uri.toString().startsWith("content://com.android.providers.downloads.documents/document/raw")) {
                String[] projection = {MediaStore.Files.FileColumns.DATA};
                cursor = getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();
                }
            } else {
                String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                Cursor cursor1 = getContentResolver().query(uri, projection, null, null, null);

                String fileName = null;
                if (cursor1 != null && cursor1.moveToFirst()) {
                    int nameIndex = cursor1.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                    fileName = cursor1.getString(nameIndex);
                    cursor1.close();
                    String downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    filePath = downloadsDirectory + "/" + fileName;
                }
            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return filePath;
    }


    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> permissions) {
                    boolean granted = true;
                    for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                        if (!entry.getValue()) {
                            granted = false;
                            break;
                        }
                    }

                    if (granted) {
                        // If the user grants the permissions, proceed with your code

                    } else {
                        // If the user denies the permissions, handle the error gracefully
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/pdf");
                    getContent.launch(intent);
                } else {
                    requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
            }
        });
        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //binding.recgText.setText("");
            }
        });
        binding.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String text = binding.recgText.getText().toString();
//                if (text.isEmpty()) {
//                    Toast.makeText(MainActivity.this, "There is no text to copy", Toast.LENGTH_SHORT).show();
//                } else {
//                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(MainActivity.this.CLIPBOARD_SERVICE);
//                    ClipData clipData = ClipData.newPlainText("Data", binding.recgText.getText().toString());
//                    clipboardManager.setPrimaryClip(clipData);
//
//                    Toast.makeText(MainActivity.this, "Text copy to Clipboard", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }


    private void extractTextPdfFile(String path) {
        try {
            // creating a string for
            // storing our extracted text.
            String extractedText = "";

            // creating a variable for pdf reader
            // and passing our PDF file in it.
            PdfReader reader = new PdfReader(path);

            // below line is for getting number
            // of pages of PDF file.
            int n = reader.getNumberOfPages();

            // running a for loop to get the data from PDF
            // we are storing that data inside our string.
            for (int i = 0; i < n; i++) {
                extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
                // to extract the PDF content from the different pages
            }

            // after extracting all the data we are
            // setting that string value to our text view.
            binding.recgText.setText(AITask.fetchInsurerName(extractedText));

            // below line is used for closing reader.
            reader.close();
        } catch (Exception e) {
            // for handling error while extracting the text file.
            binding.recgText.setText("Error found is : \n" + e);
        }
    }


}


//
//package com.waseem.idrisi.ocrapp;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.databinding.DataBindingUtil;
//
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//import com.github.drjacky.imagepicker.ImagePicker;
//import com.github.drjacky.imagepicker.constant.ImageProvider;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.mlkit.vision.common.InputImage;
//import com.google.mlkit.vision.text.Text;
//import com.google.mlkit.vision.text.TextRecognition;
//import com.google.mlkit.vision.text.TextRecognizer;
//import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
//import com.itextpdf.text.pdf.PdfReader;
//import com.itextpdf.text.pdf.parser.PdfTextExtractor;
//import com.waseem.idrisi.ocrapp.databinding.ActivityMainBinding;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//import kotlin.Unit;
//import kotlin.jvm.functions.Function1;
//import kotlin.jvm.internal.Intrinsics;
//
//public class MainActivity extends AppCompatActivity {
//
//    private ActivityMainBinding binding;
//    Uri imageUri;
//    TextRecognizer textRecognizer;
//
//    ActivityResultLauncher<Intent> launcher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
//                if (result.getResultCode() == RESULT_OK) {
//                    if (result.getData() != null) {
//                        imageUri = result.getData().getData();
//                        recognizeText();
//                        Toast.makeText(this, "image selected", Toast.LENGTH_SHORT).show();
//                    }
//                    // Use the uri to load the image
//                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
//                    // Use ImagePicker.Companion.getError(result.getData()) to show an error
//                    Toast.makeText(this, "image not selected", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        init();
//        binding.getImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImagePicker.Companion.with(MainActivity.this)
//                        .crop()
//                        .maxResultSize(1080, 1080, true)
//                        .provider(ImageProvider.BOTH) //Or bothCameraGallery()
//                        .createIntentFromDialog((Function1) (new Function1() {
//                            public Object invoke(Object var1) {
//                                this.invoke((Intent) var1);
//                                return Unit.INSTANCE;
//                            }
//
//                            public final void invoke(@NotNull Intent it) {
//                                Intrinsics.checkNotNullParameter(it, "it");
//                                launcher.launch(it);
//                            }
//                        }));
//            }
//        });
//        binding.clear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                binding.recgText.setText("");
//            }
//        });
//        binding.copy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String text = binding.recgText.getText().toString();
//                if (text.isEmpty()){
//                    Toast.makeText(MainActivity.this, "There is no text to copy", Toast.LENGTH_SHORT).show();
//                }else {
//                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(MainActivity.this.CLIPBOARD_SERVICE);
//                    ClipData clipData = ClipData.newPlainText("Data",binding.recgText.getText().toString());
//                    clipboardManager.setPrimaryClip(clipData);
//
//                    Toast.makeText(MainActivity.this,"Text copy to Clipboard",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    void init() {
//        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//    }
//
//    private void recognizeText() {
//        if (imageUri != null) {
//            try {
//                InputImage inputImage = InputImage.fromFilePath(MainActivity.this, imageUri);
//
//                Task<Text> result = textRecognizer.process(inputImage).
//                        addOnSuccessListener(new OnSuccessListener<Text>() {
//                            @Override
//                            public void onSuccess(Text text) {
//                                String recognizeText = text.getText();
//                                binding.recgText.setText(AITask.fetchPolicyNo1(recognizeText));
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//
//}



