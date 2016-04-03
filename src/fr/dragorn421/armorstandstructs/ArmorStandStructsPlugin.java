package fr.dragorn421.armorstandstructs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class ArmorStandStructsPlugin extends JavaPlugin// implements Listener
{

	final static public String MOVING_STRUCT_METADATA_KEY = "moving_struct_armorstandstructs";

	static private ArmorStandStructsPlugin instance;

	final private Map<Integer, Struct> structs = new HashMap<>();

	@Override
	public void onEnable()
	{
		ArmorStandStructsPlugin.instance = this;
		SelectionToLocationConverter.getNew();
		Bukkit.getPluginManager().registerEvents(new ListenerImpl(), this);
		super.getLogger().info(super.getName() + " enabled!");
	}

	@Override
	public void onDisable()
	{
		super.getLogger().info(super.getName() + " disabled!");
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Players only");
			return true;
		}
		if(args.length == 0)
			return false;
		final Player p = (Player) sender;
		Selection sel = SelectionToLocationConverter.get().getSelection(p);
		if(sel == null || sel.getMinimumPoint() == null || sel.getMaximumPoint() == null)
		{
			p.sendMessage("Make a selection first");
			return true;
		}
		final Struct struct;
		final int id;
		switch(args[0])
		{
		case "convert":
			final boolean hollow = args.length==1?false:Boolean.parseBoolean(args[1]);
			struct = new Struct(sel.getMinimumPoint(), sel.getMaximumPoint());
			this.structs.put(struct.getId(), struct);
			struct.toArmorStands(sel.getMinimumPoint(), hollow);
			p.sendMessage("Created struct #" + struct.getId());
			break;
		case "minimify":
			Util.showSelection(sel, p, Material.GLASS);
			sel = Util.minimifySelection(sel);
			if(sel == null)
				p.sendMessage("Your selection is empty");
			else
				Util.showSelection(sel, p, Material.BRICK);
			break;
		case "move":
			if(args.length == 1)
			{
				struct = Util.getMetadata(p, ArmorStandStructsPlugin.MOVING_STRUCT_METADATA_KEY, Struct.class);
				if(struct == null)
				{
					p.sendMessage("No struct id given");
					return false;
				}
				else
				{
					Util.setMetadata(p, ArmorStandStructsPlugin.MOVING_STRUCT_METADATA_KEY, null);
					p.sendMessage("No longer moving struct #" + Integer.toString(struct.getId()));
					return true;
				}
			}
			try {
				id = Integer.parseInt(args[1]);
			} catch(final NumberFormatException e) {
				p.sendMessage(args[1] + " is not a valid number");
				return false;
			}
			struct = this.structs.get(id);
			if(struct == null)
			{
				p.sendMessage("Struct #" + Integer.toString(id) + " doesn't exist");
				return false;
			}
			Util.setMetadata(p, ArmorStandStructsPlugin.MOVING_STRUCT_METADATA_KEY, struct);
			p.sendMessage("Moving struct #" + Integer.toString(id));
			break;
		case "remove":
			if(args.length == 1)
			{
				p.sendMessage("No struct id given");
				return false;
			}
			try {
				id = Integer.parseInt(args[1]);
			} catch(final NumberFormatException e) {
				p.sendMessage(args[1] + " is not a valid number");
				return false;
			}
			struct = this.structs.get(id);
			if(struct == null)
			{
				p.sendMessage("Struct #" + Integer.toString(id) + " doesn't exist");
				return false;
			}
			struct.remove();
			this.structs.remove(id);
			p.sendMessage("Removed struct #" + Integer.toString(id));
			break;
		}
		return true;
	}

	static public ArmorStandStructsPlugin get()
	{
		return ArmorStandStructsPlugin.instance;
	}

}
