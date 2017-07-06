package me.BartVV.Generator.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.BartVV.Generator.Config;

public class CMDHandler extends Config implements CommandExecutor {

	public CMDHandler(JavaPlugin jp) {
		jp.getCommand("generator").setExecutor(this);
		jp.getCommand("giveGen").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String cmdLabel, String[] args) {

		if (cmd.getName().equalsIgnoreCase("generator") && cs instanceof Player) {
			Player p = (Player) cs;
			// generator stone 0.25 5
			if (p.hasPermission("ItemGen.get")) {
				if (args.length > 2) {
					p.sendMessage(wrongUsage);
					return true;
				}
				ItemStack mat = null;
				try{
					if(args.length == 2){
						mat = getItemStack(args[0] + "_" + args[1]);
					}else if(args.length == 1){
						mat = getItemStack(args[0]);
					}
					
					if(mat == null){
						p.sendMessage(noMaterial);	
						return true;
					}
				}catch(Exception e){
					p.sendMessage(noMaterial);
					return true;
				}

				ItemStack is = makeItemStack(generatorMaterial, mat.getType().toString().replace("_", " "), "Material: " + mat.getType().toString().toLowerCase(), "Level: 1");
				if (is != null) {
					p.getInventory().addItem(is);
					p.sendMessage(generatorGive);
					return true;
				}else{
					p.sendMessage(noMaterial);
				}
			} else {
				p.sendMessage(noPermission);
				return true;
			}
		}else if (cmd.getName().equalsIgnoreCase("giveGen") & !(cs instanceof Player)){
			ItemStack mat = null;
			try{
				if(args.length == 3){
					mat = getItemStack(args[0] + "_" + args[1]);
				}else if(args.length == 2){
					
					mat = getItemStack(args[0]);
				}
				
				if(mat == null){
					cs.sendMessage(noMaterial);	
					return true;
				}
			}catch(Exception e){
				cs.sendMessage(noMaterial);
				return true;
			}
			ItemStack is = makeItemStack(generatorMaterial, mat.getType().toString().replace("_", " "), "Material: " + mat.getType().toString().toLowerCase(), "Level: 1");
			Player target = null;
			try{
				if(args.length == 3){
					target = Bukkit.getPlayer(args[2]);
				}else if (args.length == 2){
					target = Bukkit.getPlayer(args[1]);
				}
				
				if(target == null){
					cs.sendMessage(wrongUsage);
					return true;
				}
			}catch(Exception e){
				cs.sendMessage(wrongUsage);
				return true;
			}
			if(target != null){
				target.getInventory().addItem(is);
				target.sendMessage(generatorGive);
				return true;
			}
		}
		
		return false;
	}

	private ItemStack makeItemStack(Material mat, String arg, String... lore) {
		ItemStack is = new ItemStack(mat, 1);

		List<String> l = new ArrayList<>();
		for (String x : lore) {
			l.add(x);
		}

		ItemMeta im = is.getItemMeta();
		String x = generatorName.replace("{ore}", arg.toLowerCase().replace("_", " "));
		if(x.contains("ink sack")){
			x = x.replace("ink sack", "Lapis");
		}
		im.setDisplayName(x);
		im.setDisplayName(im.getDisplayName().replace(" ore", ""));
		im.setDisplayName(im.getDisplayName().substring(2, 3).toUpperCase() + im.getDisplayName().substring(3, im.getDisplayName().length()));
		im.setDisplayName("§a" + im.getDisplayName());
		im.setLore(l);
		is.setItemMeta(im);

		return is;
	}
}
