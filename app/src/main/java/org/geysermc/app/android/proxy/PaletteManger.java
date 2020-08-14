package org.geysermc.app.android.proxy;

import com.nukkitx.nbt.NBTOutputStream;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.nbt.NbtUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaletteManger {

    public static final NbtList<NbtMap> BLOCK_PALETTE;
    public static final NbtMap BIOMES_PALETTE;
    public static final byte[] EMPTY_LEVEL_CHUNK_DATA;

    private static final NbtMap EMPTY_TAG = NbtMap.EMPTY;

    static {
        /* Load block palette */
        // Build the air block entry
        NbtMapBuilder mainBuilder = NbtMap.builder();
        mainBuilder.putShort("id", (short) 0);

        NbtMapBuilder blockBuilder = NbtMap.builder();
        blockBuilder.putString("name", "minecraft:air");
        blockBuilder.putInt("version", 17825806);
        blockBuilder.put("states", NbtMap.EMPTY);

        mainBuilder.put("block", blockBuilder.build());

        // Build the block list with the entry
        List<NbtMap> blocks = new ArrayList<>();
        blocks.add(mainBuilder.build());

        BLOCK_PALETTE = new NbtList<>(NbtType.COMPOUND, blocks);

        /* Load biomes */
        // Build a fake plains biome entry
        NbtMapBuilder plainsBuilder = NbtMap.builder();
        plainsBuilder.putFloat("blue_spores", 0f);
        plainsBuilder.putFloat("white_ash", 0f);
        plainsBuilder.putFloat("ash", 0f);
        plainsBuilder.putFloat("temperature", 0f);
        plainsBuilder.putFloat("red_spores", 0f);
        plainsBuilder.putFloat("downfall", 0f);

        plainsBuilder.put("minecraft:overworld_generation_rules", NbtMap.EMPTY);
        plainsBuilder.put("minecraft:climate", NbtMap.EMPTY);
        plainsBuilder.put("tags", NbtList.EMPTY);

        // Add the fake plains to the map
        NbtMapBuilder biomesBuilder = NbtMap.builder();
        biomesBuilder.put("plains", plainsBuilder.build());

        // Build the biomes palette
        BIOMES_PALETTE = biomesBuilder.build();

        /* Create empty chunk data */
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(new byte[258]); // Biomes + Border Size + Extra Data Size

            try (NBTOutputStream nbtOutputStream = NbtUtils.createNetworkWriter(outputStream)) {
                nbtOutputStream.writeTag(EMPTY_TAG);
            }

            EMPTY_LEVEL_CHUNK_DATA = outputStream.toByteArray();
        } catch (IOException e) {
            throw new AssertionError("Unable to generate empty level chunk data");
        }
    }

    public static void init() {
        // no-op
    }
}
