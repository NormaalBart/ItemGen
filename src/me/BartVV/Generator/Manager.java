package me.BartVV.Generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import me.BartVV.Generator.Listener.ListOfOres;

public class Manager extends Config {

	private static Set<Manager> gens = new HashSet<>();

	private Location loc;
	private Double time;
	private ItemStack is;
	private Material from;
	private Integer level;
	private ArmorStand armorStand;

	public Manager(Location loc, Material mat, Integer level) {
		this.from = mat;
		this.loc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		this.is = ListOfOres.getOre(mat).getItemStack();
		String type = is.getType().toString().toLowerCase().replace("_", " ");
		if(type.contains("ink sack")){
			type = "Lapis";
		}
		type = type.substring(0,1).toUpperCase() + type.substring(1, type.length()).toLowerCase();
		for(Entity e : this.loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5)){
			if(e instanceof ArmorStand){
				ArmorStand as = (ArmorStand) e;
				if(as.getCustomName().equalsIgnoreCase(format.replace("{LEVEL}", "" + level).replace("{ORE}", type))){
					this.armorStand = as;
				}
			}
		}
		if(armorStand == null){
			this.armorStand = (ArmorStand) loc.getWorld().spawnEntity(
					new Location(loc.getWorld(), loc.getBlockX()+.5, loc.getBlockY(), loc.getBlockZ()+0.5),
					EntityType.ARMOR_STAND);	
		}
		this.armorStand.setVisible(false);
		this.armorStand.setGravity(false);
		this.armorStand.setCustomNameVisible(true);
		this.level = level;
		this.time = ListOfOres.getOre(mat).getTime().get(level);
		
		this.armorStand.setCustomName(
				format.replace("{LEVEL}", "" + this.level).replace("{ORE}", type));
		try {
			this.armorStand.setCollidable(false);
		} catch (NoSuchMethodError e) {
		}
		gens.add(this);
	}

	public Material from(){
		return this.from;
	}
	/**
	 * @return the loc
	 */
	public Location getLocation() {
		return loc;
	}

	/**
	 * @return if it can level up
	 */
	public boolean canLevel() {
		Integer highest = ListOfOres.getOre(from).getPrices().size();
		return (highest+1 != level);
	}

	/**
	 * @return the time
	 */
	public Double getTime() {
		return time;
	}

	/**
	 * @param set
	 *            the time of the interval
	 */
	public void setTime(Double time) {
		this.time = time;
	}

	/**
	 * @return the ItemStack
	 */
	public ItemStack getItemStack() {
		return is;
	}

	/**
	 * @param is
	 *            the ItemStack to set
	 */
	public void setItemStack(ItemStack is) {
		this.is = is;
	}

	/**
	 * Removes the generator
	 */
	public void delete() {
		armorStand.remove();
		armorStand.eject();
		gens.remove(this);
	}

	/**
	 * @return the amount
	 */
	public Integer getAmount() {
		return is.getAmount();
	}

	/**
	 * @param set
	 *            the amount to drop
	 */
	public void setAmount(Integer amount) {
		this.setAmount(amount);
	}

	/**
	 * @return the Generators(Manager)
	 */
	public static Set<Manager> getGens() {
		return gens;
	}

	/**
	 * Saves the generator to the config file
	 * 
	 * @param i
	 */
	public void Save(Integer i) {
		File file = new File(Generator.getPlugin(Generator.class).getDataFolder(), "data.yml");
		YamlConfiguration c = YamlConfiguration.loadConfiguration(file);

		String gen = "Generators." + i + ".";
		c.set(gen + "Location.World", loc.getWorld().getName());
		c.set(gen + "Location.X", loc.getBlockX());
		c.set(gen + "Location.Y", loc.getBlockY());
		c.set(gen + "Location.Z", loc.getBlockZ());
		c.set(gen + "Material", is.getType().toString());
		c.set(gen + "From", from.toString());
		c.set(gen + "Level", level);
		try {
			c.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads all the generators
	 */
	public static boolean load(Generator generator) {
		List<Integer> values = new ArrayList<Integer>();
		File file = new File(Generator.getPlugin(Generator.class).getDataFolder(), "data.yml");
		YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
		try {
			if (c.getConfigurationSection("Generators").getKeys(false) == null) {
				c.set("Generators", null);
				return true;
			}
		} catch (NullPointerException e) {
			return true;
		}
		for (String key : c.getConfigurationSection("Generators").getKeys(false)) {
			int vaule = Integer.parseInt(key);
			values.add(vaule);
		}
		generator.log(Level.INFO, "Value's loaded!");
		
		
		for (Integer i : values) {
			try{
				String gen = "Generators." + i + ".";
				World w = Bukkit.getWorld(c.getString(gen + "Location.World"));
				generator.log(Level.INFO, "  " + w);
				Double x = c.getDouble(gen + "Location.X");
				generator.log(Level.INFO, "  " + x);
				Double y = c.getDouble(gen + "Location.Y");
				generator.log(Level.INFO, "  " + y);
				Double z = c.getDouble(gen + "Location.Z");
				generator.log(Level.INFO, "  " + z);
				Location loc = new Location(w, x, y, z);
				generator.log(Level.INFO, "  " + loc);

				ItemStack itemstack = Config.getItemStack(c.getString(gen + "From"));
				generator.log(Level.INFO, "  " + itemstack.toString());
				Integer level = Integer.parseInt(c.getString(gen + "Level"));
				generator.log(Level.INFO, "  " + level);
				new Manager(loc, itemstack.getType(), level);	
				generator.log(Level.INFO, "Loaded" );
			}catch(Exception e){
				generator.log(Level.INFO, "===================");
				generator.log(Level.INFO, "  ");
				generator.log(Level.INFO, "Generator ID: " + i);
				generator.log(Level.INFO, "Failed to load.");
				generator.log(Level.INFO, "  ");
				generator.log(Level.INFO, "===================");
			}
		}
		return true;
	}

	public static Manager getGen(Location loc) {
		for (Manager m : gens) {
			Location locblock = m.getLocation();
			if (locblock.getBlockX() == loc.getBlockX() && locblock.getBlockY() == loc.getBlockY()
					&& locblock.getBlockZ() == loc.getBlockZ()) {
				return m;
			}
		}
		return null;
	}

	/**
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(Integer level) {
		if (canLevel()) {
			this.level = level;
			this.time = ListOfOres.getOre(from).getTime().get(this.level);
			String type = is.getType().toString().toLowerCase().replace("_", " ");
			if(type.contains("ink sack")){
				type = "Lapis";
			}
			type = type.substring(0,1).toUpperCase() + type.substring(1, type.length()).toLowerCase();
			
			this.armorStand.setCustomName(
					format.replace("{LEVEL}", "" + this.level).replace("{ORE}", type));
		}
	}
}
