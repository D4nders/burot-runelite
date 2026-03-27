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

	@ConfigSection(
			name = "Achievement Diary Tiers",
			description = "",
			position = 3,
			closedByDefault = true
	)
	String diaryTiersSection = "diaryTiersSection";

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
			keyName = "notifyAchievementDiary",
			name = "Notify Achievement Diary",
			description = "",
			position = 3,
			section = discordNotificationSection
	)
	default boolean notifyAchievementDiary() {
		return true;
	}

	@ConfigItem(
			keyName = "notifyPet",
			name = "Notify Pet (Funny Feeling)",
			description = "",
			position = 4,
			section = discordNotificationSection
	)
	default boolean notifyPet() {
		return true;
	}

	@ConfigItem(
			keyName = "notifyDiaryEasy",
			name = "Easy",
			description = "",
			position = 1,
			section = diaryTiersSection
	)
	default boolean notifyDiaryEasy() {
		return true;
	}

	@ConfigItem(
			keyName = "notifyDiaryMedium",
			name = "Medium",
			description = "",
			position = 2,
			section = diaryTiersSection
	)
	default boolean notifyDiaryMedium() {
		return true;
	}

	@ConfigItem(
			keyName = "notifyDiaryHard",
			name = "Hard",
			description = "",
			position = 3,
			section = diaryTiersSection
	)
	default boolean notifyDiaryHard() {
		return true;
	}

	@ConfigItem(
			keyName = "notifyDiaryElite",
			name = "Elite",
			description = "",
			position = 4,
			section = diaryTiersSection
	)
	default boolean notifyDiaryElite() {
		return true;
	}

	@ConfigItem(
			keyName = "enableCollectionLogSound",
			name = "Enable Collection Log Sound",
			description = "",
			position = 1,
			section = audioSettingsSection
	)
	default boolean enableCollectionLogSound() {
		return true;
	}

	@ConfigItem(
			keyName = "collectionLogSoundPath",
			name = "Custom Collection Log Sound Path",
			description = "",
			position = 2,
			section = audioSettingsSection
	)
	default String collectionLogSoundPath() {
		return "";
	}

	@ConfigItem(
			keyName = "enableDiarySound",
			name = "Enable Diary Sound",
			description = "",
			position = 3,
			section = audioSettingsSection
	)
	default boolean enableDiarySound() {
		return true;
	}

	@ConfigItem(
			keyName = "diarySoundPath",
			name = "Custom Diary Sound Path",
			description = "",
			position = 4,
			section = audioSettingsSection
	)
	default String diarySoundPath() {
		return "";
	}

	@ConfigItem(
			keyName = "enablePetSound",
			name = "Enable Pet Sound",
			description = "",
			position = 5,
			section = audioSettingsSection
	)
	default boolean enablePetSound() {
		return true;
	}

	@ConfigItem(
			keyName = "petSoundPath",
			name = "Custom Pet Sound Path",
			description = "",
			position = 6,
			section = audioSettingsSection
	)
	default String petSoundPath() {
		return "";
	}
}