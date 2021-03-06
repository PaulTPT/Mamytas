package mn.aug.restfulandroid.rest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import mn.aug.restfulandroid.util.Logger;

public class RestClient {

    private static byte[] readStream(InputStream in) throws IOException {
        byte[] buf = new byte[1024];
        int count = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        while ((count = in.read(buf)) != -1)
            out.write(buf, 0, count);
        return out.toByteArray();
    }

    public Response execute(Request request) {
        HttpURLConnection conn = null;
        Response response = null;
        int status = -1;
        try {

            URL url = request.getRequestUri().toURL();
            conn = (HttpURLConnection) url.openConnection();
            if (request.getHeaders() != null) {
                for (String header : request.getHeaders().keySet()) {
                    for (String value : request.getHeaders().get(header)) {
                        conn.addRequestProperty(header, value);
                    }
                }
            }

            byte[] payload = request.getBody();

            switch (request.getMethod()) {
                case GET:
                    conn.setRequestMethod("GET");
                    conn.setDoOutput(false);
                    conn.setDoInput(true);
                    status = conn.getResponseCode();
                    break;
                case POST:
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setFixedLengthStreamingMode(payload.length);
                    conn.getOutputStream().write(payload);
                    status = conn.getResponseCode();
                    break;
                case PUT:
                    conn.setRequestMethod("PUT");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setFixedLengthStreamingMode(payload.length);
                    conn.getOutputStream().write(payload);
                    status = conn.getResponseCode();
                    break;
                case DELETE:
                    conn.setRequestMethod("DELETE");
                    conn.setDoOutput(false);
                    conn.setDoInput(true);
                    status = conn.getResponseCode();
                    break;
                default:
                    break;
            }

            byte[] body= new String("").getBytes();

            if(status==200) {
                BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                body = readStream(in);
                Logger.debug("body",new String(body));
            }
            response = new Response(conn.getResponseCode(), conn.getHeaderFields(), body);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        if (response == null) {
            response = new Response(status, new HashMap<String, List<String>>(), new byte[]{});
        }

        return response;
    }
}
