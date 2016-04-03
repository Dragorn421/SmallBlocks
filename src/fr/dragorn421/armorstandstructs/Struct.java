package fr.dragorn421.armorstandstructs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.EulerAngle;

public class Struct
{

	static private int increment = 0;

	final private int id;
	final private MaterialData blocks[][][];
	final private List<ArmorStand> armorStands;

	@SuppressWarnings("deprecation")
	public Struct(final Location from, final Location to)
	{
		this.id = Struct.increment++;
		final int	sx = to.getBlockX() - from.getBlockX() + 1,
					sy = to.getBlockY() - from.getBlockY() + 1,
					sz = to.getBlockZ() - from.getBlockZ() + 1;
		this.blocks = new MaterialData[sx][sy][sz];
		final World w = from.getWorld();
		for(int i=0;i<sx;i++)
			for(int j=0;j<sy;j++)
				for(int k=0;k<sz;k++)
				{
					final Block b = w.getBlockAt(from.getBlockX()+i, from.getBlockY()+j, from.getBlockZ()+k);
					this.blocks[i][j][k] = b.isEmpty()?null:new MaterialData(b.getType(), b.getData());
				}
		this.armorStands = new ArrayList<>();
	}

	@SuppressWarnings("deprecation")
	public void toArmorStands(final Location loc)
	{
		loc.setX(loc.getBlockX() - Const.armorStandHeadSideOffset);
		loc.setY(loc.getBlockY() - Const.armorStandHeadHeightOffset);
		loc.setZ(loc.getBlockZ() - Const.armorStandHeadSideOffset);
		final World w = loc.getWorld();
		final Location spawn = loc.clone();
		for(int i=0;i<this.blocks.length;i++)
		{
			for(int j=0;j<this.blocks[i].length;j++)
			{
				for(int k=0;k<this.blocks[i][j].length;k++)
				{
					final MaterialData b = this.blocks[i][j][k];
					if(b != null)
					{
						EulerAngle headPose = null;
						if(Util.isStairs(b.getItemType()))
							headPose = Util.getStairsRotation(b.getData());
						else if(Util.isWoodLog(b.getItemType()))
						{
							headPose = Util.getWoodLogRotation(b.getData());
							b.setData((byte) (b.getData() % 4));
						}
						spawn.setX(loc.getX() + i * Const.armorStandHeadSize);
						spawn.setY(loc.getY() + j * Const.armorStandHeadSize);
						spawn.setZ(loc.getZ() + k * Const.armorStandHeadSize);
						if(headPose != null)
						{
							if(headPose.getZ() == Math.PI/2)
								 spawn.add(-Const.armorStandHeadRotation90OffsetX, Const.armorStandHeadRotation90OffsetY, 0);
							else if(headPose.getZ() == Math.PI)
								 spawn.add(0, Const.armorStandHeadRotation180Offset, 0);
							if(headPose.getX() == Math.PI/2)
								spawn.add(0, Const.armorStandHeadRotation90OffsetY, -Const.armorStandHeadRotation90OffsetX);
						}
						final ArmorStand as = w.spawn(spawn, ArmorStand.class);
						as.setGravity(false);
						if(headPose != null)
							as.setHeadPose(headPose);
						as.setHelmet(new ItemStack(b.getItemType(), 1, b.getData()));
						as.setVisible(false);
						this.armorStands.add(as);
					}
				}
			}
		}
	}

	public int getId()
	{
		return this.id;
	}

	public void moveArmorStands(final double d, final double e, final double f)
	{
		final Location loc = this.armorStands.get(0).getLocation();
		for(int i=this.armorStands.size()-1;i>=0;i--)
		{
			this.armorStands.get(i).getLocation(loc);
			loc.add(d, e, f);
			this.armorStands.get(i).teleport(loc);
		}
	}

	public void remove()
	{
		for(int i=this.armorStands.size()-1;i>=0;i--)
		{
			this.armorStands.get(i).remove();
		}
	}

}
