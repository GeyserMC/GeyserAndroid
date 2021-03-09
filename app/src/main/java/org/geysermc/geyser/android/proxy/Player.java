/*
 * Copyright (c) 2020-2020 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/GeyserAndroid
 */

package org.geysermc.geyser.android.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.data.AuthoritativeMovementMode;
import com.nukkitx.protocol.bedrock.data.GamePublishSetting;
import com.nukkitx.protocol.bedrock.data.GameRuleData;
import com.nukkitx.protocol.bedrock.data.GameType;
import com.nukkitx.protocol.bedrock.data.PlayerPermission;
import com.nukkitx.protocol.bedrock.data.SyncedPlayerMovementSettings;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.packet.BiomeDefinitionListPacket;
import com.nukkitx.protocol.bedrock.packet.CreativeContentPacket;
import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;
import com.nukkitx.protocol.bedrock.packet.PlayStatusPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityMotionPacket;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;
import com.nukkitx.protocol.bedrock.packet.TransferPacket;

import java.net.InetSocketAddress;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Player {

    private final String xuid;
    private final UUID identity;
    private final String displayName;

    private final BedrockServerSession session;

    public Player(JsonNode extraData, BedrockServerSession session) {
        this.xuid = extraData.get("XUID").asText();
        this.identity = UUID.fromString(extraData.get("identity").asText());
        this.displayName = extraData.get("displayName").asText();

        this.session = session;
    }

    /**
     * Send a few different packets to get the client to load in
     */
    public void sendStartGame() {
        // A lot of this likely doesn't need to be changed
        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.setUniqueEntityId(1);
        startGamePacket.setRuntimeEntityId(1);
        startGamePacket.setPlayerGameType(GameType.DEFAULT);
        startGamePacket.setPlayerPosition(Vector3f.from(0, 64 + 2, 0));
        startGamePacket.setRotation(Vector2f.ONE);

        startGamePacket.setSeed(-1);
        startGamePacket.setDimensionId(2);
        startGamePacket.setGeneratorId(1);
        startGamePacket.setLevelGameType(GameType.DEFAULT);
        startGamePacket.setDifficulty(0);
        startGamePacket.setDefaultSpawn(Vector3i.ZERO);
        startGamePacket.setAchievementsDisabled(true);
        startGamePacket.setCurrentTick(-1);
        startGamePacket.setEduEditionOffers(0);
        startGamePacket.setEduFeaturesEnabled(false);
        startGamePacket.setRainLevel(0);
        startGamePacket.setLightningLevel(0);
        startGamePacket.setMultiplayerGame(true);
        startGamePacket.setBroadcastingToLan(true);
        startGamePacket.getGamerules().add(new GameRuleData<>("showcoordinates", true));
        startGamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setXblBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setCommandsEnabled(true);
        startGamePacket.setTexturePacksRequired(false);
        startGamePacket.setBonusChestEnabled(false);
        startGamePacket.setStartingWithMap(false);
        startGamePacket.setTrustingPlayers(true);
        startGamePacket.setDefaultPlayerPermission(PlayerPermission.VISITOR);
        startGamePacket.setServerChunkTickRange(4);
        startGamePacket.setBehaviorPackLocked(false);
        startGamePacket.setResourcePackLocked(false);
        startGamePacket.setFromLockedWorldTemplate(false);
        startGamePacket.setUsingMsaGamertagsOnly(false);
        startGamePacket.setFromWorldTemplate(false);
        startGamePacket.setWorldTemplateOptionLocked(false);

        startGamePacket.setLevelId("");
        startGamePacket.setLevelName("LAN Proxy");
        startGamePacket.setPremiumWorldTemplateId("");
        startGamePacket.setCurrentTick(0);
        startGamePacket.setEnchantmentSeed(0);
        startGamePacket.setMultiplayerCorrelationId("");

        SyncedPlayerMovementSettings settings = new SyncedPlayerMovementSettings();
        settings.setMovementMode(AuthoritativeMovementMode.CLIENT);
        settings.setRewindHistorySize(0);
        settings.setServerAuthoritativeBlockBreaking(false);
        startGamePacket.setPlayerMovementSettings(settings);

        startGamePacket.setVanillaVersion("*");
        session.sendPacket(startGamePacket);

        // Send an empty chunk
        LevelChunkPacket data = new LevelChunkPacket();
        data.setChunkX(0);
        data.setChunkZ(0);
        data.setSubChunksLength(0);
        data.setData(PaletteManger.EMPTY_LEVEL_CHUNK_DATA);
        data.setCachingEnabled(false);
        session.sendPacket(data);

        // Send a CreativeContentPacket - required for 1.16.100
        CreativeContentPacket creativeContentPacket = new CreativeContentPacket();
        creativeContentPacket.setContents(new ItemData[0]);
        session.sendPacket(creativeContentPacket);

        // Send the biomes
        BiomeDefinitionListPacket biomeDefinitionListPacket = new BiomeDefinitionListPacket();
        biomeDefinitionListPacket.setDefinitions(PaletteManger.BIOMES_PALETTE);
        session.sendPacket(biomeDefinitionListPacket);

        // Let the client know the player can spawn
        PlayStatusPacket playStatusPacket = new PlayStatusPacket();
        playStatusPacket.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacket(playStatusPacket);

        // Freeze the player
        SetEntityMotionPacket setEntityMotionPacket = new SetEntityMotionPacket();
        setEntityMotionPacket.setRuntimeEntityId(1);
        setEntityMotionPacket.setMotion(Vector3f.ZERO);
        session.sendPacket(setEntityMotionPacket);
    }

    /**
     * Send the player to the another server
     */
    public void connectToServer(String address, int port) {
        ProxyServer.getInstance().getProxyLogger().info("Sending server transfer packet to " + displayName);

        // Create an InetSocketAddress to reduce issues with hostnames for PS4
        // Thanks Extollite
        InetSocketAddress socketAddress = new InetSocketAddress(address, port);

        TransferPacket transferPacket = new TransferPacket();
        transferPacket.setAddress(socketAddress.getAddress().getHostAddress());
        transferPacket.setPort(socketAddress.getPort());
        session.sendPacket(transferPacket);
    }
}
