
package br.com.twsoftware.poddown.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import br.com.twsoftware.alfred.object.Objeto;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.sun.syndication.io.FeedException;

@SuppressWarnings("all")
public class DownloaderFacade{

     @Setter
     @Getter
     private boolean running;

     @Getter
     @Setter
     private String progressMsg;

     private static DownloaderFacade instance;

     private DownloaderFacade(){

          this.running = false;
     }

     public static DownloaderFacade getInstance() {

          if (instance == null) {
               instance = new DownloaderFacade();
          }
          return instance;
     }

}
