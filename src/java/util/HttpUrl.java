/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import Postes.Posts;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gruposantoro1
 */
public class HttpUrl {

    private JSONObject jsono = null;
    String idValor = "";
    public static String IP_LOGIN = "http://api-twitter.us-east-1.elasticbeanstalk.com/mexa/insertar";
    
    public HttpUrl(JSONObject jsono) {

        this.jsono = jsono;

    }

    public String getData() {

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    idValor =""+ urlConnections(jsono);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        };
        Thread t = new Thread(myRunnable);
        t.start();

        return idValor;
    }

    public String urlConnections(JSONObject jsono1) throws JSONException {
         int respuesta =0;
        String regresoid = "";
        HttpURLConnection conn = null;
        JSONObject jason = this.jsono;
        URL url = null;
        try {
            url = new URL(IP_LOGIN);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(45000);
            conn.setReadTimeout(45000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(jason.toString().getBytes("UTF-8"));
            os.close();
             respuesta = conn.getResponseCode();
            if (respuesta == HttpURLConnection.HTTP_OK) {

                InputStream in = new BufferedInputStream(conn.getInputStream());
                JSONObject objetoJSON = new JSONObject(inputStreamToString(in));
                if (objetoJSON.has("Mensaje")) {
                    return objetoJSON.toString();
                    //   System.out.println(objetoJSON.getString("Mensaje"));
                } else if (objetoJSON.has("Error")) {
                    return objetoJSON.toString();
                    //      System.out.println(objetoJSON.getString("Error"));
                } else {
                    regresoid = objetoJSON.getString("id");
                }
              //  datoSend("Servidor " + respuesta);
                in.close();

            } else {
                regresoid = "error URLCONECTION " + respuesta;
                // error(regresoid);
            }
            conn.disconnect();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return ""+respuesta;
    }

    public String inputStreamToString(InputStream is) throws IOException {
        String line = "";
        String repuesta = "";
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
            rd.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        repuesta = total.toString();
        System.out.println("inputStreamToString HTTP "+repuesta);
        datoSend("Ap " + repuesta);
        return repuesta;
    }

    public void datoSend(String mensajes) throws IOException {
       String ruta = new Posts().dir+"SendHTTP.txt";
     
        File archivo = new File(ruta);
        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        if (archivo.exists()) {

            String cadena="";
            FileReader f = new FileReader(ruta);
            BufferedReader b = new BufferedReader(f);
            while ((cadena = b.readLine()) != null) {
             //   System.out.println(cadena);
            }
            b.close();
            bw.write(mensajes);
        } else {
            bw.write(mensajes);
        }
        bw.close();
    }

}
