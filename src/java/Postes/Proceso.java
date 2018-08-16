/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Postes;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;
import util.HttpUrl;

/**
 *
 * @author GS-Server
 */
public class Proceso {

    private boolean result = false;
    private JSONObject getJson;
    String nombreImage = "";
    String tok = "";

    public boolean foto(String datos) throws IOException {
        try {
            JSONObject send = new JSONObject();
            getJson = new JSONObject(datos);
            send.put("usuario", "" + getJson.getString("usuario"));
            send.put("latitud", getJson.getDouble("latitud"));
            send.put("longitud", getJson.getDouble("longitud"));
            send.put("fecha", "" + getJson.getString("fecha"));

            if (getJson.has("estado")) {
                send.put("estado", "" + getJson.getString("estado"));
            } else {
                send.put("estado", "MOrelos");
            }

            if (getJson.has("municipio")) {
                send.put("municipio", "" + getJson.getString("municipio"));
            } else {
                send.put("municipio", "Cuernavaca");
            }
            if (getJson.has("colonia")) {
                send.put("colonia", "" + getJson.getString("colonia"));
            } else {
                send.put("colonia", "Palmas");
            }
            if (getJson.has("cp")) {
                send.put("cp", "" + getJson.getString("cp"));
            } else {
                send.put("cp", "62760");
            }

            if (getJson.has("direccion")) {
                send.put("direccion", "" + getJson.getString("direccion"));
            } else {
                send.put("direccion", "nose");
            }
            send.put("descripcion", "" + getJson.getString("descripcion"));
            send.put("nos", "" + getJson.getString("nos"));

            String type = Normalizer.normalize(getJson.getString("tipo"), Normalizer.Form.NFD);//lo agregue
            type = type.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");//lo agregue
            send.put("tipo", type);

            if (getJson.has("nombre")) {
                send.put("nombre", "" + getJson.getString("nombre"));
            }

            if (getJson.has("email")) {
                send.put("email", "" + getJson.getString("email"));
            }

            if (getJson.has("tel")) {
                send.put("tel", "" + getJson.getString("tel"));
            }

            if (getJson.has("foto")) {
                send.put("foto", getJson.getString("foto"));
                // imagen(getJson.getString("foto").toString());
            }

            HttpUrl http = new HttpUrl(send);
            http.getData();
            result = true;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public void imagen(String cadena) throws IOException {

        try {
            String OrigenCarpeta = "" + new Posts().dir;
            BufferedImage image = decodeToImage(cadena);
            java.util.Date fecha = new Date();
            String fechas = fecha.toString();

            String aux = fechas.substring(10, 19);
            String aux2 = aux.replace(":", "-");
            nombreImage = "-" + fecha.getTime() + ".jpg";
            File outputfile = null;
            outputfile = new File(OrigenCarpeta + "-" + fecha.getTime() + ".jpg");
            ImageIO.write(image, "png", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public boolean printPila(String json) {
        JSONObject getSon;
        boolean rtesult = false;
        String casilla = "";
        String presi = "";
        String desc = "";
        String desc2 = "";
        try {
            getSon = new JSONObject(json);

            if (getSon.has("foto")) {
                imagen(getSon.getString("foto").toString());
            }

            if (getSon.has("casilla")) {
                casilla = ""+getSon.getString("casilla");
            }

            if (getSon.has("presi")) {
                presi = ""+getSon.getString("presi");
            }

            if (getSon.has("desc")) {
                desc = ""+getSon.getString("desc");
            }

            if (getSon.has("desc2")) {
                desc2 = ""+getSon.getString("desc2");
            }

            if (new Posts().setPostFace(nombreImage, casilla, presi, desc, desc2)) {

                if (new Posts().setPostTwitter(nombreImage, casilla, presi, desc, desc2)) {
                    rtesult = true;
                } else {
                    System.out.println(" twwiert mal ");
                }
            } else {
                System.out.println("face mal ");
            }
        } catch (JSONException ex) {
            System.out.println("Erro en validacion PIla " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Erro en Imagen " + ex.getMessage());
        }
        return rtesult;
    }

}
