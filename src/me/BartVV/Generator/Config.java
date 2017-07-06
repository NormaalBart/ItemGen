package me.BartVV.Generator;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class Config {
	
	public static String generatorName = "§aGenerator";
	public static String generatorMade = "§aGenerator is succesfully made!";
	public static String generatorDelete = "§cGenerator is deleted!";
	public static String wrongUsage = "§cWrong usage: §a/generator (Material) (Time) (Amount)";
	public static String noMaterial = "§cMaterial isn't found!";
	public static String generatorGive = "§aYou got a generator in your inventory!";
	public static String noPermission = "§cYou don't have the permissions to do that!";
	public static String format = "§aLevel {LEVEL} {ORE} generator!";
	public static String generatorUpgrade = "Upgraded";
	public static String noMoney = "Not enough money";
	public static String cantLevel = "Can't level!";
	
	public static Material generatorMaterial = Material.CHEST;

	public static ItemStack getItemStack(String str){
		ItemStack is = null;
		
		str = str.toLowerCase();
		if(str.equalsIgnoreCase("log2")){
			is = new ItemStack(Material.LOG, 1, (byte)1);
		}else if (str.equalsIgnoreCase("log3")){
			is = new ItemStack(Material.LOG, 1, (byte)2);
		}else if (str.equalsIgnoreCase("log4")){
			is = new ItemStack(Material.LOG, 1, (byte)1);
		}else if (str.equalsIgnoreCase("log5")){
			is = new ItemStack(Material.LOG_2);
		}else if (str.equalsIgnoreCase("log6")){
			is = new ItemStack(Material.LOG_2, 1, (byte)1);
		}else if (str.equalsIgnoreCase("lapis") || str.equalsIgnoreCase("lapis_lapzuli")){
			is = new ItemStack(Material.INK_SACK, 1, (byte)4);
		}else if(str.equalsIgnoreCase("oak_wood")){
			is = new ItemStack(Material.LOG);
		}else if(str.equalsIgnoreCase("spruce_wood")){
			is = new ItemStack(Material.LOG, 1, (byte)1);
		}else if(str.equalsIgnoreCase("birch_wood")){
			is = new ItemStack(Material.LOG, 1, (byte)2);
		}else if(str.equalsIgnoreCase("jungle_wood")){
			is = new ItemStack(Material.LOG, 1, (byte)3);
		}else if(str.equalsIgnoreCase("acacia_wood")){
			is = new ItemStack(Material.LOG_2);
		}else if(str.equalsIgnoreCase("dark_oak_wood")){
			is = new ItemStack(Material.LOG_2, 1, (byte)1);
		}else{
			is = new ItemStack(Material.matchMaterial(str));
		}
		return is;
	}
	
	public boolean isGenerator(Location loc){
		for(Manager m : Manager.getGens()){
			Location locblock = m.getLocation();
			if(locblock.getBlockX() == loc.getBlockX() && locblock.getBlockY() == loc.getBlockY() && locblock.getBlockZ() == loc.getBlockZ()){
				return true;
			}
		}
		return false;
	}
	
	public static void generateFile(Generator gen, String filename){
		File file = null;
		if(filename.endsWith(".yml")){
			file = new File(gen.getDataFolder(), filename);
		}else{
			file = new File(gen.getDataFolder(), filename + ".yml");	
		}
		if(!gen.getDataFolder().exists()){
			gen.getDataFolder().mkdirs();
		}
		
		if(!file.exists()){
			gen.saveResource(file.getName(), true);
			gen.getLogger().info("Succesfully made " + file.getName() + "!");
		}
		
		if(file.getName().equalsIgnoreCase("messages.yml")){
			loadMessages(file);
		}
	}
	
	private static void loadMessages(File file){
		YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
		
		generatorName = c.getString("generatorName").replace("&", "§");
		generatorMade = c.getString("generatorMade").replace("&", "§");
		generatorDelete = c.getString("generatorDelete").replace("&", "§");
		wrongUsage = c.getString("wrongUsage").replace("&", "§");
		noMaterial =  c.getString("noMaterial").replace("&", "§");
		generatorGive = c.getString("generatorGive").replace("&", "§");
		noPermission = c.getString("noPermission").replace("&", "§");
		format = c.getString("format").replace("&", "§");
		generatorUpgrade = c.getString("generatorUpgrade").replace("&", "§");
		noMoney = c.getString("noMoney").replace("&", "§");
		cantLevel = c.getString("cantLevel").replace("&", "§");
	}
}
