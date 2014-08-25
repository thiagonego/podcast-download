
package br.com.twsoftware.poddown.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import br.com.twsoftware.alfred.AlfredException;
import br.com.twsoftware.alfred.data.Data;
import br.com.twsoftware.alfred.object.Objeto;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.itunes.FeedInformation;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@SuppressWarnings("all")
public class Util{

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

     public static PodCast readFeed(InputStream inputStream, PodCast pod) throws IllegalArgumentException, FeedException, IOException {

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
