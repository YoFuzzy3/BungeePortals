package com.fuzzoland.BungeePortals.Tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.fuzzoland.BungeePortals.BungeePortals;

public class SaveTask extends BukkitRunnable{

	private BungeePortals plugin;
	
	public SaveTask(BungeePortals plugin){
		this.plugin = plugin;
	}
	
	public void run(){
		if(plugin.configFile.getBoolean("SaveTask.Enabled")){
			plugin.savePortalsData();
		}
	}
}
