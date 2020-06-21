package io.github.a5h73y.carz.configuration.impl;

import io.github.a5h73y.carz.configuration.CarzConfiguration;
import io.github.a5h73y.carz.enums.BlockType;
import io.github.a5h73y.carz.utility.PluginUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class BlocksConfig extends CarzConfiguration {

	private Set<Material> climbBlocks;
	private Set<Material> placeableBlocks;
	private Map<String, Double> speedBlocks;
	private Map<String, Double> launchBlocks;

	@Override
	protected String getFileName() {
		return "blocks.yml";
	}

	@Override
	protected void initializeConfig() {

	}

	@Override
	public void reload() {
		super.reload();

		this.climbBlocks = extractMaterialBlocks(BlockType.CLIMB);
		this.placeableBlocks = extractMaterialBlocks(BlockType.PLACEABLE);
		this.speedBlocks = extractMaterialBlocksAmount(BlockType.SPEED);
		this.launchBlocks = extractMaterialBlocksAmount(BlockType.LAUNCH);
	}

	/**
	 * Set the amount of the block type.
	 * Setting the amount to null will delete it from the config.
	 *
	 * @param blockType {@link BlockType}
	 * @param material {@link Material}
	 * @param amount amount to set
	 */
	public void setBlock(BlockType blockType, Material material, Double amount) {
		if (blockType == null || material == null) {
			return;
		}

		this.set(blockType.getConfigPath() + "." + material.name(), amount);
		this.save();
		this.reload();
	}

	/**
	 * Add a Material to a the list of block types.
	 *
	 * @param blockType {@link BlockType}
	 * @param material {@link Material}
	 */
	public void addBlock(BlockType blockType, Material material) {
		if (blockType == null || material == null) {
			return;
		}

		List<String> rawMaterials = this.getStringList(blockType.getConfigPath());
		rawMaterials.add(material.name());
		this.set(blockType.getConfigPath(), rawMaterials);
		this.save();
		this.reload();
	}

	/**
	 * Remove a Material from a list of block types.
	 *
	 * @param blockType {@link BlockType}
	 * @param material {@link Material}
	 */
	public void removeBlock(BlockType blockType, Material material) {
		if (blockType == null || material == null) {
			return;
		}

		List<String> rawMaterials = this.getStringList(blockType.getConfigPath());
		rawMaterials.remove(material.name());
		this.set(blockType.getConfigPath(), rawMaterials);
		this.save();
		this.reload();
	}

	/**
	 * Determine if the Material already exists within the block types.
	 *
	 * @param blockType {@link BlockType}
	 * @param material {@link Material}
	 * @return material exists in block type
	 */
	public boolean alreadyExists(BlockType blockType, Material material) {
		if (blockType.isHasAmount()) {
			return get(blockType.getConfigPath() + "." + material.name()) != null;

		} else {
			return getStringList(blockType.getConfigPath()).contains(material.name());
		}
	}

	public Set<String> getSpeedBlocks() {
		return this.speedBlocks.keySet();
	}

	public Set<String> getLaunchBlocks() {
		return this.launchBlocks.keySet();
	}

	public Set<Material> getClimbBlocks() {
		return this.climbBlocks;
	}

	public Set<Material> getPlaceableBlocks() {
		return this.placeableBlocks;
	}

	public boolean containsSpeedBlock(Material material) {
		return this.speedBlocks.containsKey(material.name());
	}

	public boolean containsLaunchBlock(Material material) {
		return this.launchBlocks.containsKey(material.name());
	}

	public Double getSpeedModifier(Material material) {
		return this.speedBlocks.get(material.name());
	}

	public Double getLaunchAmount(Material material) {
		return this.launchBlocks.get(material.name());
	}

	private Set<Material> extractMaterialBlocks(BlockType blockType) {
		return PluginUtils.convertToValidMaterials(getStringList(blockType.getConfigPath()));
	}

	private Map<String, Double> extractMaterialBlocksAmount(BlockType blockType) {
		Map<String, Double> values = new HashMap<>();
		ConfigurationSection section = this.getConfigurationSection(blockType.getConfigPath());

		if (section != null) {
			Set<Material> materials = PluginUtils.convertToValidMaterials(section.getKeys(false));

			for (Material material : materials) {
				values.put(material.name(),
						this.getDouble(blockType.getConfigPath() + "." + material.name()));
			}
		}

		return values;
	}
}
