package fr.dragorn421.smallblocks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class Struct
{

	static private int increment = 0;

	final private int id;
	final private MaterialData blocks[][][];
	final private Location center;
	final private List<ArmorStand> armorStands;

	private BukkitTask spinTask;

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
		this.center = from.clone();
		this.armorStands = new ArrayList<>();
		this.spinTask = null;
	}

	public void toArmorStands(final Location loc, final boolean hollow, final double rotation)
	{
		loc.setX(loc.getBlockX() - Const.armorStandHeadSideOffset);
		loc.setY(loc.getBlockY() - Const.armorStandHeadHeightOffset);
		loc.setZ(loc.getBlockZ() - Const.armorStandHeadSideOffset);
		this.center.setX(loc.getX() + (this.blocks.length - 1) * Const.armorStandHeadSize / 2D);
		this.center.setY(loc.getY() + (this.blocks[0].length - 1) * Const.armorStandHeadSize / 2D);
		this.center.setZ(loc.getZ() + (this.blocks[0][0].length - 1) * Const.armorStandHeadSize / 2D);
		//System.out.println(" center #" + this.id + " " + this.center);//TODO debug
		final Location spawn = loc.clone();
		final Vector offsetX, offsetZ;
		if(rotation == 0)
		{
			offsetX = offsetZ = null;
		}
		else
		{
			offsetX = new Vector(Const.armorStandHeadSize * Math.cos(rotation), 0, Const.armorStandHeadSize * Math.sin(rotation));
			offsetZ = new Vector(offsetX.getX(), 0, -offsetX.getZ());
		}
		for(int i=0;i<this.blocks.length;i++)
		{
			for(int j=0;j<this.blocks[i].length;j++)
			{
				for(int k=0;k<this.blocks[i][j].length;k++)
				{
					final MaterialData b = this.blocks[i][j][k];
					if(b != null && (!hollow || this.isVisible(i, j, k)))
					{
						if(rotation == 0)
						{
							spawn.setX(loc.getX() + i * Const.armorStandHeadSize);
							spawn.setY(loc.getY() + j * Const.armorStandHeadSize);
							spawn.setZ(loc.getZ() + k * Const.armorStandHeadSize);
						}
						else
						{
							final Vector v = offsetX.clone().multiply(i).add(offsetZ.clone().multiply(k));
							spawn.setX(loc.getX() + v.getX());
							spawn.setY(loc.getY() + j * Const.armorStandHeadSize);
							spawn.setZ(loc.getZ() + v.getZ());
						}
						this.convertBlock(b, spawn, rotation);
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void convertBlock(final MaterialData b, final Location spawn, final double rotation)
	{
		Material type = b.getItemType();
		byte data = b.getData();
		EulerAngle headPose = null;
		if(Util.isStairs(type))
			headPose = Util.getStairsRotation(data);
		else if(Util.isWoodLog(type))
		{
			headPose = Util.getWoodLogRotation(data);
			data %= 4;
		}
		else if(Util.isSlab(type) && Util.isReversedSlab(data))
		{
			spawn.add(0, Const.armorStandHeadSize/2, 0);
			data %= 8;
		}
		else if(Util.isDoubleSlab(type))
		{
			final MaterialData newBlock = Util.getBlockForDoubleSlab(type, data);
			if(Util.isSlab(newBlock.getItemType()))
			{
				convertBlock(newBlock, spawn, rotation);
				spawn.add(0, Const.armorStandHeadSize/2, 0);
			}
			type = newBlock.getItemType();
			data = newBlock.getData();
			//TODO double slabs
		}
		if(headPose != null)
		{
			if(headPose.getZ() == Math.PI/2)
				 spawn.add(-Const.armorStandHeadRotation90OffsetX, Const.armorStandHeadRotation90OffsetY, 0);
			else if(headPose.getZ() == Math.PI)
				 spawn.add(0, Const.armorStandHeadRotation180Offset, 0);
			if(headPose.getX() == Math.PI/2)
				spawn.add(0, Const.armorStandHeadRotation90OffsetY, -Const.armorStandHeadRotation90OffsetX);
		}
		final ArmorStand as = spawn.getWorld().spawn(spawn, ArmorStand.class);
		as.setGravity(false);
		if(headPose != null)
		{
			if(rotation != 0)
				headPose.setY(headPose.getY() + rotation);
			as.setHeadPose(headPose);
		}
		else if(rotation != 0)
			as.setHeadPose(new EulerAngle(0, rotation, 0));
		as.setHelmet(new ItemStack(type, 1, data));
		as.setVisible(false);
		this.armorStands.add(as);
	}

	public boolean isVisible(final int i, final int j, final int k)
	{
		return !this.isOccluding(i - 1,	j,		k)
			|| !this.isOccluding(i,		j - 1,	k)
			|| !this.isOccluding(i,		j,		k - 1)
			|| !this.isOccluding(i + 1,	j,		k)
			|| !this.isOccluding(i,		j + 1,	k)
			|| !this.isOccluding(i,		j,		k + 1);
	}

	private boolean isOccluding(final int i, final int j, final int k)
	{
		if(i < 0 || i >= this.blocks.length)
			return false;
		if(j < 0 || j >= this.blocks[i].length)
			return false;
		if(k < 0 || k >= this.blocks[i][j].length)
			return false;
		final MaterialData block = this.blocks[i][j][k];
		return block==null?false:block.getItemType().isOccluding();
	}

	public void moveArmorStands(final double x, final double y, final double z)
	{
		if(this.armorStands.size() == 0)
			return;
		final Location loc = this.armorStands.get(0).getLocation();
		for(int i=this.armorStands.size()-1;i>=0;i--)
		{
			this.armorStands.get(i).getLocation(loc);
			loc.add(x, y, z);
			this.armorStands.get(i).teleport(loc);
		}
		center.add(x, y, z);
	}

	public void rotateRad(double rotation)
	{
		if(this.armorStands.size() == 0)
			return;
		final Location loc = this.armorStands.get(0).getLocation();
		for(int i=this.armorStands.size()-1;i>=0;i--)
		{
			final ArmorStand as = this.armorStands.get(i);
			as.getLocation(loc);
			center.setY(loc.getY());
			final double	x = loc.getX() - this.center.getX(),
							z = loc.getZ() - this.center.getZ();
			if(x != 0 || z != 0)
			{
				final double	h = Math.hypot(x, z),
								r = Math.atan(x/z),
								newR = r + rotation,
								newX = Math.sin(newR) * h,
								newZ = Math.cos(newR) * h;
				if(z < 0)
				{
					loc.setX(this.center.getX() - newX);
					loc.setZ(this.center.getZ() - newZ);
				}
				else
				{
					loc.setX(this.center.getX() + newX);
					loc.setZ(this.center.getZ() + newZ);
				}
/*				System.out.println("Block " + as.getHelmet().getType() + ":\n"
									+ "x " + x + "\n"
									+ "z " + z + "\n"
									+ "h " + h + "\n"
									+ "r " + r + "rad = " + Math.toDegrees(r) + "deg\n"
									+ "newR " + newR + "rad = " + Math.toDegrees(newR) + "deg\n"
									+ "newX " + newX + "\n"
									+ "newZ " + newZ + "\n"
									+ "real newX " + loc.getX() + "\n"
									+ "real newZ " + loc.getZ());//TODO debug*/
				as.teleport(loc);
			}
			as.setHeadPose(as.getHeadPose().add(0, -rotation, 0));
		}
	}

	public void remove()
	{
		if(this.spinTask != null)
			this.spinTask.cancel();
		for(int i=this.armorStands.size()-1;i>=0;i--)
		{
			this.armorStands.get(i).remove();
		}
	}

	public int getId()
	{
		return this.id;
	}

	public boolean spinRad(final double rotation, final int interval)
	{
		if(this.spinTask != null)
			this.spinTask.cancel();
		if(rotation == 0 || interval < 1)
			return false;
		this.spinTask = Bukkit.getScheduler().runTaskTimer(SmallBlocksPlugin.get(), new Runnable() {
			@Override
			public void run()
			{
				Struct.this.rotateRad(rotation);
			}
		}, interval, interval);
		return true;
	}

}
