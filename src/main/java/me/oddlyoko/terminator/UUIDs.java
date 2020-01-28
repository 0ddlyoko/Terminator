package me.oddlyoko.terminator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class UUIDs {
	private static File f;
	// Contient la liste triée des UUID des personnes qui se sont connectés au moins
	// une fois sur le serveur
	private static ArrayList<UUID> uuid;

	// Contient la liste des UUID des personnes qui se sont connectés au moins une
	// fois sur le serveur
	private static HashMap<UUID, Peer> uuids;
	// Contient la liste des pseudos des personnes qui se sont connectés au moins
	// une fois sur le serveur
	private static HashMap<String, Peer> names;

	private static Pattern space = Pattern.compile(" ");
	static {
		uuid = new ArrayList<>();
		uuids = new HashMap<>();
		names = new HashMap<>();
		// Création du fichier
		f = new File("plugins" + File.separator + __.NAME + File.separator + "usercache");
		try {
			if (!f.exists() && !f.createNewFile())
				Bukkit.getLogger().severe(f.getName() + " cannot be created !");
			else if (!f.isFile()) {
				Bukkit.getLogger().severe(f.getName() + " is not a file !");
			} else {
				// Tout est bon
				// On lit les entrées
				Bukkit.getLogger().info("All is ok, reading usercache file");
				Scanner sc = new Scanner(new FileInputStream(f));
				String line;
				while (sc.hasNextLine()) {
					line = sc.nextLine();
					String[] strs = space.split(line);
					if (strs.length == 2 || strs.length == 3) {
						try {
							String strUuid = strs[0];
							String name = strs[1];
							UUID uuid = UUID.fromString(strUuid);
							register(uuid, name);
						} catch (IllegalArgumentException ex) {
							Bukkit.getLogger().warning("Cannot read this line: " + line);
						}
					}
				}
				Bukkit.getLogger().info("Usercache Done !");
				sc.close();
			}
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Error", ex);
		}
	}

	/**
	 * Save the HashMap on the Hard-Disk.<br />
	 */
	public static void save() {
		try {
			synchronized (uuids) {
				FileWriter fw = new FileWriter(f);

				Iterator<UUID> it = uuid.iterator();
				UUID e;
				while (it.hasNext()) {
					e = it.next();
					fw.append(e.toString()).append(" ").append(uuids.get(e).pseudo).append("\n");
				}
				fw.close();
			}
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Error: ", ex);
		}
	}

	/**
	 * Save the UUID of specific player in memory<br />
	 * THIS METHOD DOESN'T SAVE IN HARD DISK<br />
	 * 
	 * @param uuid
	 *                 The uuid of the player
	 * @param name
	 *                 The name of the player
	 */
	public static void register(UUID uuid, String name) {
		synchronized (uuids) {
			Peer p = uuids.get(uuid);
			// If uuid does not exist, create a new entry
			if (p == null) {
				p = new Peer(name, uuid);
				UUIDs.uuid.add(uuid);
				uuids.put(uuid, p);
			}
			// Different name
			if (!p.getPseudo().equals(name)) {
				// Remove old name from the list
				names.remove(p.getPseudo());
				// Add the new name
				p.setPseudo(name);
			}
			names.put(name, p);
		}
	}

	/**
	 * @param uuid
	 *                 The uuid of the player
	 * @return The name of specific player or null if not found
	 */
	public static String get(UUID uuid) {
		// Return if player is in the list
		Peer p;
		if ((p = uuids.get(uuid)) != null)
			return p.getPseudo();
		return null;
	}

	/**
	 * @param pseudo
	 *                   The name of the player
	 * @return The uuid of specific player or null if not found
	 */
	public static UUID get(String pseudo) {
		// Return if player is in the list
		Peer p;
		if ((p = names.get(pseudo)) != null)
			return p.getUuid();
		return null;
	}

	/**
	 * Used to load this class
	 */
	public static void load() {
	}

	@Getter
	@AllArgsConstructor
	private static class Peer {
		@Setter
		private String pseudo;
		private UUID uuid;
	}
}
