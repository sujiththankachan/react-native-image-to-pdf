package com.anyline.RNImageToPDF;

/**
 * Created by jonas on 23.08.17.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class RNImageToPdf extends ReactContextBaseJavaModule {

    public static final String REACT_CLASS = "RNImageToPdf";

    private final ReactApplicationContext reactContext;


    RNImageToPdf(ReactApplicationContext context) {
        super(context);
        this.reactContext = context;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void createPDFbyImages(ReadableMap imageObject, final Promise promise) {

        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(585, 842, 1).create(); // A4 in PS points

        ReadableArray images = imageObject.getArray("imagePaths");

        for (int idx = 0; idx < images.size(); idx++) {
            // start a page
            PdfDocument.Page page = document.startPage(pageInfo);

            // get image from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bmp = BitmapFactory.decodeFile(images.getString(idx), options);

            // add image to page
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(bmp, 0, 0, null);

            document.finishPage(page);
        }

        // write the document content
        File targetPath = reactContext.getExternalFilesDir(null);
        File filePath = new File(targetPath, "file.pdf");
        try {
            document.writeTo(new FileOutputStream(filePath));
            WritableMap resultMap = Arguments.createMap();
            resultMap.putString("filePath", filePath.getAbsolutePath());
            promise.resolve(resultMap);
        } catch (IOException e) {
            promise.reject("failed.to.write.file", e);
        }

        // close the document
        document.close();
    }

}
  