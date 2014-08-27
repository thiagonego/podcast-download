
package br.com.twsoftware.poddown.util;

import java.io.IOException;
import java.text.DecimalFormat;

import lombok.extern.java.Log;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;

@Log
public class FileUploadProgressListener implements MediaHttpUploaderProgressListener{

     private String mFileUploadedName;

     public FileUploadProgressListener(String fileName){

          mFileUploadedName = fileName;
     }

     @SuppressWarnings("incomplete-switch")
     @Override
     public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {

          String r = null;
          DecimalFormat decimal = new DecimalFormat( "0.00" );  

          if (mediaHttpUploader == null)
               return;
          switch (mediaHttpUploader.getUploadState()) {

               case INITIATION_STARTED:
                    r = "Starting upload of " + mFileUploadedName;
                    break;
               case INITIATION_COMPLETE:
                    r = "Upload starded of " + mFileUploadedName;
                    break;
               case MEDIA_IN_PROGRESS:
                    double percent = mediaHttpUploader.getProgress() * 100;
                    r = "Uploading.. " + mFileUploadedName + " - " + new Double(percent).intValue() + "%";
                    break;
               case MEDIA_COMPLETE:
                    log.info("Upload is complete!");
                    DownloaderFacade.getInstance().setRunning(false);

          }

          DownloaderFacade.getInstance().setProgressMsg(r);
          log.info(r);
     }
     
     
     public static void main(String[] args) { 

          double d = 02.123123123;
          DecimalFormat decimal = new DecimalFormat( "0.00" );  
          System.out.println(decimal.format(d));
     }
}