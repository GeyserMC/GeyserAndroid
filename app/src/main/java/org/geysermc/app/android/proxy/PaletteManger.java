package org.geysermc.app.android.proxy;

import android.content.res.Resources;

import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NBTOutputStream;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtUtils;

import org.geysermc.app.android.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PaletteManger {

    public static final NbtList<NbtMap> BLOCK_PALLETE;
    public static final NbtMap BIOMES_PALLETE;
    public static final byte[] EMPTY_LEVEL_CHUNK_DATA;

    private static final NbtMap EMPTY_TAG = NbtMap.EMPTY;

    static {
        /* Load block palette */
        InputStream stream = getResource(R.raw.runtime_block_states);

        try (NBTInputStream nbtInputStream = NbtUtils.createNetworkReader(stream)) {
            BLOCK_PALLETE = (NbtList<NbtMap>) nbtInputStream.readTag();
        } catch (Exception e) {
            throw new AssertionError("Unable to get blocks from runtime block states", e);
        }

        /* Load biomes */
        stream = getResource(R.raw.biome_definitions);

        try (NBTInputStream nbtInputStream = NbtUtils.createNetworkReader(stream)){
            BIOMES_PALLETE = (NbtMap) nbtInputStream.readTag();
        } catch (Exception e) {
            throw new AssertionError("Failed to get biomes from biome definitions", e);
        }

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

    private static InputStream getResource(int resourceID) {
        return Resources.getSystem().openRawResource(resourceID);
    }
}
