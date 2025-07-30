package org.alvesdev.repository;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.file.Paths;

public class CallMakerManager {
    private static final File CONFIG_FILE = Paths.get("callmaker_config.json").toFile();

    public static boolean saveChannelId(String channelId) {
        JsonObject json = new JsonObject();
        json.addProperty("voice_channel_id", channelId);

        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            writer.write(json.toString());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String loadChannelId() {
        if (!CONFIG_FILE.exists()) return null;

        try (Reader reader = new FileReader(CONFIG_FILE)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            return json.get("voice_channel_id").getAsString();
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            return null;
        }
    }
}

