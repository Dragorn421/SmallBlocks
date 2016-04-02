package fr.dragorn421.armorstandstructs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class Util
{

	final static public Selection minimifySelection(final Selection selection)
	{
		final World w = selection.getWorld();
		final Location from = selection.getMinimumPoint();
		final Location to = selection.getMaximumPoint();
		int fromX = 0, fromY = 0, fromZ = 0, toX = 0, toY = 0, toZ = 0;
		boolean noBlocks = true;
		for(int x=from.getBlockX();x<=to.getBlockX();x++)
			for(int y=from.getBlockY();y<=to.getBlockY();y++)
				for(int z=from.getBlockZ();z<=to.getBlockZ();z++)
				{
					if(!w.getBlockAt(x, y, z).isEmpty())
					{
						if(noBlocks)
						{
							fromX = x;
							noBlocks = false;
						}
						toX = x;
					}
				}
		int empty = noBlocks?1:0;
		noBlocks = true;
		for(int y=from.getBlockY();y<=to.getBlockY();y++)
			for(int z=from.getBlockZ();z<=to.getBlockZ();z++)
				for(int x=from.getBlockX();x<=to.getBlockX();x++)
				{
					if(!w.getBlockAt(x, y, z).isEmpty())
					{
						if(noBlocks)
						{
							fromY = y;
							noBlocks = false;
						}
						toY = y;
					}
				}
		empty += noBlocks?1:0;
		noBlocks = true;
		for(int z=from.getBlockZ();z<=to.getBlockZ();z++)
			for(int x=from.getBlockX();x<=to.getBlockX();x++)
				for(int y=from.getBlockY();y<=to.getBlockY();y++)
				{
					if(!w.getBlockAt(x, y, z).isEmpty())
					{
						if(noBlocks)
						{
							fromZ = z;
							noBlocks = false;
						}
						toZ = z;
					}
				}
		empty += noBlocks?1:0;
		if(empty == 3)
			return null;
		return new CuboidSelection(selection.getWorld(), new Location(w, fromX, fromY, fromZ), new Location(w, toX, toY, toZ));
	}

	@SuppressWarnings("deprecation")
	final static public void showSelection(final Selection selection, final Player player, final Material block)
	{
		final World w = selection.getWorld();
		final Location from = selection.getMinimumPoint();
		final Location to = selection.getMaximumPoint();
		for(int x=from.getBlockX();x<=to.getBlockX();x++)
			for(int y=from.getBlockY();y<=to.getBlockY();y++)
				for(int z=from.getBlockZ();z<=to.getBlockZ();z++)
					player.sendBlockChange(new Location(w, x, y, z), block, (byte) 0);
	}

	final static public EulerAngle getStairsRotation(final byte data)
	{
		double x = 0, y = 0, z = 0;
		if(data > 3)
			z = Math.PI;
		switch(data % 4)
		{
		case 0:
			y = z==0?Math.PI:0;
			break;
		case 1:
			y = z==0?0:Math.PI;
			break;
		case 2:
			y = -Math.PI/2;
			break;
		case 3:
			y = Math.PI/2;
			break;
		}
		//System.out.println("data " + data + " angle " + Util.angleToString(angle));
		return new EulerAngle(x, y, z);
	}

	final static public boolean isStairs(final Material type)
	{
		switch(type)
		{
		case ACACIA_STAIRS:
		case BIRCH_WOOD_STAIRS:
		case BRICK_STAIRS:
		case COBBLESTONE_STAIRS:
		case DARK_OAK_STAIRS:
		case JUNGLE_WOOD_STAIRS:
		case NETHER_BRICK_STAIRS:
		//case PURPUR_STAIRS:
		case QUARTZ_STAIRS:
		case RED_SANDSTONE_STAIRS:
		case SANDSTONE_STAIRS:
		case SMOOTH_STAIRS:
		case SPRUCE_WOOD_STAIRS:
		case WOOD_STAIRS:
			return true;
		default:
			return false;
		}
	}

	final static public EulerAngle getWoodLogRotation(final byte data)
	{
		double x = 0, y = 0, z = 0;
		if((data & 0b100) != 0)
		{
			//y = Math.PI;TODO what axis is which
			//y = Math.PI/2;
			z = Math.PI/2;
		}
		if((data & 0b1000) != 0)
		{
			//System.out.println("woaw");
			//y = Math.PI/2;
			x = Math.PI/2;
		}
		return new EulerAngle(x, y, z);
	}

	final static public TreeType getWoodLogType(final Material type, final byte data)
	{
		if(type == Material.LOG)
		{
			switch(data % 4)
			{
			case 0:
				return TreeType.TREE;
			case 1:
				return TreeType.REDWOOD;//spruce?
			case 2:
				return TreeType.BIRCH;
			case 3:
				return TreeType.JUNGLE;
			}
		}
		else if(type == Material.LOG_2)
		{
			switch(data % 4)
			{
			case 0:
				return TreeType.ACACIA;
			case 1:
				return TreeType.DARK_OAK;
			}
		}
		return null;//TODO return normal tree?
	}

	final static public boolean isWoodLog(final Material type)
	{
		switch(type)
		{
		case LOG:
		case LOG_2:
			return true;
		default:
			return false;
		}
	}

	final static public String angleToString(final EulerAngle angle)
	{
		return "EulerAngle{x=" + angle.getX() + ",y=" + angle.getY() + ",z=" + angle.getZ() + "}";
	}

}
