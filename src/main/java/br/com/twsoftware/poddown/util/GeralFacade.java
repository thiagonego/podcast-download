
package br.com.twsoftware.poddown.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.Getter;
import lombok.Setter;
import br.com.twsoftware.alfred.object.Objeto;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;

@SuppressWarnings("all")
public class GeralFacade{

     @Setter
     @Getter
     private boolean running;

     @Getter
     @Setter
     private String progressMsg;

     public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

     public static final JsonFactory JSON_FACTORY = new JacksonFactory();

     public static final String APP_NAME = "nesupodcast";

     public static final String FROM = "thiagonego@gmail.com";

     public static final String SUBJECT = "Novo podcast adicionado";

     private static GeralFacade instance;

     private GeralFacade(){

          this.running = false;
     }

     public static GeralFacade getInstance() {

          if (instance == null) {
               instance = new GeralFacade();
          }
          return instance;
     }

     public boolean enviarEmail(PodCast pod, String from, String subject, String bodyText, String to, final String username, final String password) {

          try {
               
               if(Objeto.isBlank(username) || Objeto.isBlank(password)){
                    return false;
               }

               Properties props = new Properties();
               props.put("mail.smtp.host", "smtp.gmail.com");
               props.put("mail.smtp.socketFactory.port", "465");
               props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
               props.put("mail.smtp.auth", "true");
               props.put("mail.smtp.port", "465");

               Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication() {
                         return new PasswordAuthentication(username, password);
                    }
               });

               /** Ativa Debug para sessão */
               session.setDebug(true);

               if (Objeto.isBlank(from)) {
                    from = FROM;
               }

               Message message = new MimeMessage(session);
               message.setFrom(new InternetAddress(from)); // Remetente

               Address[] toUser = InternetAddress.parse(to);
               message.setRecipients(Message.RecipientType.TO, toUser);
               message.setSubject(SUBJECT);// Assunto

               StringBuffer sb = new StringBuffer();
               sb.append("Novo episódeo do podcast " + pod.getNome() + " foi adicionado. \n");
               sb.append("Para baixar acesse: " + pod.getLink() + "\n\n");
               message.setText(sb.toString());

               Transport.send(message);

               System.out.println("Feito!!!");
               return true;

          } catch (MessagingException e) {
               e.printStackTrace();
          }

          return false;
     }

     // public boolean enviarEmail(GoogleCredential credential, PodCast pod, String from, String subject, String bodyText, List<String> tos) throws MessagingException, IOException {
     //
     // try {
     //
     // if (Objeto.isBlank(tos)) {
     // tos.add("thiagonego@gmail.com");
     // tos.add("thiago.sampaio@neus.com.br");
     // }
     //
     // if (Objeto.isBlank(bodyText)) {
     // StringBuffer sb = new StringBuffer();
     // sb.append("Novo episódeo do podcast " + pod.getNome() + " foi adicionado. \n");
     // sb.append("Para baixar acesse: " + pod.getLink() + "\n\n");
     // bodyText = sb.toString();
     // }
     //
     // Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APP_NAME).build();
     // MimeMessage email = createEmail(from, subject, bodyText, tos);
     // sendMessage(service, "me", email);
     // return true;
     //
     // } catch (Exception e) {
     // e.printStackTrace();
     // }
     //
     // return false;
     // }
     //
     // /**
     // * Create a MimeMessage using the parameters provided.
     // *
     // * @param to
     // * Email address of the receiver.
     // * @param from
     // * Email address of the sender, the mailbox account.
     // * @param subject
     // * Subject of the email.
     // * @param bodyText
     // * Body text of the email.
     // * @return MimeMessage to be used to send email.
     // * @throws MessagingException
     // */
     // private static MimeMessage createEmail(String from, String subject, String bodyText, List<String> tos) throws MessagingException {
     //
     // Properties props = new Properties();
     // Session session = Session.getDefaultInstance(props, null);
     //
     // MimeMessage email = new MimeMessage(session);
     //
     // for (String to : tos) {
     // InternetAddress tAddress = new InternetAddress(to);
     // email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
     // }
     //
     // InternetAddress fAddress = new InternetAddress(from);
     // email.setFrom(new InternetAddress(from));
     // email.setSubject(subject);
     // email.setText(bodyText);
     // return email;
     // }
     //
     // /**
     // * Create a Message from an email
     // *
     // * @param email
     // * Email to be set to raw of message
     // * @return Message containing base64 encoded email.
     // * @throws IOException
     // * @throws MessagingException
     // */
     // private static Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
     //
     // ByteArrayOutputStream bytes = new ByteArrayOutputStream();
     // email.writeTo(bytes);
     // String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
     // Message message = new Message();
     // message.setRaw(encodedEmail);
     //
     // return message;
     // }
     //
     // /**
     // * Send an email from the user's mailbox to its recipient.
     // *
     // * @param service
     // * Authorized Gmail API instance.
     // * @param userId
     // * User's email address. The special value "me" can be used to indicate the authenticated user.
     // * @param email
     // * Email to be sent.
     // * @throws MessagingException
     // * @throws IOException
     // */
     // private static void sendMessage(Gmail service, String userId, MimeMessage email) throws MessagingException, IOException {
     //
     // Message message = createMessageWithEmail(email);
     // message = service.users().messages().send(userId, message).execute();
     //
     // System.out.println("Message id: " + message.getId());
     // System.out.println(message.toPrettyString());
     // }

}
