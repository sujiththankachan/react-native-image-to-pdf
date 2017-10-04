package com.anyline.RNImageToPDF;

/**
 * Created by jonas on 23.08.17.
 */

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
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
  
          try {
              ReadableArray imagePaths = imageObject.getArray("imagePaths");
              String documentName = imageObject.getString("name");
              Document document = new Document();
              File documentFile = getTempFile(documentName);
              PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(documentFile));
              writer.setCompressionLevel(0);
              document.open();
  
              for (int i = 0; i < imagePaths.size(); i++) {
                  Image img = Image.getInstance(imagePaths.getString(i));
                  document.setPageSize(img);
                  document.newPage();
                  img.setAbsolutePosition(0, 0);
                  document.add(img);
              }
              document.close();
  
              String filePath = documentFile.getPath();
              WritableMap resultMap = Arguments.createMap();
              resultMap.putString("filePath", filePath);
  
              promise.resolve(resultMap);
          } catch (FileNotFoundException e) {
              e.printStackTrace();
              return;
          } catch (IOException | DocumentException e) {
              e.printStackTrace();
              promise.reject(e);
              return;
          } catch (Exception e) {
              e.printStackTrace();
              promise.reject(e);
              return;
          }
  
          System.out.println("Done");
  
  
      }
  
  
      private File getTempFile(String name) throws Exception {
          try {
              File outputDir = getReactApplicationContext().getExternalCacheDir();
              return File.createTempFile(name, ".pdf", outputDir);
  
          } catch (Exception e) {
              throw new Exception(e);
          }
      }
  }
  