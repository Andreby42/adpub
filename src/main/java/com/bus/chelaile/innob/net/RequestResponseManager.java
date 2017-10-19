package com.bus.chelaile.innob.net;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.bus.chelaile.innob.request.Request;

/**
 * Created by Administrator on 2016/8/9.
 */
public class RequestResponseManager {
    private static final String TERMINAL      = "http://api.w.inmobi.cn/showad/v3";
    private static final String SAFE_TERMINAL = "https://api.w.inmobi.cn/showad/v3";
    private static final Logger logger = LoggerFactory
            .getLogger(RequestResponseManager.class);

    private static void setConnectionParams(HttpURLConnection connection) throws ProtocolException {
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(60 * 1000);
        connection.setReadTimeout(30 * 1000);
        connection.setUseCaches(false);
        connection.setRequestProperty("content-type", "application/json");
    }

    private void postData(HttpURLConnection connection, String payload) throws IOException {
        connection.setRequestProperty("content-length", Integer.toString(payload.length()));
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(payload);
        } finally {
            closeResource(bw);
        }
    }

    public InputStream fetchAdResponseAsStream(Request request) {
        String payloadStr = JsonPayloadBuilder.getRequestPayloadJson(request);
        InputStream is = null;
        if (payloadStr != null) {
            try {
                URL serverUrl = new URL(TERMINAL);
                HttpURLConnection connection = (HttpURLConnection)serverUrl.openConnection();
                setConnectionParams(connection);
                logger.debug(String.format("Post payload is %s", payloadStr));
                // System.out.println(payloadStr);
                postData(connection, payloadStr);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // System.out.println(connection.getResponseMessage());
                    // System.out.println(connection.get);
                    is = connection.getInputStream();
                } else {
                    logger.error("Request got an error code {}", connection.getResponseCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return is;
    }

    private String convertStreamToString(InputStream is) {
        String response = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line).append("\n");
            response = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResource(is);
        }
        return response;
    }

    public String fetchAdResponseAsString(Request request) {
        String response = null;
        try {
            InputStream is = fetchAdResponseAsStream(request);
            if (is == null)
                logger.error("Ad response is null");
            response = convertStreamToString(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private void closeResource(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
