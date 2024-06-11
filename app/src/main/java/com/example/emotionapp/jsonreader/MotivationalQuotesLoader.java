package com.example.emotionapp.jsonreader;
import android.content.Context;
import android.content.res.AssetManager;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class MotivationalQuotesLoader {

    public static MotivationalQuotes loadMotivationalQuotes(Context context) {
        Gson gson = new Gson();
        MotivationalQuotes motivationalQuotes = null;

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("motivational_quotes.json");
            Reader reader = new InputStreamReader(inputStream);

            motivationalQuotes = gson.fromJson(reader, MotivationalQuotes.class);

            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return motivationalQuotes;
    }
}
