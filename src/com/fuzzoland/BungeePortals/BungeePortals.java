package com.fuzzoland.BungeePortals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.fuzzoland.BungeePortals.Commands.CommandBPortals;
import com.fuzzoland.BungeePortals.Listeners.EventListener;
import com.fuzzoland.BungeePortals.Tasks.SaveTask;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class BungeePortals extends JavaPlugin{

	private Logger logger = Bukkit.getLogger();
	public Map<String, String> portalData = new HashMap<String, String>();
	public WorldEditPlugin worldEdit = null;
	public YamlConfiguration configFile = null;
	public YamlConfiguration portalsFile = null;

	public void onEnable(){
		Long time = System.currentTimeMillis();
		if(getServer().getPluginManager().getPlugin("WorldEdit") == null){
			getPluginLoader().disablePlugin(this);
			throw new NullPointerException("[BungeePortals] WorldEdit not found, disabling...");
		}
		this.worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		startMetrics();
		getCommand("BPortals").setExecutor(new CommandBPortals(this));
		this.logger.log(Level.INFO, "[BungeePortals] Commands registered!");
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		this.logger.log(Level.INFO, "[BungeePortals] Events registered!");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.logger.log(Level.INFO, "[BungeePortals] Plugin channel registered!");
		loadConfigurationFiles();
		loadPortalsData();
		Integer interval = this.configFile.getInt("SaveTask.Interval") * 20;
		new SaveTask(this).runTaskTimer(this, interval, interval);
		this.logger.log(Level.INFO, "[BungeePortals] Save task started!");
		this.logger.log(Level.INFO, "[BungeePortals] Version " + getDescription().getVersion() + " has been enabled. (" + (System.currentTimeMillis() - time) + "ms)");
	}
	
	public void onDisable(){
		Long time = System.currentTimeMillis();
		savePortalsData();
		this.logger.log(Level.INFO, "[BungeePortals] Version " + getDescription().getVersion() + " has been disabled. (" + (System.currentTimeMillis() - time) + "ms)");
	}
	
	private void startMetrics(){
		try{
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
			this.logger.log(Level.INFO, "[BungeePortals] Metrics initiated!");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void createConfigurationFile(InputStream in, File file){
		try{
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void loadConfigurationFiles(){
		File configFile = new File(getDataFolder(), "config.yml");
		if(!configFile.exists()){
			configFile.getParentFile().mkdirs();
			createConfigurationFile(getResource("config.yml"), configFile);
			this.logger.log(Level.INFO, "[BungeePortals] Configuration file config.yml created!");
		}
		this.configFile = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
		this.logger.log(Level.INFO, "[BungeePortals] Configuration file config.yml loaded!");
		File portalsFile = new File(getDataFolder(), "portals.yml");
		if(!portalsFile.exists()){
			portalsFile.getParentFile().mkdirs();
			createConfigurationFile(getResource("portals.yml"), portalsFile);
			this.logger.log(Level.INFO, "[BungeePortals] Configuration file portals.yml created!");
		}
		this.portalsFile = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "portals.yml"));
		this.logger.log(Level.INFO, "[BungeePortals] Configuration file portals.yml loaded!");
	}
	
	public void loadPortalsData(){
		try{
			Long time = System.currentTimeMillis();
			for(String key : this.portalsFile.getKeys(false)){
	    	    String value = this.portalsFile.getString(key);
	    	    this.portalData.put(key, value);
	    	}
			this.logger.log(Level.INFO, "[BungeePortals] Portal data loaded! (" + (System.currentTimeMillis() - time) + "ms)");
	    }catch(NullPointerException e){ }
	}
	
	public void savePortalsData(){
		Long time = System.currentTimeMillis();
		for(Entry<String, String> entry : this.portalData.entrySet()){
			this.portalsFile.set(entry.getKey(), entry.getValue());
		}
		try{
			this.portalsFile.save(new File(getDataFolder(), "portals.yml"));
		}catch(IOException e){
			e.printStackTrace();
		}
		this.logger.log(Level.INFO, "[BungeePortals] Portal data saved! (" + (System.currentTimeMillis() - time) + "ms)");
	}
}
