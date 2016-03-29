package fr.dragorn421.armorstandstructs;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class SelectionToLocationConverter
{

	static private SelectionToLocationConverter instance;

	private boolean enabled = false;

	private SelectionToLocationConverter() {}

	public boolean tryEnable()
	{
		try {
			Class.forName("com.sk89q.worldedit.bukkit.WorldEditPlugin");
		} catch(final ClassNotFoundException e) {
			this.enabled = false;
			return false;
		}
		this.enabled = true;
		return true;
	}

	public boolean hasCompleteSelection(Player p)
	{
		this.verify();
		Selection sel = this.getSelection(p);
		if(sel == null)
			return false;
		return sel.getMinimumPoint() != null && sel.getMaximumPoint() != null;
	}

	public Location getFirstPoint(Player p)
	{
		this.verify();
		Selection sel = this.getSelection(p);
		if(sel == null)
			return null;
		return sel.getMinimumPoint();
	}

	public Location getSecondPoint(Player p)
	{
		this.verify();
		Selection sel = this.getSelection(p);
		if(sel == null)
			return null;
		return sel.getMaximumPoint();
	}

	public Selection getSelection(Player p)
	{
		return JavaPlugin.getPlugin(WorldEditPlugin.class).getSelection(p);
	}

	public boolean isEnabled()
	{
		return this.enabled;
	}

	private void verify()
	{
		if(!this.enabled)
			throw new IllegalStateException("this isn't enabled.");
	}

	public static SelectionToLocationConverter get()
	{
		if(SelectionToLocationConverter.instance == null)
		{
			SelectionToLocationConverter.instance = new SelectionToLocationConverter();
			SelectionToLocationConverter.instance.tryEnable();
		}
		return SelectionToLocationConverter.instance;
	}

	public static SelectionToLocationConverter getNew()
	{
		SelectionToLocationConverter.instance = new SelectionToLocationConverter();
		SelectionToLocationConverter.instance.tryEnable();
		return SelectionToLocationConverter.instance;
	}

}
