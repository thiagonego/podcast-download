
package com.tocolado.site.controladores;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Properties;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.util.ServiceException;
import com.tocolado.site.util.PropertiesUtil;


@Controller
@RequestMapping("/")
public class FestasController{

     @SuppressWarnings("unchecked")
     @RequestMapping(value = "", method = RequestMethod.GET)
     public String index(ModelMap model) {

          try {

               // ClientConfig config = new DefaultClientConfig();
               // Client client = Client.create(config);
               // WebResource service = client.resource(this.url);
               //
               // ObjectMapper mapper = new ObjectMapper();
               // String result = service.path("festas").accept(MediaType.APPLICATION_JSON).get(String.class);
               // org.codehaus.jackson.type.TypeReference<Collection<Festa>> ref = new org.codehaus.jackson.type.TypeReference<Collection<Festa>>(){
               // };
               // Collection<Festa> festas = ((Collection<Festa>) mapper.readValue(result, ref));

               model.addAttribute("festas", null);

          } catch (Exception e) {
               e.printStackTrace();
               return "erro";
          }

          return "index";

     }


     /**
      * Build and returns a Drive service object authorized with the service accounts.
      * @param httpTransport 
      * @param jsonFactory 
      *
      * @return Drive service object that is ready to make requests.
      */
     public Drive getDriveService(GoogleCredential credential, HttpTransport httpTransport, JsonFactory jsonFactory) throws GeneralSecurityException, IOException, URISyntaxException {
          Drive service = new Drive.Builder(httpTransport, jsonFactory, null).setHttpRequestInitializer(credential).build();
          return service;
     }
     
     
     public GoogleCredential getGoogleCredential(HttpTransport httpTransport, JsonFactory jsonFactory) throws GeneralSecurityException, IOException{
          
          Properties pro = new PropertiesUtil().obterProperties("META-INF/core.properties");
          File key = new File(new PropertiesUtil().getKey());
          GoogleCredential credential = new GoogleCredential.Builder()
                                             .setTransport(httpTransport)
                                             .setJsonFactory(jsonFactory)
                                             .setServiceAccountId(pro.getProperty("email"))
                                             .setServiceAccountScopes(Arrays.asList(DriveScopes.DRIVE))
                                             .setServiceAccountPrivateKeyFromPemFile(key)
                                             .build();
          return credential;
     }
     
     public SpreadsheetService getSpreadSheetService(GoogleCredential credential){
          SpreadsheetService service = new SpreadsheetService("podcaster_downloader");
          service.setOAuth2Credentials(credential);
          return service;
     }
     
     public static void main(String[] args) throws GeneralSecurityException, IOException, URISyntaxException, ServiceException {

          FestasController f = new FestasController();
          
          HttpTransport httpTransport = new NetHttpTransport();
          JacksonFactory jsonFactory = new JacksonFactory();          
          GoogleCredential credential = f.getGoogleCredential(httpTransport, jsonFactory);
          
          SpreadsheetService spreadsheetService = f.getSpreadSheetService(credential);
//          Drive driveService = f.getDriveService(credential, httpTransport, jsonFactory);
          
          String spreadsheetURL = "https://docs.google.com/spreadsheets/d/1bEA0pWRZ8maPk_jTkrxU3aV8sWkampZpEnxH64zjXdE/edit#gid=0";
          SpreadsheetEntry spreadsheet = spreadsheetService.getEntry(new URL(spreadsheetURL), SpreadsheetEntry.class);    
          
          System.out.println(spreadsheet);
     }

     @SuppressWarnings("unchecked")
     @RequestMapping(value = "/f/{key}", method = RequestMethod.GET)
     public String show(@PathVariable String key, ModelMap model) {

          //
          // var result;
          // googleAuth.authenticate(GOOGLE_API_KEYS, function (err, token) {
          //
          // accessToken = token;
          // if(err || !accessToken){
          // res.send(500, "Erro ao autenticar com o google driver api");
          // }
          //
          // Spreadsheet.load({
          // debug: true,
          // spreadsheetName: 'podcaster_downloader',
          // worksheetName: '1',
          // accessToken : {
          // type: 'Bearer',
          // token: accessToken
          // }
          // },function sheetReady(err, spreadsheet) {
          // spreadsheet.receive(function(err, rows, info) {
          // if(err) throw err;
          // console.log(rows);
          // res.render('index', {
          // linhas : rows
          // });
          // });
          // });
          //
          // });

          // Festa festa = null;
          // try {
          //
          // String id = String.valueOf(Festa.decode(key));
          //
          // ClientConfig config = new DefaultClientConfig();
          // Client client = Client.create(config);
          // WebResource service = client.resource(this.url);
          //
          // ObjectMapper mapper = new ObjectMapper();
          // String result = service.path("festas").path(id).accept(MediaType.APPLICATION_JSON).get(String.class);
          // festa = mapper.readValue(result, Festa.class);
          // model.addAttribute("festa", festa);
          //
          // result = service.path("festas").queryParam("offset", "1").queryParam("limit", "5").accept(MediaType.APPLICATION_JSON).get(String.class);
          // org.codehaus.jackson.type.TypeReference<Collection<Festa>> ref = new org.codehaus.jackson.type.TypeReference<Collection<Festa>>(){
          // };
          // Collection<Festa> festas = ((Collection<Festa>) mapper.readValue(result, ref));
          // model.addAttribute("festas", festas);
          //
          // } catch (Exception e) {
          // e.printStackTrace();
          // return "erro";
          // }

          return "show";

     }

}