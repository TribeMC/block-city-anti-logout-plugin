package antilogg;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class AntiLogg {

	private Plugin pl;
	private Nachrichten msg;

	private int inFight;
	private final List<Player> toMessage;

	public AntiLogg(Nachrichten msg, Main main, int i) {
		this.pl = main;
		this.msg = msg;
		this.inFight = i * 1000;
		toMessage = new LinkedList<>();

		SendMessages();

	}

	public boolean isInFight(Player p) {
		if (p.hasMetadata("LastInFight")) {
			return (System.currentTimeMillis() - p.getMetadata("LastInFight")
					.get(0).asLong()) <= 0;
		}
		return false;
	}

	public void setLastFight(Player p) {
		p.setMetadata("LastInFight",
				new FixedMetadataValue(this.pl,
						(System.currentTimeMillis() + inFight)));
		if (!toMessage.contains(p)) {
			toMessage.add(p);
		}
	}

	@SuppressWarnings("deprecation")
	public void dropItems(Player p) {
		for (ItemStack i : p.getInventory().getArmorContents()) {
			if (i != null && !i.getType().equals(Material.AIR)) {
				p.getWorld().dropItemNaturally(p.getLocation(), i);
			}
		}
		p.getInventory().setArmorContents(null);
		for (ItemStack i : p.getInventory().getContents()) {
			if (i != null && !i.getType().equals(Material.AIR)) {
				p.getWorld().dropItemNaturally(p.getLocation(), i);
			}
		}
		p.getInventory().clear();
		p.updateInventory();
		p.setHealth(0.0);
		setLogged(p);
	}

	private void setLogged(Player p) {
		p.setMetadata("Logged", new FixedMetadataValue(this.pl, true));
	}

	private void SendMessages() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this.pl,
				new Runnable() {

					@Override
					public void run() {
						for (Player p : toMessage) {
							if (!isInFight(p)) {
								p.sendMessage(msg.prefix()
										+ msg.message("OutOfFight"));
								toMessage.remove(p);
							}
						}

					}
				}, 20, 20);
	}

	public void removeToMessage(Player p) {
		if (this.toMessage.contains(p)) {
			this.toMessage.remove(p);
		}

	}

	public void savePlayers() {
		for (Player p : toMessage) {
			p.removeMetadata("LastInFight", pl);
		}
	}

}
