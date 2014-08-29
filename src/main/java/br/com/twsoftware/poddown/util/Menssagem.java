
package br.com.twsoftware.poddown.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menssagem{

     public int tipo;

     public String msg;

     public static final int ERRO = 0;

     public static final int SUCESSO = 1;

     public static final int INFO = 2;

     public static final int WARNING = 3;
     
}
