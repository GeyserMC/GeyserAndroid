package org.geysermc.app.android.proxy;

import com.nukkitx.nbt.NBTOutputStream;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.nbt.NbtUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class PaletteManger {

    public static final NbtList<NbtMap> BLOCK_PALLETE;
    public static final byte[] EMPTY_LEVEL_CHUNK_DATA;

    static {
        /* Load block palette */
        Collection<NbtMap> blocksList = new ArrayList<>();
        BLOCK_PALLETE = new NbtList(NbtType.COMPOUND, blocksList);

        /* Create empty chunk data */
        try (
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(new byte[258]); // Biomes + Border Size + Extra Data Size

            try (NBTOutputStream nbtOutputStream = NbtUtils.createNetworkWriter(outputStream)) {
                nbtOutputStream.writeTag(NbtMap.EMPTY);
            }

            EMPTY_LEVEL_CHUNK_DATA = outputStream.toByteArray();
        } catch (IOException e) {
            throw new AssertionError("Unable to generate empty level chunk data");
        }
    }
}
