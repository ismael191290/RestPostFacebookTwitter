package Postes;

import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import com.restfb.types.Page;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import sun.misc.IOUtils;
import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author GS-Server
 */
public class Posts {

 // public String dir = "C:\\Users\\GS-Server\\Desktop\\Proyectos Integra\\";
    public String dir = "C:\\Users\\root\\Desktop\\MEXA\\";

    public boolean setPostFace(String imagen, String casilla,  String presidente, String info, String info2) throws IOException {
        boolean resul = false;
        String tokenAl = "EAAdMhTWnPt8BAEkOzTKMQwRKXhRe8w9h8alCJ5iFPviZC80K6HOON83zcfsP3i07JwGDrbUPTwrNNZCxCMguUhXQ9fC4cqNvK2aqsYFI9fGasxVsAYy7OYqPY0PqpYZAav0fWSDuQZAoZCzBGJDyannXcbyfJlsmT0TkchxNQtwZDZD";
        Page page = null;
        try {
            FacebookClient fc = new DefaultFacebookClient(tokenAl);
            try {
                //SomosElDiaD
                page = fc.fetchObject("corroborayd", Page.class);
            } catch (Exception e) {
                System.out.println("Error en token " + e.getMessage());
            }
            File file = new File(""+new Posts().dir+imagen);
            FileInputStream fi = new FileInputStream(file);
            FacebookType resType = fc.publish(page.getId() + "/photos", FacebookType.class, BinaryAttachment.with(""+imagen, fi), Parameter.with("message", "  casilla: "+casilla+" "+presidente+" "+info+" "+info2));
          
            resul = true;
        } catch (FileNotFoundException ex) {
            System.out.println("error " + ex.getMessage());
        }
        return resul;
    }

    public boolean setPostTwitter(String imagen, String casilla, String presidente, String info, String info2) {
        boolean resul = false;
        try {
            // cambiar twitters
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true).setOAuthConsumerKey("oXLrNziH7OxqPMsBClZ8pKpbp")
                    .setOAuthConsumerSecret("p83avI3B3Km6z0oZhmkpidQUqS3Ru89riuJsUnGZ1qw5qnYjaT")
                    .setOAuthAccessToken("1009193627279609856-1zS20dD0bGNBLa8g63YcoAfV7U1P2d")
                    .setOAuthAccessTokenSecret("0b4uWaUR6o9Fu5TaGSz1fqBtKgRQXr6cfuvzTqijAlDZM");

            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter t = tf.getInstance();
            File file = new File(""+new Posts().dir+imagen);
            StatusUpdate status = new StatusUpdate("#CorroborayDenuncia  casilla: "+casilla+" "+presidente+" "+info+" "+info2);

           //  GeoLocation gl = new GeoLocation(latitud, longitud);
            //  status.setLocation(gl);
            status.setMedia(file); // set the image to be uploaded here.
            t.updateStatus(status);
            System.out.println("Successfully");
            resul = true;
        } catch (TwitterException ex) {
            ex.printStackTrace();
        }
        return resul;
    }

   
    
}
