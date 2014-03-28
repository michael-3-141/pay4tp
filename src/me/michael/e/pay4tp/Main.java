package me.michael.e.pay4tp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.michael.e.pay4tp.command.TpCommand;
import me.michael.e.pay4tp.command.TpPoints;

import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	List<TpCommand> commands = new ArrayList<TpCommand>();
	TpPoints tppoints = new TpPoints(0);
	
	public File COMMANDS_FILE;
	public File TPPOINTS_FILE;
	
	private int tppointsPerIron = 5;
	private boolean usePublicTppoints = false;
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		getLogger().info("Plugin enabled.");
		
		saveDefaultConfig();
		tppointsPerIron = getConfig().getInt("tppointsPerIron");
		usePublicTppoints = getConfig().getBoolean("publicTppoints");
		
		COMMANDS_FILE = new File(getDataFolder() + "/commands.json");
		TPPOINTS_FILE = new File(getDataFolder() + "/tppoints.json"); 
		if(!COMMANDS_FILE.exists())
		{
			try {
				if(!COMMANDS_FILE.getParentFile().exists())COMMANDS_FILE.getParentFile().mkdirs();
				COMMANDS_FILE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(!TPPOINTS_FILE.exists())
		{
			try {
				if(!TPPOINTS_FILE.getParentFile().exists())TPPOINTS_FILE.getParentFile().mkdirs();
				TPPOINTS_FILE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Gson gson = new Gson();
		try {
			String jsonTppoints = FileUtils.readFileToString(TPPOINTS_FILE);
			tppoints = gson.fromJson(jsonTppoints, TpPoints.class);
			String jsonCommands = FileUtils.readFileToString(COMMANDS_FILE);
			TpCommand[] savedCommands = gson.fromJson(jsonCommands, TpCommand[].class);
			if(savedCommands != null)
			{
				commands = new ArrayList<TpCommand>(Arrays.asList(savedCommands));
			}
		} catch (FileNotFoundException e) {
			getLogger().warning("Commands file not found.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(tppoints == null)
		{
			tppoints = new TpPoints(0);
		}
		if(commands == null)
		{
			commands = new ArrayList<TpCommand>();
		}
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Plugin disabled.");
		
		super.onDisable();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			if(label.equalsIgnoreCase("addPayedTp"))
			{
				if(!p.hasPermission("pay4tp.addTps")){p.sendMessage(ChatColor.DARK_BLUE + "You do not have permission to use this command.");return true;}
				if(args.length == 4)
				{
					commands.add(new TpCommand(args[0], p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ(), Integer.parseInt(args[1]), new int[]{Integer.parseInt(args[2]), Integer.parseInt(args[3])}));
					p.sendMessage(ChatColor.DARK_BLUE + "Your command: \"" + args[0] + "\",was created and will point to: " + p.getLocation().getBlockX() + "," + p.getLocation().getBlockY() + "," + p.getLocation().getBlockZ() + ".");
					
					Gson gson = new Gson();
					String jsonCommands = gson.toJson(commands.toArray(new TpCommand[]{}));
					try {
						PrintWriter pw = new PrintWriter(COMMANDS_FILE);
						pw.write(jsonCommands);
						pw.close();
					} catch (FileNotFoundException e) {
						getLogger().warning("Commands file not found.");
					}
					return true;
				}
			}
			
			else if(label.equalsIgnoreCase("tp"))
			{
				if(!p.hasPermission("pay4tp.tp")){p.sendMessage(ChatColor.DARK_BLUE + "You do not have permission to use this command.");return true;}
				if(args.length == 1)
				{
					for(Iterator<TpCommand> i = commands.iterator(); i.hasNext();)
					{
						TpCommand item = i.next();
						
						if(item.getName().equalsIgnoreCase(args[0]))
						{
							Location l = new Location(p.getWorld(), item.getX(), item.getY(), item.getZ());
							int charge = 0;
							if(l.distance(p.getLocation()) > item.getPaymentStartBlocks())
							{
								Double d = l.distance(p.getLocation());
								try{
									charge = (d.intValue() / item.getPointsPerBlocks()[0]) * item.getPointsPerBlocks()[1];
								}
								catch(ArithmeticException e)
								{
									charge = 0;
								}
								if(!tppoints.charge(charge, p.getName(), usePublicTppoints))
								{
									p.sendMessage(ChatColor.DARK_BLUE + "You do not have enough points to teleport this distance.");
									return true;
								}
							}
							p.teleport(l);
							p.sendMessage(ChatColor.DARK_BLUE + "You have been teleported for " + charge + " tp points.");
							Gson gson = new Gson();
							String jsonPoints = gson .toJson(tppoints);
							try {
								PrintWriter pw = new PrintWriter(TPPOINTS_FILE);
								pw.write(jsonPoints);
								pw.close();
							} catch (FileNotFoundException e) {
								getLogger().warning("Points file not found.");
							}
							return true;
						}
					}
				}
			}
			else if(label.equalsIgnoreCase("tppoints"))
			{
				if(!p.hasPermission("pay4tp.tppoints")){p.sendMessage(ChatColor.DARK_BLUE + "You do not have permission to use this command.");return true;}
				p.sendMessage(ChatColor.DARK_BLUE + "You have " + tppoints.getPoints(p.getName(), usePublicTppoints) + " tp points.");
				return true;
			}
			else if(label.equalsIgnoreCase("buypoints"))
			{
				if(!p.hasPermission("pay4tp.buypoints")){p.sendMessage(ChatColor.DARK_BLUE + "You do not have permission to use this command.");return true;}
				if(args.length == 1)
				{
					if(p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), Integer.parseInt(args[0])))
					{
						ItemStack held = p.getInventory().getItemInHand();
						if(held.getType() != Material.IRON_INGOT)
						{
							p.sendMessage(ChatColor.DARK_BLUE + "You must hold the iron stack you want to buy with in your hand.");
						}
						else
						{
							held.setAmount(held.getAmount() - Integer.parseInt(args[0]));
							tppoints.addPoints(Integer.parseInt(args[0]) * tppointsPerIron, p.getName(), usePublicTppoints);
							p.sendMessage(ChatColor.DARK_BLUE + "You have been given " + Integer.parseInt(args[0]) * 5 + " tp points.");
							Gson gson = new Gson();
							String jsonPoints = gson .toJson(tppoints);
							try {
								PrintWriter pw = new PrintWriter(TPPOINTS_FILE);
								pw.write(jsonPoints);
								pw.close();
							} catch (FileNotFoundException e) {
								getLogger().warning("Points file not found.");
							}
						}
					}
					else
					{
						p.sendMessage(ChatColor.DARK_BLUE + "You dont have enough iron for that.");
					}
					return true;
				}
			}
		}
		
		return false;
	}

}
