/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services.service;

import Postes.Posts;
import Postes.Proceso;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author GS-Server
 */
@Provider
@Path("/")
public class PostFace implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST");
    }

    @POST
    @Path("/imagePost")
    @Consumes(MediaType.APPLICATION_JSON)
    public String post(String cadena) {
        Proceso posl = new Proceso();
        JSONObject json = null;
        try {
            json = new JSONObject();
            if (posl.printPila(cadena)) {
                json.put("Mensaje", "El documento se almaceno exitosamente");
            } else {
                json.put("Mensaje", "Error en proceso...");
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return json.toString();
    }

    @POST
    @Path("/corrobora")
    @Consumes(MediaType.APPLICATION_JSON)
    public String corrobora(String cadena) throws IOException {
        JSONObject json = null;
        try {
            json = new JSONObject();
            if (new Proceso().foto(cadena)) {
                json.put("Mensaje", "El documento se almaceno exitosamente");
            } else {
                json.put("Mensaje", "Error en proceso...");
            }
            datoSend(json.toString());
        } catch (JSONException ex) {
            datoSend("Error: " + ex.getMessage());
        } catch (IOException ex) {
            datoSend("Errors: " + ex.getMessage());
        }
        return json.toString();
    }

    public void datoSend(String mensajes) throws IOException {
        String ruta = new Posts().dir + "SendFoto.txt";

        File archivo = new File(ruta);
        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        if (archivo.exists()) {

            String cadena = "";
            FileReader f = new FileReader(ruta);
            BufferedReader b = new BufferedReader(f);
            while ((cadena = b.readLine()) != null) {

            }
            b.close();
            bw.write(mensajes);
        } else {
            bw.write(mensajes);
        }
        bw.close();
    }

    @POST
    @Path("/JSONS")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String PuebaJSON(InputStream inputStream) throws JSONException {
        StringBuilder cruBuilder = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
         JSONObject on = null;
        try {
            while ((line = in.readLine()) != null) {
                cruBuilder.append(line);
            }
            on = new JSONObject(cruBuilder.toString());
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
           ex.printStackTrace();
        }
        return on.toString();
    }

}
