package me.oddlyoko.terminator.config;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MessageManager {
	private static Config config;
	private static HashMap<String, String> messages;
	private static HashMap<String, List<String>> messagesList;

	static {
		config = new Config(new File("plugins" + File.separator + "Terminator" + File.separator + "lang.yml"));
		messages = new HashMap<>();
		messagesList = new HashMap<>();
		register("date");
		registerList("loading");
		register("commands.perm");
		register("commands.notloaded");
		register("commands.playerNotFound");
		register("commands.playerNotConnected");
		register("commands.notAPlayer");

		register("commands.ban.syntax");
		register("commands.ban.parse");
		register("commands.ban.outofrange");
		register("commands.ban.alreadyBanned");
		register("commands.ban.bypass");

		register("commands.banip.syntax");
		register("commands.banip.parse");
		register("commands.banip.outofrange");
		register("commands.banip.ipNotFound");
		register("commands.banip.alreadyBanned");

		register("commands.bypass.syntax");
		register("commands.bypass.add");
		register("commands.bypass.remove");

		register("commands.kick.syntax");
		register("commands.kick.bypass");

		register("commands.mute.syntax");
		register("commands.mute.parse");
		register("commands.mute.outofrange");
		register("commands.mute.alreadyMuted");
		register("commands.mute.bypass");

		register("commands.unban.syntax");
		register("commands.unban.notBanned");

		register("commands.unbanip.syntax");
		register("commands.unbanip.ipNotFound");
		register("commands.unbanip.notBanned");

		register("commands.unmute.syntax");
		register("commands.unmute.notMuted");

		register("console");

		registerList("ban.player.until");
		registerList("ban.player.perm");
		register("ban.admin.until");
		register("ban.admin.perm");

		register("unban.noId");
		register("unban.ok");

		registerList("banip.player.until");
		registerList("banip.player.perm");
		register("banip.admin.knowuser.until");
		register("banip.admin.knowuser.perm");
		register("banip.admin.unknownuser.until");
		register("banip.admin.unknownuser.perm");
		register("banip.admin.hidden");

		register("unbanip.noId");
		register("unbanip.ok");

		registerList("kick.player");
		register("kick.admin");

		register("mute.talk.until");
		register("mute.talk.perm");
		register("mute.player.until");
		register("mute.player.perm");
		register("mute.admin.until");
		register("mute.admin.perm");

		register("unmute.noId");
		register("unmute.ok");
	}

	// Used to load the class
	public static void load() {
	}

	private static void register(String key) {
		messages.put(key, config.getString(key));
	}

	private static void registerList(String key) {
		messagesList.put(key, config.getStringList(key));
	}

	public static String get(String id) {
		return messages.getOrDefault(id, id);
	}

	public static List<String> getList(String id) {
		return messagesList.getOrDefault(id, Arrays.asList(id));
	}
}
