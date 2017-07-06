package me.BartVV.Generator.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;

import me.BartVV.Generator.Config;
import me.BartVV.Generator.Manager;

public class InteractEvent extends Config implements Listener {

	private Inventory inv;

	public InteractEvent(Plugin pl) {
		pl.getServer().getPluginManager().registerEvents(this, pl);

		inv = Bukkit.createInventory(null, 9, generatorName);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerInteractEvent e) {
		Block b = e.getClickedBlock();
		if (b != null) {
			if (b.getType() == Material.CHEST) {
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					return;
				} else {
					if(isGenerator(b.getLocation())){
						Island is = ASkyBlockAPI.getInstance().getIslandAt(b.getLocation());
						if(is != null){
							if(is.getMembers().contains(e.getPlayer().getUniqueId())){
								if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
									e.setCancelled(true);
									openInv(e.getPlayer(), b.getLocation());
									return;
								}	
							}else{
								if(e.getPlayer().hasPermission("ItemGen.admin")){
									e.setCancelled(true);
									openInv(e.getPlayer(), b.getLocation());
									return;
								}
							}
						}	
					}
				}
			}
		}
	}

	private ItemStack makeItemStack(Material mat, Byte b, Material to, String... lore) {
		ItemStack is = new ItemStack(mat, 1, b);

		List<String> l = new ArrayList<>();
		for (String x : lore) {
			l.add(x);
		}

		ItemMeta im = is.getItemMeta();
		if(to == null){
			im.setDisplayName("§cDelete this generator");
		}else{
			im.setDisplayName("§a" + to.toString().toLowerCase().replace("_", " ") + " generator");	
			im.setDisplayName(im.getDisplayName().replace("ink sack", "Lapis"));
			im.setDisplayName(im.getDisplayName().substring(2, 3).toUpperCase() + im.getDisplayName().substring(3, im.getDisplayName().length()));
			im.setDisplayName("§a" + im.getDisplayName());
		}
		im.setLore(l);
		is.setItemMeta(im);

		return is;
	}

	private void openInv(Player p, Location loc) {
		try{
			String cost = "" + (ListOfOres.getOre(Manager.getGen(loc).from()).getPrices().get(Manager.getGen(loc).getLevel() + 1));
			if(ListOfOres.getOre(Manager.getGen(loc).from()).getPrices().get(Manager.getGen(loc).getLevel() +1) == null){
				cost = "§cMax Level";
			}
			if(cost == null || cost == "" || cost == "null"){
				cost = "§cMax Level";
			}
			Material from = Manager.getGen(loc).getItemStack().getType();
			inv.setItem(4, makeItemStack(Material.WOOL, (byte) 5, from, " ", "§7Click here to",
					"§7upgrade your generator", "§7to level §a" + (Manager.getGen(loc).getLevel() + 1), 
					"§7Cost: §a" + cost));
			inv.setItem(8, makeItemStack(Material.WOOL, (byte)14, null,"", "§cDelete this generator", "§cCannot be rollbacked!"));
			p.openInventory(inv);
			
			// §adiamond ore generator
			String str = inv.getTitle().replace("{ore}", Manager.getGen(loc).from().toString().toLowerCase().replace("_", " "));
			str = str.replace("ink sack", "Lapis");
			
			update(p, (str.substring(0,2) +str.substring(2,3).toUpperCase() + str.substring(3, str.length())));
			
		}catch(NullPointerException e){ }
	}
	
	
	
	
	
	private Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(23) + "." + nmsClassName);
	}

	private void update(final Player p, String title) {
		Object handle;
		try {
			handle = p.getClass().getMethod("getHandle").invoke(p);
			Object message = getNmsClass("ChatMessage").getConstructor(String.class, Object[].class).newInstance(title, new Object[0]);
			Object container = handle.getClass().getField("activeContainer").get(handle);
			Object windowId = container.getClass().getField("windowId").get(container);
			Object packet = getNmsClass("PacketPlayOutOpenWindow").getConstructor(Integer.TYPE, String.class, getNmsClass("IChatBaseComponent"), Integer.TYPE).newInstance(windowId, "minecraft:chest", message, p.getOpenInventory().getTopInventory().getSize());
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket",getNmsClass("Packet")).invoke(playerConnection, packet);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | InstantiationException | ClassNotFoundException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		p.updateInventory();
	}

}
