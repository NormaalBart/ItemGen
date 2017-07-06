package me.BartVV.Generator.Listener;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import me.BartVV.Generator.Config;
import me.BartVV.Generator.Generator;

public class ListOfOres extends Config{
	
	private static HashMap<Material, ListOfOres> ores = new HashMap<>();
	
	private HashMap<Integer, Double> prices;

	private Material mat;

	private HashMap<Integer, Double> time;

	private ItemStack ItemStack;
	
	public ListOfOres(Material mat, ItemStack is, HashMap<Integer, Double> price, HashMap<Integer, Double> time){
		this.prices = price;
		this.mat = mat;
		this.setTime(time);
		this.setItemStack(is);
		ores.put(mat, this);
	}
	
	public HashMap<Integer, Double> getPrices(){
		return this.prices;
	}
	
	public Material getMaterial(){
		return this.mat;
	}
	
	public static ListOfOres getOre(Material mat){
		return ores.get(mat);
	}
	
	public static void load(Generator gen){
		File file = new File(gen.getDataFolder(), "prices.yml");
		YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
		
		for(String str : c.getConfigurationSection("Prices").getKeys(false)){
			if(str != null){
				Material mat = getItemStack(str).getType();
				if(mat != null){
					String s = "Prices." + str + ".";
					ItemStack is = getItemStack(c.getString(s + "get"));
					Integer amount = c.getInt(s + "Amount");
					is.setAmount(amount);
					HashMap<Integer, Double> price = new HashMap<>();
					HashMap<Integer, Double> interval = new HashMap<>();
					
					for(String l : c.getConfigurationSection(s + "prices").getKeys(false)){
						String x = "Prices." + str + ".prices." + l + ".";
						price.put(Integer.parseInt(l), c.getDouble(x + "price"));
						interval.put(Integer.parseInt(l), c.getDouble(x + "interval"));
					}
					new ListOfOres(mat, is, price, interval);
				}else{
					gen.log(Level.WARNING, "===================");
					gen.log(Level.WARNING,  "  ");
					gen.log(Level.WARNING, "Failed to load the Section: " + str + " Because it isn't a material!");
					gen.log(Level.WARNING,  "  ");
					gen.log(Level.WARNING, "===================");
					continue;
				}	
			}
		}
	}

	/**
	 * @return the itemStack
	 */
	public ItemStack getItemStack() {
		return ItemStack;
	}

	/**
	 * @param itemStack the itemStack to set
	 */
	public void setItemStack(ItemStack itemStack) {
		ItemStack = itemStack;
	}

	/**
	 * @return the time
	 */
	public HashMap<Integer, Double> getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(HashMap<Integer, Double> time) {
		this.time = time;
	}

}
