package antilogg;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class Events implements Listener {

	private AntiLogg al;
	private Nachrichten msg;
	private Plugin pl;
	private List<String> bypasscmds;
	private List<String> bypasskickcause;

	public Events(AntiLogg al, Nachrichten msg, Main main,
			ConfigurationSection cs) {
		this.al = al;
		this.msg = msg;
		this.pl = main;

		this.bypasscmds = new LinkedList<>();
		for (String temp : cs.getStringList("BypassCommands")) {
			bypasscmds.add("/" + temp.toLowerCase());
		}

		this.bypasskickcause = new LinkedList<>();
		for (String temp : cs.getStringList("BypassKicks")) {
			bypasskickcause.add(temp.toLowerCase());
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (e.getPlayer().hasMetadata("Logged")) {
			if (e.getPlayer().getMetadata("Logged").get(0).asBoolean()) {
				e.getPlayer().sendMessage(msg.prefix() + msg.message("Logged"));
				e.getPlayer().removeMetadata("Logged", pl);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (this.al.isInFight(e.getPlayer())
				&& !e.getPlayer().hasPermission("antilogg.bypass")) {
			al.dropItems(e.getPlayer());
			brodCastQuit(e.getPlayer().getName());
		}
		al.removeToMessage(e.getPlayer());
	}

	private void brodCastQuit(String name) {
		Bukkit.getServer()
				.broadcastMessage(
						msg.prefix()
								+ msg.message("BrodCast").replace("%TARGET",
										name));

	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		if (!e.getPlayer().hasPermission("antilogg.bypass")
				&& isBypassKickCause(e.getReason())) {
			e.getPlayer().removeMetadata("LastInFight", pl);

		}
	}

	private boolean isBypassKickCause(String reason) {
		for (String temp : bypasskickcause) {
			if (reason.toLowerCase().contains(temp)) {
				return true;
			}

		}
		return false;
	}

	@EventHandler
	public void onDmg(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player tar = (Player) e.getEntity();

			if (e.getDamager() instanceof Player) {
				Player dmger = (Player) e.getDamager();
				setInFight(tar, dmger);
			} else if (e.getDamager() instanceof Projectile) {
				Projectile pj = (Projectile) e.getDamager();
				if (pj.getShooter() instanceof Player) {
					setInFight(tar, (Player) pj.getShooter());
				}
			}
		}
	}

	private void setInFight(Player tar, Player dmger) {
		if (tar == dmger) {

			return;
		}
		if (!al.isInFight(tar)) {
			tar.sendMessage(msg.prefix()
					+ msg.message("DamageRecive").replace("%DAMAGER",
							dmger.getName()));
		}
		al.setLastFight(tar);

		if (!al.isInFight(dmger)) {
			dmger.sendMessage(msg.prefix()
					+ msg.message("DamageOther").replace("%TARGET",
							tar.getName()));
		}
		al.setLastFight(dmger);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (al.isInFight(e.getEntity())) {
			e.getEntity().removeMetadata("LastInFight", pl);
			al.removeToMessage(e.getEntity());
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (al.isInFight(e.getPlayer())
				&& !this.bypasscmds.contains(e.getMessage().split(" ")[0]
						.toLowerCase())
				&& !e.getPlayer().hasPermission("antilogg.bypass")) {
			e.getPlayer().sendMessage(
					msg.prefix() + msg.message("DeniedInFight"));
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onTele(PlayerTeleportEvent e) {
		if (!e.getPlayer().hasPermission("antilogg.bypass")
				&& al.isInFight(e.getPlayer())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(
					msg.prefix() + msg.message("DeniedInFight"));
		}
	}
}
