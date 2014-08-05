package com.tocolado.site.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Classe utilitaria para arquivos de properties.
 * 
 * @author marcelo.borba
 */
public class PropertiesUtil {

     /**
      * Obtem um arquivo de properties a partir do caminho especificado.
      * 
      * @param pathFile
      * caminho do arquivo.
      * @return arquivo de properties.
      */
     public Properties obterProperties(String pathFile) {

          InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(pathFile);
          Properties props = new Properties();

          try {

               props.load(inputStream);

          } catch (FileNotFoundException ex) {
               ex.printStackTrace();

          } catch (IOException ex) {
               ex.printStackTrace();
          }

          return props;
     }
     
     public String getKey(){          
//          return this.getClass().getClassLoader().getResource("META-INF/api-google.pem").getFile();
          return this.getClass().getClassLoader().getResource("META-INF/key").getFile();
     }
     
}
