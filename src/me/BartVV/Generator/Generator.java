package me.BartVV.Generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.util.concurrent.AtomicDouble;

import me.BartVV.Generator.Commands.CMDHandler;
import me.BartVV.Generator.Listener.BlockBreak;
import me.BartVV.Generator.Listener.BlockPlace;
import me.BartVV.Generator.Listener.ChunkManager;
import me.BartVV.Generator.Listener.ClickEvent;
import me.BartVV.Generator.Listener.InteractEvent;
import me.BartVV.Generator.Listener.ListOfOres;
import net.milkbowl.vault.economy.Economy;

public class Generator extends JavaPlugin{
	
	public static Integer taskid;
    public static Economy economy = null;
    public static Generator inst;
	
	@Override
	public void onEnable() {
		Long begin = System.currentTimeMillis();
		inst = this;
		Manager.gen = this;
		Config.generateFile(this, "prices.yml");
		Config.generateFile(this, "messages.yml");
		Config.generateFile(this, "data.yml");
		
		ListOfOres.load(this);
		setupEconomy();
		
		if(!Manager.load(this)){
			log(Level.WARNING, "====================");
			log(Level.WARNING, " ");
			log(Level.WARNING, "Something went wrong with loading");
			log(Level.WARNING, "Disabling plugin because of it!");
			log(Level.WARNING, " ");
			log(Level.WARNING, "====================");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}else{
			log(Level.INFO, "====================");
			log(Level.INFO, " ");
			log(Level.INFO, "Loading enabled!");
			log(Level.INFO, "Active generators: " + Manager.getGens().size());
			log(Level.INFO, "Activating Schedular for repeating task for every 25MS!");
			log(Level.INFO, " ");
			log(Level.INFO, "====================");
		}
		AtomicDouble ad = new AtomicDouble(0);
		
		start(ad);
		new BlockPlace(this);
		new BlockBreak(this);
		new ClickEvent(this);
		new CMDHandler(this);
		new InteractEvent(this);
		new ChunkManager(this);
		
		begin = System.currentTimeMillis() - begin;
		log(Level.INFO, "====================");
		log(Level.INFO, " ");
		log(Level.INFO, "Plugin activated without problems!");
		log(Level.INFO, "Startup took: " + begin + "MS");
		log(Level.INFO, " ");
		log(Level.INFO, "====================");
	}
	
	@Override
	public void onDisable() {
		File file = new File(this.getDataFolder(), "data.yml");
		YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
		
		c.set("Generators", null);
		try {
			c.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Integer i = 0;
		for(Manager m : Manager.getGens()){
			m.Save(i);
			i++;
		}
	}
	
    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
    
	public void log(Level l, String msg){
		getLogger().log(l, msg);
	}
	
	public void start(AtomicDouble ad){
		taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run() {
				if(!Manager.getGens().isEmpty()){
					Boolean canSave = ad.doubleValue() % 2400 == 0;
					if(canSave){
						File file = new File(getDataFolder(), "data.yml");
						YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
						c.set("Generators", null);
						try{
							c.save(file);	
						}catch(IOException e){
							e.printStackTrace();
						}
					}
					Integer i = 0;
					List<Manager> remove = new ArrayList<>();
					for(Manager m : Manager.getGens()){
						Boolean canRun = ad.doubleValue() % m.getTime() == 0 || ad.doubleValue() % m.getTime() == 0.0;
						if(canRun){
							Chest chest = null;
							try{
								Block c = m.getLocation().getWorld().getBlockAt(m.getLocation());
								chest = (Chest)c.getState();
								chest.getInventory().addItem(m.getItemStack());
							}catch(ClassCastException e){
								remove.add(m);
								m.getLocation().getWorld().dropItemNaturally(m.getLocation().add(0, +1, 0), m.getItemStack());	
								continue;
							}
						}
						if(canSave){
							m.Save(i);
							i++;
						}
					}
					if(!remove.isEmpty()){
						Manager.getGens().removeAll(remove);
						remove.clear();
						i = 0;
					}
				}
				ad.addAndGet(5);
			}
		}, 0L, 100L);
	}
}
