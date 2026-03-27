package com.burot;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanID;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

@Slf4j
@PluginDescriptor(
		name = "Burot"
)
public class BurotPlugin extends Plugin {

	@Inject
	private Client gameClient;

	@Inject
	private BurotConfig pluginConfiguration;

	@Inject
	private OkHttpClient sharedNetworkClient;

	@Inject
	private ClientToolbar clientToolbar;

	private List<GameEventProcessor> activeEventProcessors;
	private NavigationButton devModeNavigationButton;

	@Provides
	BurotConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(BurotConfig.class);
	}

	@Override
	protected void startUp() {
		SharedEventState sharedEventState = new SharedEventState();

		List<Notifier> instantiatedNotifiers = Arrays.asList(
				new DiscordWebhookNotifier(pluginConfiguration, sharedNetworkClient),
				new AudioNotifier()
		);

		activeEventProcessors = Arrays.asList(
				new PetEventProcessor(instantiatedNotifiers, pluginConfiguration, sharedEventState),
				new CollectionLogEventProcessor(instantiatedNotifiers, pluginConfiguration, sharedEventState),
				new AchievementDiaryEventProcessor(instantiatedNotifiers, pluginConfiguration)
		);

		initializeDevModeInterface();
	}

	private void initializeDevModeInterface() {
		BurotPanel testingPanel = new BurotPanel(activeEventProcessors, gameClient);

		BufferedImage devIcon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphicsContext = devIcon.createGraphics();
		graphicsContext.setColor(Color.RED);
		graphicsContext.fillRect(0, 0, 16, 16);
		graphicsContext.dispose();

		devModeNavigationButton = NavigationButton.builder()
				.tooltip("Burot Dev Mode")
				.icon(devIcon)
				.priority(5)
				.panel(testingPanel)
				.build();

		if (pluginConfiguration.devMode()) {
			clientToolbar.addNavigation(devModeNavigationButton);
		}
	}

	@Override
	protected void shutDown() {
		clientToolbar.removeNavigation(devModeNavigationButton);
		activeEventProcessors = null;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configurationEvent) {
		if (!configurationEvent.getGroup().equals("burot") || !configurationEvent.getKey().equals("devMode")) {
			return;
		}

		if (pluginConfiguration.devMode()) {
			clientToolbar.addNavigation(devModeNavigationButton);
		} else {
			clientToolbar.removeNavigation(devModeNavigationButton);
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage incomingChatMessage) {
		Player localPlayerEntity = gameClient.getLocalPlayer();

		if (localPlayerEntity == null) {
			return;
		}

		String activePlayerName = localPlayerEntity.getName();

		ClanChannel activeClanChannel = gameClient.getClanChannel(ClanID.CLAN);
		String activeClanName = (activeClanChannel != null) ? activeClanChannel.getName() : "";

		int currentClientTick = gameClient.getTickCount();

		for (GameEventProcessor targetedProcessor : activeEventProcessors) {
			targetedProcessor.evaluateIncomingEvent(incomingChatMessage, activePlayerName, activeClanName, currentClientTick);
		}
	}
}