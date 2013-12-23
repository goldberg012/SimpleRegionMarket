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

package com.thezorro266.bukkit.srm.helpers;

import lombok.Data;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.Configuration;

import com.thezorro266.bukkit.srm.SimpleRegionMarket;
import com.thezorro266.bukkit.srm.exceptions.ContentLoadException;
import com.thezorro266.bukkit.srm.helpers.RegionFactory.Region;
import com.thezorro266.bukkit.srm.templates.Template;

public @Data
class Sign {
	public static final int SIGN_LINE_COUNT = 4;

	final Region region;
	final Location location;
	final boolean isWallSign;
	final BlockFace direction;

	public Sign(Region region, Location location, boolean isWallSign, BlockFace direction) {
		if (region == null) {
			throw new IllegalArgumentException("Region must not be null");
		}
		if (location == null) {
			throw new IllegalArgumentException("Location must not be null");
		}
		if (direction == null) {
			throw new IllegalArgumentException("Direction must not be null");
		}

		this.region = region;
		this.location = new Location(location);
		this.isWallSign = isWallSign;
		this.direction = direction;
	}

	public void clear() {
		setContent(new String[] { "", "", "", "" });
	}

	public void setContent(String[] lines) {
		Block signBlock = location.getBlock();
		org.bukkit.block.Sign signBlockState = (org.bukkit.block.Sign) signBlock.getState();
		if (!isSign(signBlock)) {
			signBlock.setType(isWallSign ? Material.WALL_SIGN : Material.SIGN_POST);
			((org.bukkit.material.Sign) signBlockState.getData()).setFacingDirection(direction);
		}

		for (int i = 0; i < SIGN_LINE_COUNT; i++) {
			signBlockState.setLine(i, lines[i]);
		}
		
		signBlockState.update(false, false);
	}

	public boolean isBlockThisSign(Block block) {
		if (isSign(block)) {
			if (location.isBlockAt(block)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSign(Block block) {
		if (block.getType().equals(Material.WALL_SIGN) || block.getType().equals(Material.SIGN_POST)) {
			return true;
		}
		return false;
	}

	public static Sign getSignFromBlock(Block block) {
		if (isSign(block)) {
			for (Template template : SimpleRegionMarket.getInstance().getTemplateManager().getTemplateList()) {
				for (Region region : template.getRegionList()) {
					for (Sign sign : region.getSignList()) {
						if (sign.isBlockThisSign(block)) {
							return sign;
						}
					}
				}
			}
		}
		return null;
	}

	public void saveToConfiguration(Configuration config, String path) {
		config.set(path + "region", region.getName());
		location.saveToConfiguration(config, path + "location.");
		config.set(path + "is_wall_sign", isWallSign);
		config.set(path + "direction", direction.toString());
	}

	public static Sign loadFromConfiguration(Configuration config, Region region, String path) throws ContentLoadException {
		String regionName = region.getName();
		String configRegionName = config.getString(path + "region");
		if (regionName.equals(configRegionName)) {
			Location location = Location.loadFromConfiguration(config, path + "location.");
			boolean isWallSign = config.getBoolean(path + "is_wall_sign");
			BlockFace direction = BlockFace.valueOf(config.getString(path + "direction"));

			return new Sign(region, location, isWallSign, direction);
		} else {
			throw new ContentLoadException("Region string in sign config did not match the outer region");
		}
	}

	@Override
	public String toString() {
		return String.format("Sign[r:%s,l:%s]", region.getName(), location);
	}
}
