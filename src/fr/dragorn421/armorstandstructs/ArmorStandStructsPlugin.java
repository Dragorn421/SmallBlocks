package fr.dragorn421.armorstandstructs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class ArmorStandStructsPlugin extends JavaPlugin// implements Listener
{

	static private ArmorStandStructsPlugin instance;

	final private Map<Integer, Struct> structs = new HashMap<>();

	@Override
	public void onEnable()
	{
		ArmorStandStructsPlugin.instance = this;
		SelectionToLocationConverter.getNew();
		//Bukkit.getPluginManager().registerEvents(this, this);
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
		switch(args[0])
		{
		case "convert":
			final Struct struct = new Struct(sel.getMinimumPoint(), sel.getMaximumPoint());
			this.structs.put(struct.getId(), struct);
			struct.toArmorStands(sel.getMinimumPoint());
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
		}
		return true;
	}

	static public ArmorStandStructsPlugin get()
	{
		return ArmorStandStructsPlugin.instance;
	}

}
