package antilogg;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class Nachrichten {

	private HashMap<String, String> messages = new LinkedHashMap<>();

	public Nachrichten(ConfigurationSection cs) {
		String color = cs.getString("Color");
		for (String temp : cs.getKeys(false)) {
			
				messages.put(temp.toLowerCase(), ChatColor
						.translateAlternateColorCodes('&', cs.getString(temp)
								.replace("%r", color)));
			
		}
	}

	public String message(String s) {
		if (messages.containsKey(s.toLowerCase())) {
			return messages.get(s.toLowerCase());
		}

		return "§c§l! §7" + s;
	}

	public String prefix() {
		return this.messages.get("prefix") + this.messages.get("color");
	}

}
