package de.mathis.schademod.client;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SchadeModClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchadeModClient.class);
    private static Random random = new Random();
    private static Timer timer = new Timer();

    @Override
    public void onInitializeClient() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            String json = Text.Serializer.toJson(message);

            try {
                JsonObject jsonObject = JsonHelper.deserialize(json).getAsJsonObject();

                String translate = JsonHelper.getString(jsonObject, "translate");
                String name = JsonHelper.getString(jsonObject.getAsJsonArray("with").get(0).getAsJsonObject(), "text");

                if (translate.startsWith("death.") && !name.equalsIgnoreCase(player.getName().getString())) {
                    sendSchadeMessage(player);
                }
            } catch (Exception e) {
                LOGGER.error("Error parsing Json: {}", e.getMessage());
            }

        });
    }

    private void sendSchadeMessage(ClientPlayerEntity player) {
        if (player != null) {
            int randomNumber = random.nextInt(2900) +1000;

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        player.networkHandler.sendChatMessage("schade");
                    } catch (Exception e) {
                        LOGGER.error("Fehler beim Senden der Nachricht: {}", e.getMessage());
                    }
                }
            }, randomNumber);
        }
    }

}

