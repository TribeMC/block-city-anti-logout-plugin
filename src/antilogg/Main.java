package antilogg;

import org.bukkit.plugin.java.JavaPlugin;

import commands.CMDAntiLogg;
import commands.CMDTimeLeft;

public class Main extends JavaPlugin {

	/*
	 * Commands /al info /al version /al info <Player> /al reset <Player>
	 */

	private AntiLogg al;
	private Nachrichten msg;

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		copyDefaults();

		this.msg = new Nachrichten(getConfig().getConfigurationSection(
				"Nachrichten"));
		this.al = new AntiLogg(this.msg, this, getConfig().getInt(
				"InFightSeconds"));

		this.getServer()
				.getPluginManager()
				.registerEvents(
						new Events(al, msg, this, getConfig()
								.getConfigurationSection("Events")), this);
		CMDTimeLeft tl = new CMDTimeLeft(msg, al);
		getCommand("tl").setExecutor(tl);
		getCommand("timeleft").setExecutor(tl);

		CMDAntiLogg cmdal = new CMDAntiLogg(msg, al, this);
		getCommand("al").setExecutor(cmdal);
		getCommand("antilogg").setExecutor(cmdal);
		super.onEnable();
	}

	private void copyDefaults() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public void onDisable() {
		al.savePlayers();
		super.onDisable();
	}
}
