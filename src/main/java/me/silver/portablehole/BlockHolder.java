package me.silver.portablehole;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

public class BlockHolder {

    private final Location location;
    private final Material material;
    private final MaterialData materialData;
    private final byte data;

    public BlockHolder(Location location, Material material, MaterialData materialData, byte data) {
        this.location = location;
        this.material = material;
        this.materialData = materialData;
        this.data = data;
    }

    public void createBlock() {
        Block block = location.getBlock();

        block.setType(material);
        block.getState().setData(materialData);
        block.setData(data);
    }

}
