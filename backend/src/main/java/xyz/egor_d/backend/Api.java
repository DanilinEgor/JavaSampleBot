package xyz.egor_d.backend;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class Api {
    String token = "place_your_token_here";
    String endpoint = "https://api.telegram.org/bot" + token + "/";
    Logger log = Logger.getLogger(Api.class.getSimpleName());

    /**
     * Method for sending message to someone
     *
     * @param chatId to whom we send message
     * @param text   what we send
     */
    public void sendMessage(final String chatId, final String text) {
        try {
            doHttp("sendMessage", String.format("chat_id=%s&text=%s",
                    URLEncoder.encode(chatId, "utf-8"),
                    URLEncoder.encode(text, "utf-8")));
        } catch (IOException e) {
            log.warning("error=" + e.getMessage());
        }
    }

    public void sendMessage(final String chatId, final String replyToMessageId, final String text) {
        try {
            doHttp("sendMessage", String.format("chat_id=%s&reply_to_message_id=%s&text=%s",
                    URLEncoder.encode(chatId, "utf-8"),
                    URLEncoder.encode(replyToMessageId, "utf-8"),
                    URLEncoder.encode(text, "utf-8")));
        } catch (IOException e) {
            log.warning("error=" + e.getMessage());
        }
    }

    public void sendMessage(JsonObject jsonObject) {
        try {
            doHttp("sendMessage", true, jsonObject);
        } catch (IOException e) {
            log.warning("error=" + e.getMessage());
        }
    }

    private String doHttp(String method, String query) throws IOException {
        return doHttp(method + "?" + query, false, null);
    }

    private String doHttp(String method, boolean json, JsonObject jsonObject) throws IOException {
        String url = endpoint + method;
        log.info("url=" + url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Accept-Charset", "utf-8");
        if (json) {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            String jsonString = jsonObject.toString();
            log.info("jsonString=" + jsonString);
            os.write(jsonString.getBytes("UTF-8"));
            os.close();
        } else {
            connection.setRequestMethod("GET");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        log.info("response=" + response.toString());
        in.close();
        return response.toString();
    }
}
