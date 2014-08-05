
package com.tocolado.site.controladores;

import java.io.File;
import java.io.IOException;
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
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.tocolado.site.util.PropertiesUtil;


@Controller
@RequestMapping("/")
public class FestasController{
     
     public FestasController(){
          
     }

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
               model.addAttribute("festas", null);

          } catch (Exception e) {
               e.printStackTrace();
               return "erro";
          }

          return "index";

     }
//
//
//     /**
//      * Build and returns a Drive service object authorized with the service accounts.
//      * @param httpTransport 
//      * @param jsonFactory 
//      *
//      * @return Drive service object that is ready to make requests.
//      */
//     public Drive getDriveService(GoogleCredential credential, HttpTransport httpTransport, JsonFactory jsonFactory) throws GeneralSecurityException, IOException, URISyntaxException {
//          Drive service = new Drive.Builder(httpTransport, jsonFactory, null).setHttpRequestInitializer(credential).build();
//          return service;
//     }
//     
//     
//     public GoogleCredential getGoogleCredential(HttpTransport httpTransport, JsonFactory jsonFactory) throws GeneralSecurityException, IOException{
//          
//          Properties pro = new PropertiesUtil().obterProperties("META-INF/core.properties");
//          File key = new File(new PropertiesUtil().getKey());
//          GoogleCredential credential = new GoogleCredential.Builder()
//                                             .setTransport(httpTransport)
//                                             .setJsonFactory(jsonFactory)
//                                             .setServiceAccountId(pro.getProperty("email"))
//                                             .setServiceAccountScopes(Arrays.asList(DriveScopes.DRIVE))
//                                             .setServiceAccountPrivateKeyFromPemFile(key)
//                                             .build();
//          return credential;
//     }
//     
//     public SpreadsheetService getSpreadSheetService(GoogleCredential credential){
//          SpreadsheetService service = new SpreadsheetService("podcaster_downloader");
//          service.setOAuth2Credentials(credential);
//          return service;
//     }
     
     public static void main(String[] args) throws GeneralSecurityException, IOException, ServiceException{

          FestasController f = new FestasController();
          
          Properties pro = new PropertiesUtil().obterProperties("META-INF/core.properties");
          HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
          JsonFactory JSON_FACTORY = new JacksonFactory();         
          
          File key = new File("/home/thiago/Desenvolvimento/workspace_gosfest_mvn/gofest-site/src/main/resources/META-INF/API Project-258883e6e7b5.p12");
          GoogleCredential credential = new GoogleCredential.Builder()
                                             .setTransport(HTTP_TRANSPORT)
                                             .setJsonFactory(JSON_FACTORY)
                                             .setServiceAccountId("911071503471-a50bf6uo8hsoq3ukt76s6oq27t6t3uj6@developer.gserviceaccount.com")
                                             .setServiceAccountScopes(Arrays.asList(
                                                            "https://spreadsheets.google.com/feeds/",
                                                            "http://spreadsheets.google.com/feeds",
                                                            "https://docs.google.com/feeds/",
                                                            "https://www.googleapis.com/auth/drive" 
                                                            ))
                                             .setServiceAccountPrivateKeyFromP12File(key)
                                             .build();          
          credential.refreshToken();
          System.out.println(credential.getAccessToken());
          
          SpreadsheetService spreadsheetService = new SpreadsheetService("podcaster_downloader");
          spreadsheetService.setOAuth2Credentials(credential);        
          
          URL url = new URL("http://spreadsheets.google.com/feeds/spreadsheets/private/full/" + "1bEA0pWRZ8maPk_jTkrxU3aV8sWkampZpEnxH64zjXdE");
          SpreadsheetEntry spreadsheet = spreadsheetService.getEntry(url, SpreadsheetEntry.class);   
          
          
          for(WorksheetEntry ws : spreadsheet.getWorksheets()){
               CellEntry newEntry = new CellEntry(4, 1, "thiago");
               spreadsheetService.insert(ws.getCellFeedUrl(), newEntry);
          }
          
//          Drive driveService = f.getDriveService(credential, httpTransport, jsonFactory);
//          String spreadsheetURL = "https://docs.google.com/spreadsheets/d/1bEA0pWRZ8maPk_jTkrxU3aV8sWkampZpEnxH64zjXdE/edit#gid=0";
//          System.out.println(spreadsheet);
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