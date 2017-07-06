package me.BartVV.Generator.Listener;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.BartVV.Generator.Config;
import me.BartVV.Generator.Manager;

public class BlockPlace extends Config implements Listener{
	
	public BlockPlace(Plugin pl){
		pl.getServer().getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(BlockPlaceEvent e){
		Block b = e.getBlock();
		ItemStack is = e.getItemInHand();
		Player p = e.getPlayer();
		
		if(p.hasPermission("ItemGen.place")){
			
			if(b != null){
				
				if(is.getType() == generatorMaterial){
					
					if(is.hasItemMeta()){
						
						if(is.getItemMeta().hasDisplayName()){
							
							if(is.getItemMeta().hasLore()){
								
								List<String> lore = is.getItemMeta().getLore();
								Location loc = b.getLocation();
								Material mat = getItemStack(lore.get(0).split("Material: ")[1]).getType();
								Integer level = Integer.parseInt(lore.get(1).split("Level: ")[1]);
								if(ListOfOres.getOre(mat) == null){
									p.sendMessage(noMaterial);
									return;
								}
								
								new Manager(loc, mat, level);
								p.sendMessage(generatorMade);
								return;
							}
						}
					}	
				}
			}
		}
	}

}
