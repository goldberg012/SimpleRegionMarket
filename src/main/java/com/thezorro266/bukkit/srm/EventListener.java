/**
 * SimpleRegionMarket
 * Copyright (C) 2013  theZorro266 <http://www.thezorro266.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.thezorro266.bukkit.srm;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.thezorro266.bukkit.srm.helpers.Location;
import com.thezorro266.bukkit.srm.helpers.Sign;
import com.thezorro266.bukkit.srm.templates.Template;

public class EventListener implements Listener {
	public EventListener() {
		SimpleRegionMarket.getInstance().getServer().getPluginManager().registerEvents(this, SimpleRegionMarket.getInstance());
	}

	@EventHandler
	public void onSignChanged(SignChangeEvent event) {
		if (!event.isCancelled()) {
			Player player = event.getPlayer();
			Sign sign = Sign.getSignFromBlock(event.getBlock());
			if (sign != null) {
				if (!sign.getRegion().getTemplate().breakSign(player, sign)) {
					event.setCancelled(true);
					return;
				}
			}

			for (Template template : SimpleRegionMarket.getInstance().getTemplateManager().getTemplateList()) {
				String[] lines = event.getLines().clone();
				if (template.isSignApplicable(Location.fromBlock(event.getBlock()), lines)) {
					if (template.createSign(player, event.getBlock(), lines)) {
						for (int i = 0; i < lines.length; i++) {
							event.setLine(i, lines[i]);
						}
						break;
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			Sign sign = Sign.getSignFromBlock(event.getBlock());
			if (sign != null) {
				if (!sign.getRegion().getTemplate().breakSign(event.getPlayer(), sign)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.hasBlock()) {
			if (Sign.isSign(event.getClickedBlock())) {
				if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Sign sign = Sign.getSignFromBlock(event.getClickedBlock());
					if (sign != null) {
						sign.getRegion().getTemplate().clickSign(event.getPlayer(), sign);
					}
				}
			}
		}
	}
}