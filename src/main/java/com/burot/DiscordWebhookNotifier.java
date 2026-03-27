package com.burot;

import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

public class DiscordWebhookNotifier implements Notifier {

    private final BurotConfig pluginConfiguration;
    private final OkHttpClient networkClient;

    public DiscordWebhookNotifier(BurotConfig pluginConfiguration, OkHttpClient networkClient) {
        this.pluginConfiguration = pluginConfiguration;
        this.networkClient = networkClient;
    }

    @Override
    public void dispatchNotification(String formattedEventText, String targetSoundFilePath) {
        String targetWebhookUrl = pluginConfiguration.webhookUrl();

        if (targetWebhookUrl == null || targetWebhookUrl.isEmpty()) {
            return;
        }

        JsonObject discordPayload = new JsonObject();
        discordPayload.addProperty("content", formattedEventText);

        RequestBody postBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                discordPayload.toString()
        );

        Request networkRequest = new Request.Builder()
                .url(targetWebhookUrl)
                .post(postBody)
                .build();

        networkClient.newCall(networkRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call failedCall, IOException exception) {
            }

            @Override
            public void onResponse(Call successfulCall, Response networkResponse) {
                networkResponse.close();
            }
        });
    }
}