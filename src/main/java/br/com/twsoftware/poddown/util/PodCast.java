
package br.com.twsoftware.poddown.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import lombok.Data;
import br.com.twsoftware.alfred.object.Objeto;

@Data
public class PodCast{

     public String feed;

     public String emails;

     public String ultimo;

     public Integer row;

     public List<Episodeo> episodeos;

     public PodCast(){

          episodeos = new ArrayList<Episodeo>();
     }

     public boolean baixar() throws ParseException {

          if (Objeto.notBlank(getEpisodeos())) {

               Collections.sort(getEpisodeos(), new Comparator<Episodeo>(){

                    public int compare(Episodeo e1, Episodeo e2) {

                         return e1.getDataPublicacao().compareTo(e2.getDataPublicacao());
                    }
               });

               Collections.reverse(getEpisodeos());
               System.out.println(getEpisodeos().get(0));

               Date u = br.com.twsoftware.alfred.data.Data.getData(getUltimo(), "dd/MM/yyyy");
               u = br.com.twsoftware.alfred.data.Data.getData(u, 00, 00, 00, 00);
               if (getEpisodeos().get(0).getDataPublicacao().after(u)) {
                    return true;
               }

          }

          return false;
     }

}
