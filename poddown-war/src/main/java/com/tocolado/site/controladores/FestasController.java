
package com.tocolado.site.controladores;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.twsoftware.alfred.AlfredException;
import br.com.twsoftware.alfred.data.Data;
import br.com.twsoftware.alfred.io.Arquivo;
import br.com.twsoftware.alfred.io.TipoDeArquivo;
import br.com.twsoftware.alfred.object.Objeto;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.itunes.FeedInformation;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.tocolado.site.util.Episodeo;
import com.tocolado.site.util.PodCast;

@Controller
@RequestMapping("/")
public class FestasController{

     @Getter
     @Setter
     public List<PodCast> podCasts;

     @Getter
     @Setter
     public SpreadsheetService spreadsheetService;

     @Getter
     @Setter
     public SpreadsheetEntry spreadsheet;

     @Getter
     @Setter
     public ConcurrentHashMap<Integer, PodCast> map;

//     public static File UPLOADd_FILE = new File("/home/thiago/Desenvolvimento/workspace_gosfest_mvn/gofest-site/src/main/java/com/tocolado/site/util/Episodeo.java");

     public static String FOLDER_ID = "0B4owD4in8_X7a3pXcGtFcDR1Z0E";

     public FestasController(){

          init();
     }

     @RequestMapping(value = "", method = RequestMethod.GET)
     public String index(ModelMap model) {

          try {

               if (Objeto.isBlank(podCasts)) {

                    init();

                    GoogleCredential credential = getCredential();
                    spreadsheetService = new SpreadsheetService("podcaster_downloader");
                    spreadsheetService.setOAuth2Credentials(credential);

                    URL url = new URL("http://spreadsheets.google.com/feeds/spreadsheets/private/full/" + "1bEA0pWRZ8maPk_jTkrxU3aV8sWkampZpEnxH64zjXdE");
                    spreadsheet = spreadsheetService.getEntry(url, SpreadsheetEntry.class);

                    for (WorksheetEntry ws : spreadsheet.getWorksheets()) {

                         CellFeed feed = spreadsheetService.getFeed(ws.getCellFeedUrl(), CellFeed.class);

                         for (CellEntry entry : feed.getEntries()) {
                              if (entry.getCell().getRow() != 1) {

                                   PodCast pod = new PodCast();
                                   pod.setRow(entry.getCell().getRow());

                                   switch (entry.getCell().getCol()) {
                                        case 1:

                                             if (Objeto.notBlank(map.get(entry.getCell().getRow()))) {
                                                  map.get(entry.getCell().getRow()).setFeed(entry.getCell().getValue());
                                             } else {
                                                  pod.setFeed(entry.getCell().getValue());
                                                  map.put(entry.getCell().getRow(), pod);
                                             }
                                             break;

                                        case 2:

                                             if (Objeto.notBlank(map.get(entry.getCell().getRow()))) {
                                                  map.get(entry.getCell().getRow()).setEmails(entry.getCell().getValue());
                                             } else {
                                                  pod.setEmails(entry.getCell().getValue());
                                                  map.put(entry.getCell().getRow(), pod);
                                             }

                                             break;

                                        default:

                                             if (Objeto.notBlank(map.get(entry.getCell().getRow()))) {
                                                  map.get(entry.getCell().getRow()).setUltimo(entry.getCell().getValue());
                                             } else {
                                                  pod.setUltimo(entry.getCell().getValue());
                                                  map.put(entry.getCell().getRow(), pod);
                                             }

                                             break;
                                   }

                              }
                         }
                    }

                    if (Objeto.notBlank(map)) {
                         podCasts = new ArrayList(map.values());
                    }

               }

               model.addAttribute("podCasts", podCasts);

          } catch (Exception e) {
               e.printStackTrace();
               return "erro";
          }

          return "index";

     }

     public void changeUltimo(PodCast pod, Episodeo ep) throws IOException, ServiceException {

          if (Objeto.notBlank(spreadsheet)) {
               
               for (WorksheetEntry ws : spreadsheet.getWorksheets()) {

                    CellFeed feed = spreadsheetService.getFeed(ws.getCellFeedUrl(), CellFeed.class);

                    for (CellEntry entry : feed.getEntries()) {

                         if (entry.getCell().getCol() == 3 && entry.getCell().getRow() == pod.getRow()) {
                                   
                              spreadsheetService.insert(ws.getCellFeedUrl(), new CellEntry(entry.getCell().getRow(), entry.getCell().getCol(), Data.getDataFormatada(ep.getDataPublicacao(), "dd/MM/yyyy")));
                              
                         }
                    }
               }
          }
          
     }

     @RequestMapping(value = "/refresh", method = RequestMethod.GET)
     public String refresh(ModelMap model) {

          init();
          return "redirect:/";
     }

     @RequestMapping(value = "/run/{row}", method = RequestMethod.GET)
     public ResponseEntity<String> run(@PathVariable String row, ModelMap model) {

          if (Objeto.notBlank(map)) {

               PodCast pod = map.get(Integer.parseInt(row));
               if (Objeto.notBlank(pod)) {

                    try {

                         InputStream input = getConteudoArquivo(pod.getFeed());
                         pod = readFeed(input, pod);

                         if (pod.baixar()) {
//                         if (true) {

                              Episodeo ep = pod.getEpisodeos().get(0);
                              InputStream inputEpisodeo = getConteudoArquivo(ep.getUrlEpisodeo());

                              // Fazendo upload para o driver
                              HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
                              JsonFactory JSON_FACTORY = new JacksonFactory();

                              Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, null).setHttpRequestInitializer(getCredential()).build();
                              com.google.api.services.drive.model.File file = insertFile(drive, Arquivo.extraiNomeDoArquivo(ep.getUrlEpisodeo()), ep.getUrlEpisodeo(), FOLDER_ID, "audio/mpeg", inputEpisodeo, pod.getEmails());
                              
                              changeUltimo(pod, ep);
                              
                              return new ResponseEntity<String>("{ 'ultimo' : '" + Data.getDataFormatada(ep.getDataPublicacao(), "dd/MM/yyyy") + "', 'episodio' : '" + file.getDownloadUrl() + "'}", HttpStatus.OK);

                         } else {

                              return new ResponseEntity<String>("{ 'msg' : 'Desatualizado' }", HttpStatus.NO_CONTENT);
                         }

                    } catch (Exception e) {
                         e.printStackTrace();
                    }

               } else {

                    return new ResponseEntity<String>("Podcast não encontrado com o id informado", HttpStatus.NOT_FOUND);
               }

          }

          return new ResponseEntity<String>("Mapa esta em branco. Carregue novamente a página", HttpStatus.BAD_REQUEST);
     }

     public void init() {

          podCasts = new ArrayList<PodCast>();
          map = new ConcurrentHashMap<>();

     }

     public GoogleCredential getCredential() throws IOException, GeneralSecurityException {

          HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
          JsonFactory JSON_FACTORY = new JacksonFactory();

          URL url = getClass().getClassLoader().getResource("META-INF/api-google-nova.p12");
          File key = new File(url.getFile());

          GoogleCredential credential = new GoogleCredential.Builder()
                                        .setTransport(HTTP_TRANSPORT)
                                        .setJsonFactory(JSON_FACTORY)
//                                        .setServiceAccountId("911071503471-a50bf6uo8hsoq3ukt76s6oq27t6t3uj6@developer.gserviceaccount.com")
                                        .setServiceAccountId("246319659699-v9kgnv5mnd495rkrnkprpq19q9le4p9s@developer.gserviceaccount.com")
                                        .setServiceAccountScopes(Arrays.asList("https://spreadsheets.google.com/feeds/", "http://spreadsheets.google.com/feeds", "https://docs.google.com/feeds/", "https://www.googleapis.com/auth/drive"))
                                        .setServiceAccountPrivateKeyFromP12File(key).build();
          credential.refreshToken();
          System.out.println(credential.getAccessToken());
          return credential;

     }

     //
     //
     // /**
     // * Build and returns a Drive service object authorized with the service accounts.
     // * @param httpTransport
     // * @param jsonFactory
     // *
     // * @return Drive service object that is ready to make requests.
     // */
     // public Drive getDriveService(GoogleCredential credential, HttpTransport httpTransport, JsonFactory jsonFactory) throws GeneralSecurityException, IOException, URISyntaxException {
     // Drive service = new Drive.Builder(httpTransport, jsonFactory, null).setHttpRequestInitializer(credential).build();
     // return service;
     // }
     //
     //
     // public GoogleCredential getGoogleCredential(HttpTransport httpTransport, JsonFactory jsonFactory) throws GeneralSecurityException, IOException{
     //
     // Properties pro = new PropertiesUtil().obterProperties("META-INF/core.properties");
     // File key = new File(new PropertiesUtil().getKey());
     // GoogleCredential credential = new GoogleCredential.Builder()
     // .setTransport(httpTransport)
     // .setJsonFactory(jsonFactory)
     // .setServiceAccountId(pro.getProperty("email"))
     // .setServiceAccountScopes(Arrays.asList(DriveScopes.DRIVE))
     // .setServiceAccountPrivateKeyFromPemFile(key)
     // .build();
     // return credential;
     // }
     //
     // public SpreadsheetService getSpreadSheetService(GoogleCredential credential){
     // SpreadsheetService service = new SpreadsheetService("podcaster_downloader");
     // service.setOAuth2Credentials(credential);
     // return service;
     // }

//     public static void main(String[] args) throws GeneralSecurityException, IOException, ServiceException {
//
//          FestasController f = new FestasController();
//
//          HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
//          JsonFactory JSON_FACTORY = new JacksonFactory();
//
//          File key = new File("/home/thiago/Desenvolvimento/workspace_gosfest_mvn/gofest-site/src/main/resources/META-INF/API Project-258883e6e7b5.p12");
//          GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY).setServiceAccountId("911071503471-a50bf6uo8hsoq3ukt76s6oq27t6t3uj6@developer.gserviceaccount.com").setServiceAccountScopes(Arrays.asList("https://spreadsheets.google.com/feeds/", "http://spreadsheets.google.com/feeds", "https://docs.google.com/feeds/", "https://www.googleapis.com/auth/drive")).setServiceAccountPrivateKeyFromP12File(key).build();
//          credential.refreshToken();
//          System.out.println(credential.getAccessToken());
//
//          Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, null).setHttpRequestInitializer(credential).build();
//
//          insertFile(drive, "upload", "arquivo feito upload no java", FOLDER_ID, TipoDeArquivo.TXT.getTipoDeArquivo(), new FileInputStream(UPLOAD_FILE), "thiago.sampaio@neus.com.br");
//
//          System.out.println(Arquivo.extraiNomeDoArquivo("http://jovemnerd.com.br/podpress_trac/feed/99890/0/nerdcast_424_matematica.mp3"));
//     }

     private static com.google.api.services.drive.model.File insertFile(Drive service, String title, String description, String parentId, String mimeType, InputStream inputStream, String emails) {

          // File's metadata.
          com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
          body.setTitle(title);
          body.setDescription(description);
          body.setMimeType(mimeType);
          body.setShared(true);

          // Set the parent folder.
          if (parentId != null && parentId.length() > 0) {
               body.setParents(Arrays.asList(new ParentReference().setId(parentId)));
          }

          // File's content.
          InputStreamContent mediaContent = new InputStreamContent(mimeType, inputStream);
          try {

               com.google.api.services.drive.model.File file = service.files().insert(body, mediaContent).execute();

               Permission newPermission = new Permission();
               newPermission.setValue(emails);
               newPermission.setEmailAddress(emails);
               newPermission.setType("anyone");
               newPermission.setRole("reader");

               Permission permission = service.permissions().insert(file.getId(), newPermission).execute();

               System.out.println("File ID: " + file.getId());
               System.out.println(permission);

               return file;

          } catch (Exception e) {
               System.out.println("An error occured: " + e);
               return null;
          }
     }

     public static InputStream getConteudoArquivo(String u) {

          try {

               URL url = new URL(u);
               URLConnection conn = url.openConnection();
               conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
               return new DataInputStream(conn.getInputStream());

          } catch (MalformedURLException e) {
               throw new AlfredException(e);
          } catch (IOException e) {
               throw new AlfredException(e);
          }
     }

     public PodCast readFeed(InputStream inputStream, PodCast pod) throws IllegalArgumentException, FeedException, IOException {

          SyndFeedInput input = new SyndFeedInput();
          SyndFeed syndfeed = input.build(new XmlReader(inputStream));

          Module module = syndfeed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
          FeedInformation feedInfo = (FeedInformation) module;

          List<SyndEntryImpl> lista = syndfeed.getEntries();
          for (SyndEntryImpl entry : lista) {

               List<SyndEnclosureImpl> enclosures = (List) entry.getEnclosures();
               if (Objeto.notBlank(enclosures)) {

                    for (SyndEnclosureImpl enclosure : enclosures) {

                         Episodeo ep = new Episodeo();
                         ep.setDataPublicacao(Data.getData(entry.getPublishedDate(), 00, 00, 00, 00));
                         ep.setUrlEpisodeo(enclosure.getUrl());

                         if (Objeto.isBlank(pod.getEpisodeos())) {
                              pod.setEpisodeos(new ArrayList<Episodeo>());
                         }
                         pod.getEpisodeos().add(ep);

                    }
               }

          }

          return pod;
     }

}