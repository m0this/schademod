package de.mathis;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.TranslatableTextContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SchadeModClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SchadeModClient.class);
	private static Random random = new Random();
	private static Timer timer = new Timer();

	@Override
	public void onInitializeClient() {
		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			ClientPlayerEntity player = MinecraftClient.getInstance().player;

			if (message.getContent() instanceof TranslatableTextContent) {
				try {
					TranslatableTextContent translatableContent = (TranslatableTextContent) message.getContent();
					String translateKey = translatableContent.getKey();
					String name = translatableContent.getArgs().length > 0 ? extractName(translatableContent.getArgs()[0].toString()) : "";

					if (translateKey.startsWith("death.")) {
						assert player != null;
						if (!name.equalsIgnoreCase(player.getName().getString())) {
							sendSchadeMessage(player);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Error processing message: {}", e.getMessage());
				}
			}
		});
	}

	private String extractName(String argString) {
		int startIndex = argString.indexOf("literal{") + "literal{".length();
		int endIndex = argString.indexOf("}", startIndex);
		if (startIndex != -1 && endIndex != -1) {
			return argString.substring(startIndex, endIndex);
		}

		return argString;
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