# Burot - RuneLite Plugin

A highly customizable RuneLite plugin designed to broadcast your Old School RuneScape achievements, drops, and failures directly to Discord—perfect for keeping the clan updated ("for the boys").

Burot doesn't just send boring text to Discord; it **generates a high-quality replica of the in-game chatbox** and sends it as an image, making your webhook feeds look authentic and clean. Alongside Discord webhooks, Burot can also trigger local audio cues for every event.

## ✨ Features

Burot tracks a wide variety of in-game events. Every single feature can be toggled on or off, and many have configurable thresholds.

### 🎲 RNG & Drops
* **Pet Drops:** Broadcasts the "funny feeling" message.
* **Valuable Drops:** Triggers when loot exceeds your configurable GP threshold.
* **Collection Log:** Broadcasts new collection log slots and current progress.

### 🏆 Progression & Achievements
* **Level Ups:** Notifies when you hit a configurable threshold for Skills (default 99) and Combat (default 126).
* **Quest Completions:** Broadcasts completed quests.
* **Achievement Diaries:** Configurable by tier (Easy, Medium, Hard, Elite).
* **Combat Achievements:** Configurable by task tier and full tier completions.

### ⚔️ Combat, Bossing & Records
* **Personal Bests:** Tracks new records and categorizes them (Minigames, Agility, Raids, Barracuda Trials, Bosses) so you can filter what gets broadcasted.
* **Deaths:** * PvP Deaths (includes the value of the loot lost).
    * PvE Deaths to monsters (configurable).
* **Kills:** * PvP Kills (includes the value of the loot gained).
    * Raid Boss Kills (e.g., Olm, Verzik, Wardens).

## ⚙️ Configuration

### Discord Webhook Setup
To send messages to Discord, you need to provide a Webhook URL:
1. In your Discord server, go to **Server Settings** -> **Integrations** -> **Webhooks**.
2. Click **New Webhook**, name it, and select the channel.
3. Click **Copy Webhook URL**.
4. Paste this URL into the `Discord Webhook URL` field in the Burot plugin settings.

### Custom Audio Setup
By default, Burot comes with built-in sounds for events. However, you can completely customize this:
1. Find or create `.wav` audio files for the events you want to customize.
2. In the Burot plugin settings, locate the **Audio Settings** section.
3. Paste the absolute file path to your `.wav` file in the respective custom sound path box (e.g., `C:\Users\Name\Music\custom_pet_sound.wav`).
4. You can also globally mute the plugin using the `Universal Sound Mute` toggle.

## 🛠️ Developer Mode
If you want to test your webhook configurations or custom sound paths without actually getting a drop in-game, you can enable **Developer Mode**.

1. Check `Enable Developer Mode` in the plugin settings.
2. A red square icon will appear in your RuneLite sidebar.
3. Open the panel to simulate any of the tracked events instantly.

## 🐛 Bug Reports & Issues
Because Burot relies on reading the exact text from the RuneScape chatbox, game updates that change text formatting can sometimes break specific event triggers.

If an event fails to trigger, please submit an issue on GitHub using our Bug Report template. **Providing the exact, unedited in-game chat message is required for us to fix the issue.**