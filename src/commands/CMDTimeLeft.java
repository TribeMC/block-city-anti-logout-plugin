package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import antilogg.AntiLogg;
import antilogg.Nachrichten;

public class CMDTimeLeft implements CommandExecutor {

	private Nachrichten msg;
	private AntiLogg al;

	public CMDTimeLeft(Nachrichten msg, AntiLogg al) {
		this.msg = msg;
		this.al = al;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2,
			String[] args) {
		if (cs instanceof Player) {
			Player p = (Player) cs;
			if (al.isInFight(p)) {
				p.sendMessage(msg.prefix()
						+ msg.message("InFight")
								.replace(
										"%TIME",
										((int) (p.getMetadata("LastInFight")
												.get(0).asLong() - System
												.currentTimeMillis()) / 1000)
												+ ""));
				return true;
			}

			p.sendMessage(msg.prefix() + msg.message("NotInFight"));

		} else {
			cs.sendMessage(msg.prefix() + msg.message("onlyPlayer"));
		}
		return true;
	}
}
