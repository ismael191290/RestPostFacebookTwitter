/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServletVideo;

import Postes.Posts;
import Postes.Proceso;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.Base64;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.JSONException;
import org.json.JSONObject;
import util.HttpUrl;

/**
 *
 * @author GS-Server
 */
@WebServlet(name = "ServletVideo", urlPatterns = {"/ServletVideo"})
@MultipartConfig(
      //  location = "C:\\Users\\GS-Server\\Desktop\\Proyectos Integra",
          location = "C:\\Users\\root\\Desktop\\MEXA",
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 180, // 10 MB 
        maxRequestSize = 1024 * 1024 * 9 * 81 // 25 MB
)
public class ServletVideo extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, accept, authorization");
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Max-Age", "1728000");
            response.addHeader("Access-Control-Allow-Methods", "POST, GET");
            JSONObject json = new JSONObject();
            String filename = "";
            // esta variable abuelo
            JSONObject send = new JSONObject();
            try {
                Collection<Part> parts = request.getParts();

                for (Part part : parts) {
                    /*se modifico*/
                    if (getFileName(part) != null) {
                        filename = System.currentTimeMillis() + getFileName(part);
                        part.write(filename);
                    }
                }

                String type = Normalizer.normalize(new String(request.getParameter("tipo")//se sgrego
                        .getBytes("ISO-8859-1"), "UTF-8"), Normalizer.Form.NFD);//se sgrego
                final String evento = type.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");//se sgrego

                send.put("usuario", "" + request.getParameter("usuario"));
                send.put("latitud", "" + request.getParameter("latitud"));
                send.put("longitud", "" + request.getParameter("longitud"));
                send.put("fecha", "" + request.getParameter("fecha"));
                send.put("estado", "" + request.getParameter("estado"));
                send.put("municipio", "" + request.getParameter("municipio"));
                send.put("colonia", "" + request.getParameter("colonia"));
                send.put("cp", "" + request.getParameter("cp"));
                String dirc = sinAcentos("" + request.getParameter("direccion"));
                send.put("direccion", "" + dirc);
                send.put("descripcion", request.getParameter("descripcion"));
                send.put("nos", "" + request.getParameter("nos"));
                send.put("tipo", "" + evento);

                if (request.getParameter("nombre") != null) {
                    send.put("nombre", "" + request.getParameter("nombre"));
                }

                if (request.getParameter("email") != null) {
                    send.put("email", "" + request.getParameter("email"));
                }

                if (request.getParameter("tel") != null) {
                    send.put("tel", "" + request.getParameter("tel"));
                }

                final String filename2 = filename;
                Runnable myRunnable = new Runnable() {

                    @Override
                    public void run() {
                        upLoad2Server("" + filename2, send);
                    }
                };
                Thread t = new Thread(myRunnable);
                t.start();
                json.put("Mensaje", "El documento se almaceno exitosamente");
            } catch (Exception e) {
                json.put("Mensaje", "Error " + e.getMessage());
                datoSend2("" + e.getMessage(), "Errors");
                e.printStackTrace();
            }
            out.print(json);
        }
    }

    private String getFileName(Part part) {
        for (String token : part.getHeader("content-disposition").split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    public static int upLoad2Server(String sourceFileUri, JSONObject json) {

        String fileName = sourceFileUri;
        int serverResponseCode = 0;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String responseFromServer = "";

        File sourceFile = new File(new Posts().dir + sourceFileUri);
        if (!sourceFile.isFile()) {
            return 0;
        }
        try { // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(HttpUrl.IP_LOGIN);
            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Encoding", "");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("video", fileName);
            conn.setRequestProperty("usuario", "" + json.getString("usuario"));
            conn.setRequestProperty("latitud", json.getString("latitud"));
            conn.setRequestProperty("longitud", "" + json.getString("longitud"));
            conn.setRequestProperty("fecha", "" + json.getString("fecha"));
            conn.setRequestProperty("estado", "" + json.getString("estado"));
            conn.setRequestProperty("municipio", "" + json.getString("municipio"));
            conn.setRequestProperty("colonia", "" + json.getString("colonia"));
            conn.setRequestProperty("cp", "" + json.getString("cp"));
            conn.setRequestProperty("direccion", "" + json.getString("direccion"));
            conn.setRequestProperty("descripcion", "" + json.getString("descripcion"));
            conn.setRequestProperty("nos", "" + json.getString("nos"));
            conn.setRequestProperty("tel", "" + json.getString("tel"));
            conn.setRequestProperty("tipo", "" + json.getString("tipo"));

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"usuario\"" + lineEnd + lineEnd + json.getString("usuario") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"latitud\"" + lineEnd + lineEnd + json.getString("latitud") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"longitud\"" + lineEnd + lineEnd + json.getString("longitud") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"fecha\"" + lineEnd + lineEnd + json.getString("fecha") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"estado\"" + lineEnd + lineEnd + json.getString("estado") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"municipio\"" + lineEnd + lineEnd + json.getString("municipio") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"colonia\"" + lineEnd + lineEnd + json.getString("colonia") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"cp\"" + lineEnd + lineEnd + json.getString("cp") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"direccion\"" + lineEnd + lineEnd + json.getString("direccion") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"descripcion\"" + lineEnd + lineEnd + json.getString("descripcion") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"nos\"" + lineEnd + lineEnd + json.getString("nos") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"tel\"" + lineEnd + lineEnd + json.getString("tel") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"tipo\"" + lineEnd + lineEnd + json.getString("tipo") + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"video\";filename=\"" + fileName.getBytes("UTF-8").toString() + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();
            System.out.println("R - " + serverResponseMessage + " - " + serverResponseCode);
            new HttpUrl(null).datoSend("A " + serverResponseCode + " " + serverResponseMessage);
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            System.out.println("Upload file to server error: " + ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            new HttpUrl(null).inputStreamToString(conn.getInputStream());
        } catch (IOException ioex) {
            ioex.printStackTrace();
            System.out.println("error: " + ioex.getMessage());
        }
        conn.disconnect();
        return serverResponseCode;  // like 200 (Ok)

    } // end upLoad2Server

    public void datoSend(String mensajes) throws IOException {
        String ruta = new Posts().dir + "SendVideo.txt";

        File archivo = new File(ruta);
        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        if (archivo.exists()) {

            String cadena = "";
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

    public void datoSend2(String mensajes, String nombre) throws IOException {
        String ruta = new Posts().dir + "" + nombre + ".txt";

        File archivo = new File(ruta);
        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        if (archivo.exists()) {

            String cadena = "";
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

    public String sinAcentos(String cadena) {
        String normalized = Normalizer.normalize(cadena, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\P{ASCII}+");
        return pattern.matcher(normalized).replaceAll("");

    }

    public static String conver64(File originalFile) {
        String encodedBase64 = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
            byte[] bytes = new byte[(int) originalFile.length()];
            fileInputStreamReader.read(bytes);
            encodedBase64 = new String(Base64.getEncoder().encodeToString(bytes));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedBase64;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JSONException ex) {
            Logger.getLogger(ServletVideo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
