package com.burot;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("burot")
public interface BurotConfig extends Config {

	@ConfigSection(
			name = "Discord Notifications",
			description = "",
			position = 1
	)
	String discordNotificationSection = "discordNotificationSection";

	@ConfigSection(
			name = "Audio Settings",
			description = "",
			position = 2
	)
	String audioSettingsSection = "audioSettingsSection";

	@ConfigItem(
			keyName = "devMode",
			name = "Enable Developer Mode",
			description = "",
			position = 0
	)
	default boolean devMode() {
		return false;
	}

	@ConfigItem(
			keyName = "webhookUrl",
			name = "Discord Webhook URL",
			description = "",
			position = 1,
			section = discordNotificationSection
	)
	default String webhookUrl() {
		return "";
	}

	@ConfigItem(
			keyName = "notifyCollectionLog",
			name = "Notify Collection Log",
			description = "",
			position = 2,
			section = discordNotificationSection
	)
	default boolean notifyCollectionLog() {
		return true;
	}

	@ConfigItem(
			keyName = "collectionLogSoundPath",
			name = "Collection Log Sound Path",
			description = "",
			position = 1,
			section = audioSettingsSection
	)
	default String collectionLogSoundPath() {
		return "C:/sounds/collectionlog.wav";
	}
}