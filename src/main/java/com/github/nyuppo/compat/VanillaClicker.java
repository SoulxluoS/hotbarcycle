package com.github.nyuppo.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.inventory.ClickType;

public class VanillaClicker implements Clicker {
    @Override
    public void swap(Minecraft client, int from, int to) {
        MultiPlayerGameMode interactionManager = client.gameMode;

        if (interactionManager != null && client.player != null && client.player.getInventory() != null) {
            interactionManager.handleInventoryMouseClick(client.player.inventoryMenu.containerId, from, to, ClickType.SWAP, client.player);
        }
    }
}
