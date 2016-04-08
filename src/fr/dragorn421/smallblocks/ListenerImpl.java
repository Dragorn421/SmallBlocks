package fr.dragorn421.smallblocks;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ListenerImpl implements Listener
{

	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	public void onPlayerMove(final PlayerMoveEvent e)
	{
		final Struct struct = Util.getMetadata(e.getPlayer(), SmallBlocksPlugin.MOVING_STRUCT_METADATA_KEY, Struct.class);
		if(struct == null)
			return;
		final Location from = e.getFrom(), to = e.getTo();
		struct.moveArmorStands(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ());
	}

}
