package me.BartVV.Generator.Listener;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

import me.BartVV.Generator.Config;
import me.BartVV.Generator.Manager;

public class ChunkManager extends Config implements Listener{
	
	public ChunkManager(Plugin pl){
		pl.getServer().getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler
	public void on(ChunkUnloadEvent e){
		Chunk c = e.getChunk();
		for(Manager m : Manager.getGens()){
			if(m.getLocation().getChunk() == c){
				m.deleteArmorStand();
			}
		}
		
		
	}
	
	@EventHandler
	public void on(ChunkLoadEvent e){
		Chunk c = e.getChunk();
		for(Manager m : Manager.getGens()){
			if(m.getLocation().getChunk() == c){
				m.loadArmorStand();
			}
		}
	}
	
	

}
