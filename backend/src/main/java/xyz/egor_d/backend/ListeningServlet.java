package xyz.egor_d.backend;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListeningServlet extends HttpServlet {
    Logger log = Logger.getLogger(ListeningServlet.class.getName());
    Api api = new Api();
    String[] answers = {"Cool!", "Lol :D", ":)", "Heh", "(:", ":D", "Yep"};

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        StringBuilder jb = new StringBuilder();
        String line;
        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
        } catch (Exception e) { /*report an error*/ }

        log.info("request=" + jb.toString());
        Update update = new Gson().fromJson(jb.toString(), Update.class);
        log.info("incoming text=" + update.message.text);

        processMessage(update);
    }

    private void processMessage(final Update update) {
        String text = update.message.text == null ? "" : update.message.text.trim();
        String chatId = String.valueOf(update.message.chat.id);
        if (text.isEmpty()) {
            api.sendMessage(chatId, "I understand text only :(");
        } else if (text.startsWith("/start")) {
            JsonArray keyboardArray = new JsonArray();
            keyboardArray.add("Hi!");
            keyboardArray.add("Hello!");

            JsonArray keyboardArrayWrapper = new JsonArray();
            keyboardArrayWrapper.add(keyboardArray);

            JsonObject replyKeyboard = new JsonObject();
            replyKeyboard.add("keyboard", keyboardArrayWrapper);
            replyKeyboard.addProperty("one_time_keyboard", true);
            replyKeyboard.addProperty("resize_keyboard", true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("chat_id", chatId);
            jsonObject.addProperty("text", "Hi, " + update.message.chat.first_name + "!");
            jsonObject.add("reply_markup", replyKeyboard);
            api.sendMessage(jsonObject);
        } else if (text.equalsIgnoreCase("Hello!") || text.equalsIgnoreCase("Hi!")) {
            api.sendMessage(chatId, "Nice to meet you \uD83D\uDC4B How are you?");
        } else if (text.startsWith("/stop")) {

        } else {
            api.sendMessage(chatId, String.valueOf(update.message.message_id), answers[new Random().nextInt(answers.length)]);
        }
    }
}
