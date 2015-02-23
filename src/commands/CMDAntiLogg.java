package commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import antilogg.AntiLogg;
import antilogg.Main;
import antilogg.Nachrichten;

public class CMDAntiLogg implements CommandExecutor {

	private Nachrichten msg;

	private AntiLogg al;

	private Plugin pl;

	public CMDAntiLogg(Nachrichten msg, AntiLogg al, Main main) {
		this.msg = msg;
		this.al = al;
		this.pl = main;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2,
			String[] args) {
		if (args.length == 0) {
			sendInfo(cs);
		} else {
			if (args[0].equalsIgnoreCase("info")) {
				sendInfo(cs);
			} else if (args[0].equalsIgnoreCase("reset") && args.length == 2) {
				if (!cs.hasPermission("antilogg.reset")) {
					cs.sendMessage(msg.prefix() + msg.message("NoPerm"));
					return true;
				}

				@SuppressWarnings("deprecation")
				Player p = Bukkit.getPlayer(args[1]);
				if (p == null) {
					cs.sendMessage(msg.prefix() + msg.message("NoPlayer"));
					return true;
				}
				if (!al.isInFight(p)) {
					cs.sendMessage(msg.prefix()
							+ msg.message("PlayerNotInFight"));
					return true;
				}
				al.removeToMessage(p);
				p.removeMetadata("LastInFight", pl);
				cs.sendMessage(msg.prefix() + msg.message("ChangeTrue"));
			} else {
				sendHelp(cs);
			}
		}
		return true;
	}

	private void sendHelp(CommandSender cs) {
		cs.sendMessage(msg.prefix() + msg.message("HelpHeader"));
		cs.sendMessage("");
		cs.sendMessage(msg.prefix() + msg.message("HelpTimeLeft"));
		cs.sendMessage(msg.prefix() + msg.message("HelpReset"));
	}

	private void sendInfo(CommandSender cs) {
		cs.sendMessage(msg.prefix()
				+ "Das Plugin wurde von §a§lV3lop5 %rerstellt!".replace("%r",
						msg.message("Color")));
		cs.sendMessage(msg.prefix() + "Für Plugin-Hilfe nutze &e/al Help");

	}

}
