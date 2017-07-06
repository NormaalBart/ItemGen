package me.BartVV.Generator.Listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;

import me.BartVV.Generator.Config;
import me.BartVV.Generator.Manager;

public class BlockBreak extends Config implements Listener{
	
	public BlockBreak(Plugin pl){
		pl.getServer().getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(BlockBreakEvent e){
		Block b = e.getBlock();
		Player p = e.getPlayer();
		Island is = ASkyBlockAPI.getInstance().getIslandAt(b.getLocation());
		if(!e.isCancelled()){
			if(isGenerator(b.getLocation())){
				if(is != null){
					if(is.getMembers().contains(p.getUniqueId())){
						if(p.hasPermission("ItemGen.Break")){
							Manager m = Manager.getGen(b.getLocation());
							if(m == null) return;
							m.delete();
							e.setCancelled(true);
							p.sendMessage(generatorDelete);
							ItemStack is1 = makeItemStack(generatorMaterial, m.getItemStack().getType().toString(), "Material: " + m.from().toString().toLowerCase(), "Level: " + m.getLevel());
							b.getLocation().getWorld().dropItem(b.getLocation(), is1);
							b.setType(Material.AIR);
						}else{
							if(p.hasPermission("ItemGen.Break.Admin")){
								Manager m = Manager.getGen(b.getLocation());
								if(m == null) return;
								m.delete();
								e.setCancelled(true);
								p.sendMessage(generatorDelete);
								ItemStack is1 = makeItemStack(generatorMaterial, m.getItemStack().getType().toString(), "Material: " + m.from().toString().toLowerCase(), "Level: " + m.getLevel());
								b.getLocation().getWorld().dropItem(b.getLocation(), is1);
								b.setType(Material.AIR);
							}else{
								e.setCancelled(true);
							}
						}
					}else{
						if(p.hasPermission("ItemGen.Break.Admin")){
							Manager m = Manager.getGen(b.getLocation());
							if(m == null) return;
							m.delete();
							e.setCancelled(true);
							p.sendMessage(generatorDelete);
							ItemStack is1 = makeItemStack(generatorMaterial, m.getItemStack().getType().toString(), "Material: " + m.from().toString().toLowerCase(), "Level: " + m.getLevel());
							b.getLocation().getWorld().dropItem(b.getLocation(), is1);
							b.setType(Material.AIR);
						}else{
							e.setCancelled(true);
						}
					}
				}else{
					if(p.hasPermission("ItemGen.Break.Admin")){
						Manager m = Manager.getGen(b.getLocation());
						if(m == null) return;
						m.delete();
						e.setCancelled(true);
						p.sendMessage(generatorDelete);
						ItemStack is1 = makeItemStack(generatorMaterial, m.getItemStack().getType().toString(), "Material: " + m.from().toString().toLowerCase(), "Level: " + m.getLevel());
						b.getLocation().getWorld().dropItem(b.getLocation(), is1);
						b.setType(Material.AIR);
					}else{
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	private ItemStack makeItemStack(Material mat, String arg, String... lore) {
		ItemStack is = new ItemStack(mat, 1);

		List<String> l = new ArrayList<>();
		for (String x : lore) {
			l.add(x);
		}

		ItemMeta im = is.getItemMeta();
		im.setDisplayName(generatorName.replace("{ore}", arg.toLowerCase().replace("_", " ")));
		im.setDisplayName(im.getDisplayName().replace(" ore", ""));
		im.setDisplayName(im.getDisplayName().replace("ink sack", "lapis"));
		im.setDisplayName(im.getDisplayName().substring(2, 3).toUpperCase() + im.getDisplayName().substring(3, im.getDisplayName().length()));
		im.setDisplayName("§a" + im.getDisplayName());
		im.setDisplayName(im.getDisplayName().replace(" ingot", ""));
		im.setLore(l);
		is.setItemMeta(im);

		return is;
	}

}
