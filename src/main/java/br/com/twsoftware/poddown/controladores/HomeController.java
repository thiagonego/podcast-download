
package br.com.twsoftware.poddown.controladores;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

import br.com.twsoftware.alfred.data.Data;
import br.com.twsoftware.alfred.io.Arquivo;
import br.com.twsoftware.alfred.object.Objeto;
import br.com.twsoftware.poddown.util.DownloaderFacade;
import br.com.twsoftware.poddown.util.Episodeo;
import br.com.twsoftware.poddown.util.PodCast;
import br.com.twsoftware.poddown.util.Util;

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

@Controller
@RequestMapping("/")
@SuppressWarnings("all")
public class HomeController{

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

     private static String FOLDER_ID = "0B4owD4in8_X7a3pXcGtFcDR1Z0E";

     @Getter
     @Setter
     public PodCast pod;

     public HomeController(){

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

     @RequestMapping(value = "/refresh", method = RequestMethod.GET)
     public String refresh(ModelMap model) {

          init();
          return "redirect:/";
     }

     @RequestMapping(value = "/run/{row}", method = RequestMethod.GET)
     public ResponseEntity<String> run(@PathVariable String row, ModelMap model) {

          if (!DownloaderFacade.getInstance().isRunning()) {

               DownloaderFacade.getInstance().setRunning(true);

               if (Objeto.notBlank(map)) {

                    setPod(map.get(Integer.parseInt(row)));
                    if (Objeto.notBlank(getPod())) {

                         try {

                              InputStream input = Util.getConteudoArquivo(getPod().getFeed());
                              setPod(readFeed(input, getPod()));

                              if (getPod().baixar()) {

                                   final GoogleCredential credential = getCredential();

                                   new Thread(new Runnable(){

                                        @Override
                                        public void run() {

                                             try {

                                                  Episodeo ep = getPod().getEpisodeos().get(0);
                                                  InputStream inputEpisodeo = Util.getConteudoArquivo(ep.getUrlEpisodeo());

                                                  // Fazendo upload para o driver
                                                  HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
                                                  JsonFactory JSON_FACTORY = new JacksonFactory();

                                                  Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, null).setHttpRequestInitializer(credential).build();
                                                  com.google.api.services.drive.model.File file = insertFile(drive, Arquivo.extraiNomeDoArquivo(ep.getUrlEpisodeo()), ep.getUrlEpisodeo(), FOLDER_ID, "audio/mpeg", inputEpisodeo, pod.getEmails());

                                                  changeUltimo(getPod(), ep, spreadsheet, spreadsheetService);

                                                  DownloaderFacade.getInstance().setRunning(false);
                                                  System.out.println("Terminou!");

                                             } catch (Exception e) {
                                                  e.printStackTrace();
                                             }

                                        }

                                   }).start();

                                   return new ResponseEntity<String>("Downloading.... Aguarde!", HttpStatus.OK);

                              } else {

                                   return new ResponseEntity<String>("Ainda não foi publicado nenhum episódeo novo desse podcast", HttpStatus.BAD_REQUEST);
                              }

                         } catch (java.text.ParseException e) {

                              e.printStackTrace();
                              DownloaderFacade.getInstance().setRunning(false);
                              return new ResponseEntity<String>("Ocorreu algum erro ao tentar ler o feed.", HttpStatus.BAD_REQUEST);

                         } catch (Exception e) {

                              e.printStackTrace();
                              DownloaderFacade.getInstance().setRunning(false);
                              return new ResponseEntity<String>("Ocorreu algum erro ao tentar baixar o episódeo", HttpStatus.BAD_REQUEST);

                         }

                    } else {

                         return new ResponseEntity<String>("Podcast não encontrado com o id informado", HttpStatus.NOT_FOUND);
                    }

               } else {

                    return new ResponseEntity<String>("Mapa esta em branco. Carregue novamente a página", HttpStatus.BAD_REQUEST);

               }

          } else {

               return new ResponseEntity<String>("Download em execução. Aguarde!", HttpStatus.OK);
          }

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

          GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY).setServiceAccountId("246319659699-v9kgnv5mnd495rkrnkprpq19q9le4p9s@developer.gserviceaccount.com").setServiceAccountScopes(Arrays.asList("https://spreadsheets.google.com/feeds/", "http://spreadsheets.google.com/feeds", "https://docs.google.com/feeds/", "https://www.googleapis.com/auth/drive")).setServiceAccountPrivateKeyFromP12File(key).build();
          credential.refreshToken();
          System.out.println(credential.getAccessToken());
          return credential;

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

     private com.google.api.services.drive.model.File insertFile(Drive service, String title, String description, String parentId, String mimeType, InputStream inputStream, String emails) throws IOException {

          com.google.api.services.drive.model.File file;
          try {

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

               file = service.files().insert(body, mediaContent).execute();

               Permission newPermission = new Permission();
               newPermission.setValue(emails);
               newPermission.setEmailAddress(emails);
               newPermission.setType("anyone");
               newPermission.setRole("reader");

               Permission permission = service.permissions().insert(file.getId(), newPermission).execute();

               System.out.println("File ID: " + file.getId());
               System.out.println(permission);

          } catch (IOException e) {
               e.printStackTrace();
               DownloaderFacade.getInstance().setRunning(false);
               throw e;
          }

          return file;

     }

     private void changeUltimo(PodCast pod, Episodeo ep, SpreadsheetEntry spreadsheet, SpreadsheetService spreadsheetService) throws IOException, ServiceException {

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

}