package me.oddlyoko.terminator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class UUIDs {
	private static File f;

	// Contient la liste des pseudos des personnes qui se sont connectés au moins
	// une fois sur le serveur
	private static HashMap<UUID, Pear> uuids;
	// Contient la liste des UUID des personnes qui se sont connectés au moins une
	// fois sur le serveur
	private static HashMap<String, Pear> names;

	static {
		uuids = new HashMap<>();
		names = new HashMap<>();
		// Création du fichier
		f = new File("usercache.txt");
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
					String[] strs = line.split(" ");
					if (strs.length == 2) {
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

				Iterator<Entry<UUID, Pear>> it = uuids.entrySet().iterator();
				Entry<UUID, Pear> e;
				while (it.hasNext()) {
					e = it.next();
					fw.append(e.getKey().toString()).append(" ").append(e.getValue().getPseudo()).append("\n");
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
			Pear p;
			if ((p = uuids.get(uuid)) != null) {
				// Same uuid
				if (p.getPseudo().equals(name))
					// Same pseudo, don't change
					return;
				// Change pseudo and update
				p.setPseudo(name);
				names.remove(name);
				names.put(name, p);
			} else {
				if ((p = names.get(name)) != null)
					// Same name
					uuids.remove(p.getUuid());
				// Create
				p = Pear.of(name, uuid);
				uuids.put(uuid, p);
				names.put(name, p);
			}
		}
	}

	/**
	 * @param uuid
	 *                 The uuid of the player
	 * @return The name of specific player or null if not found
	 */
	public static String get(UUID uuid) {
		// Return if player is in the list
		Pear p;
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
		Pear p;
		if ((p = names.get(pseudo)) != null)
			return p.getUuid();
		return null;
	}

	/**
	 * Used to load this class
	 */
	public static void load() {
	}

	private static class Pear {
		private String pseudo;
		private UUID uuid;

		private Pear(String pseudo, UUID uuid) {
			this.pseudo = pseudo;
			this.uuid = uuid;
		}

		public static Pear of(String pseudo, UUID uuid) {
			return new Pear(pseudo, uuid);
		}

		public String getPseudo() {
			return pseudo;
		}

		public void setPseudo(String pseudo) {
			this.pseudo = pseudo;
		}

		public UUID getUuid() {
			return uuid;
		}
	}
}
