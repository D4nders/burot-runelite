package com.burot;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("burot")
public interface BurotConfig extends Config
{
	@ConfigItem(
			keyName = "webhookUrl",
			name = "Discord Webhook URL",
			description = ""
	)
	default String webhookUrl() {
		return "";
	}

	@ConfigItem(
			keyName = "notifyCollectionLog",
			name = "Notify Collection Log",
			description = ""
	)
	default boolean notifyCollectionLog() {
		return true;
	}

	@ConfigItem(
			keyName = "devMode",
			name = "Enable Developer Mode",
			description = ""
	)
	default boolean devMode() {
		return false;
	}

	@ConfigItem(
			keyName = "collectionLogSoundPath",
			name = "Collection Log Sound Path",
			description = ""
	)
	default String collectionLogSoundPath() {
		return "C:/sounds/collectionlog.wav";
	}
}