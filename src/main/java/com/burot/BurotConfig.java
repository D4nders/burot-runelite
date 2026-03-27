package com.burot;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("burot")
public interface BurotConfig extends Config {

	@ConfigSection(name = "Discord Notifications", description = "", position = 1)
	String discordNotificationSection = "discordNotificationSection";

	@ConfigSection(name = "Audio Settings", description = "", position = 2)
	String audioSettingsSection = "audioSettingsSection";

	@ConfigSection(name = "Achievement Diary Tiers", description = "", position = 3, closedByDefault = true)
	String diaryTiersSection = "diaryTiersSection";

	@ConfigSection(name = "Combat Achievement Tiers", description = "", position = 4, closedByDefault = true)
	String combatTiersSection = "combatTiersSection";

	@ConfigItem(keyName = "devMode", name = "Enable Developer Mode", description = "", position = 0)
	default boolean devMode() { return false; }

	@ConfigItem(keyName = "universalSoundMute", name = "Universal Sound Mute", description = "", position = 0, section = audioSettingsSection)
	default boolean universalSoundMute() { return false; }

	@ConfigItem(keyName = "webhookUrl", name = "Discord Webhook URL", description = "", position = 1, section = discordNotificationSection)
	default String webhookUrl() { return ""; }

	@ConfigItem(keyName = "notifyCollectionLog", name = "Notify Collection Log", description = "", position = 2, section = discordNotificationSection)
	default boolean notifyCollectionLog() { return true; }

	@ConfigItem(keyName = "notifyAchievementDiary", name = "Notify Achievement Diary", description = "", position = 3, section = discordNotificationSection)
	default boolean notifyAchievementDiary() { return true; }

	@ConfigItem(keyName = "notifyPet", name = "Notify Pet (Funny Feeling)", description = "", position = 4, section = discordNotificationSection)
	default boolean notifyPet() { return true; }

	@ConfigItem(keyName = "notifyCombatAchievement", name = "Notify Combat Achievement", description = "", position = 5, section = discordNotificationSection)
	default boolean notifyCombatAchievement() { return true; }

	@ConfigItem(keyName = "notifyValuableDrop", name = "Notify Valuable Drop", description = "", position = 6, section = discordNotificationSection)
	default boolean notifyValuableDrop() { return true; }

	@ConfigItem(keyName = "valuableDropThreshold", name = "Valuable Drop Threshold", description = "", position = 7, section = discordNotificationSection)
	default int valuableDropThreshold() { return 5000000; }

	@ConfigItem(keyName = "notifyQuest", name = "Notify Quest Completion", description = "", position = 8, section = discordNotificationSection)
	default boolean notifyQuest() { return true; }

	@ConfigItem(keyName = "notifyLevelUp", name = "Notify Level Up", description = "", position = 9, section = discordNotificationSection)
	default boolean notifyLevelUp() { return true; }

	@ConfigItem(keyName = "skillLevelThreshold", name = "Skill Level Threshold", description = "", position = 10, section = discordNotificationSection)
	default int skillLevelThreshold() { return 99; }

	@ConfigItem(keyName = "combatLevelThreshold", name = "Combat Level Threshold", description = "", position = 11, section = discordNotificationSection)
	default int combatLevelThreshold() { return 126; }

	@ConfigItem(keyName = "notifyDiaryEasy", name = "Easy", description = "", position = 1, section = diaryTiersSection)
	default boolean notifyDiaryEasy() { return true; }

	@ConfigItem(keyName = "notifyDiaryMedium", name = "Medium", description = "", position = 2, section = diaryTiersSection)
	default boolean notifyDiaryMedium() { return true; }

	@ConfigItem(keyName = "notifyDiaryHard", name = "Hard", description = "", position = 3, section = diaryTiersSection)
	default boolean notifyDiaryHard() { return true; }

	@ConfigItem(keyName = "notifyDiaryElite", name = "Elite", description = "", position = 4, section = diaryTiersSection)
	default boolean notifyDiaryElite() { return true; }

	@ConfigItem(keyName = "notifyCombatEasy", name = "Easy", description = "", position = 1, section = combatTiersSection)
	default boolean notifyCombatEasy() { return false; }

	@ConfigItem(keyName = "notifyCombatMedium", name = "Medium", description = "", position = 2, section = combatTiersSection)
	default boolean notifyCombatMedium() { return false; }

	@ConfigItem(keyName = "notifyCombatHard", name = "Hard", description = "", position = 3, section = combatTiersSection)
	default boolean notifyCombatHard() { return false; }

	@ConfigItem(keyName = "notifyCombatElite", name = "Elite", description = "", position = 4, section = combatTiersSection)
	default boolean notifyCombatElite() { return true; }

	@ConfigItem(keyName = "notifyCombatMaster", name = "Master", description = "", position = 5, section = combatTiersSection)
	default boolean notifyCombatMaster() { return true; }

	@ConfigItem(keyName = "notifyCombatGrandmaster", name = "Grandmaster", description = "", position = 6, section = combatTiersSection)
	default boolean notifyCombatGrandmaster() { return true; }

	@ConfigItem(keyName = "notifyCombatTierCompletion", name = "Full Tier Completed", description = "", position = 7, section = combatTiersSection)
	default boolean notifyCombatTierCompletion() { return true; }

	@ConfigItem(keyName = "enableCollectionLogSound", name = "Enable Collection Log Sound", description = "", position = 1, section = audioSettingsSection)
	default boolean enableCollectionLogSound() { return true; }

	@ConfigItem(keyName = "collectionLogSoundPath", name = "Custom Collection Log Sound Path", description = "", position = 2, section = audioSettingsSection)
	default String collectionLogSoundPath() { return ""; }

	@ConfigItem(keyName = "enableDiarySound", name = "Enable Diary Sound", description = "", position = 3, section = audioSettingsSection)
	default boolean enableDiarySound() { return true; }

	@ConfigItem(keyName = "diarySoundPath", name = "Custom Diary Sound Path", description = "", position = 4, section = audioSettingsSection)
	default String diarySoundPath() { return ""; }

	@ConfigItem(keyName = "enablePetSound", name = "Enable Pet Sound", description = "", position = 5, section = audioSettingsSection)
	default boolean enablePetSound() { return true; }

	@ConfigItem(keyName = "petSoundPath", name = "Custom Pet Sound Path", description = "", position = 6, section = audioSettingsSection)
	default String petSoundPath() { return ""; }

	@ConfigItem(keyName = "enableCombatTaskSound", name = "Enable Combat Task Sound", description = "", position = 7, section = audioSettingsSection)
	default boolean enableCombatTaskSound() { return true; }

	@ConfigItem(keyName = "combatTaskSoundPath", name = "Custom Combat Task Sound Path", description = "", position = 8, section = audioSettingsSection)
	default String combatTaskSoundPath() { return ""; }

	@ConfigItem(keyName = "enableCombatTierSound", name = "Enable Full Combat Tier Sound", description = "", position = 9, section = audioSettingsSection)
	default boolean enableCombatTierSound() { return true; }

	@ConfigItem(keyName = "combatTierSoundPath", name = "Custom Combat Tier Sound Path", description = "", position = 10, section = audioSettingsSection)
	default String combatTierSoundPath() { return ""; }

	@ConfigItem(keyName = "enableValuableDropSound", name = "Enable Valuable Drop Sound", description = "", position = 11, section = audioSettingsSection)
	default boolean enableValuableDropSound() { return true; }

	@ConfigItem(keyName = "valuableDropSoundPath", name = "Custom Valuable Drop Sound Path", description = "", position = 12, section = audioSettingsSection)
	default String valuableDropSoundPath() { return ""; }

	@ConfigItem(keyName = "enableQuestSound", name = "Enable Quest Sound", description = "", position = 13, section = audioSettingsSection)
	default boolean enableQuestSound() { return true; }

	@ConfigItem(keyName = "questSoundPath", name = "Custom Quest Sound Path", description = "", position = 14, section = audioSettingsSection)
	default String questSoundPath() { return ""; }

	@ConfigItem(keyName = "enableLevelUpSound", name = "Enable Level Up Sound", description = "", position = 15, section = audioSettingsSection)
	default boolean enableLevelUpSound() { return true; }

	@ConfigItem(keyName = "levelUpSoundPath", name = "Custom Level Up Sound Path", description = "", position = 16, section = audioSettingsSection)
	default String levelUpSoundPath() { return ""; }
}