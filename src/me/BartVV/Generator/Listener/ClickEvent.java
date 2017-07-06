package me.BartVV.Generator.Listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;

import me.BartVV.Generator.Config;
import me.BartVV.Generator.Generator;
import me.BartVV.Generator.Manager;

public class ClickEvent extends Config implements Listener{
	
	public ClickEvent(Plugin pl){
		pl.getServer().getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void on(InventoryClickEvent e){
		try{
			Inventory clicked = e.getClickedInventory();
			if(e.getWhoClicked() instanceof Player){
				Player p = (Player)e.getWhoClicked();
				if(clicked.getTitle().contains("Generator")){
					if(clicked.getSize() == 9){
						if(e.getSlot() == 4){
							e.setCancelled(true);
							if(getTargetBlock(p, 10).getType() == Material.CHEST){
								Double money;
								try{
									money = Double.parseDouble(clicked.getItem(4).getItemMeta().getLore().get(4).split("Cost: §a")[1]);
								}catch(NumberFormatException nfe){
									p.sendMessage(cantLevel);
									p.closeInventory();
									return;
								}
								if(Generator.economy.getBalance(p) >= money){
									Manager m = Manager.getGen(getTargetBlock(p, 10).getLocation());
									if(m == null){
										p.sendMessage("§cError, please try again");
										p.closeInventory();
										return;
									}
									if(m.canLevel()){
										m.setLevel(m.getLevel() + 1);
										p.sendMessage(generatorUpgrade.replace("{LEVEL}", "" + (m.getLevel())));
										Generator.economy.withdrawPlayer(p, money);
										p.closeInventory();
										return;
									}else{
										p.closeInventory();
										p.sendMessage(cantLevel);
										return;
									}
									
								}else{
									p.closeInventory();
									p.sendMessage(noMoney);
									return;
								}
							}
						}else if(e.getSlot() == 8){
							e.setCancelled(true);
							Block b = getTargetBlock(p, 10);
							if(b == null){
								p.sendMessage("§cSomething went wrong, this issue will be fixxed soon! (CODE: 77)");
								p.sendMessage("§cGive this code to the owner, he will contact the developer in question");
								return;
							}
							if(p.hasPermission("ItemGen.Break")){
								Manager m = Manager.getGen(b.getLocation());
								if(m == null){
									p.sendMessage("§cSomething went wrong, this issue will be fixxed soon! (CODE: 83)");
									p.sendMessage("§cGive this code to the owner, he will contact the developer in question");
									return;
								}
								e.setCancelled(true);
								p.sendMessage(generatorDelete);
								ItemStack is = makeItemStack(generatorMaterial, m.getItemStack().getType().toString(), "Material: " + m.from().toString().toLowerCase(), "Level: " + m.getLevel());
								b.getLocation().getWorld().dropItem(b.getLocation(), is);
								b.setType(Material.AIR);
								m.delete();
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
								}
							}
							p.closeInventory();
						}
					}
				}	
			}
		}catch(Exception exc){}
		
	}
	
    private final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        if(lastBlock.getType() != Material.CHEST){
        	return null;
        }
        return lastBlock;
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
