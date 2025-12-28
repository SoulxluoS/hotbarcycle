package com.github.nyuppo.mixin;

import com.github.nyuppo.HotbarCycleClient;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class RepeatClickCycleMixin {
    @Final
    @Mutable
    @Shadow
    public Options options;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    public abstract boolean hasControlDown();

    @Inject(
        method = "handleKeybinds",
        at = @At("HEAD"))
    private void repeatClickCycleMixin(CallbackInfo ci) {
        if (player == null) {
            return;
        }
        if (HotbarCycleClient.getConfig().getRepeatSlotToCycle() && this.options.keyHotbarSlots[this.player.getInventory().getSelectedSlot()].consumeClick()) {
            HotbarCycleClient.shiftSingle(((Minecraft) (Object) this), this.player.getInventory().getSelectedSlot(), HotbarCycleClient.Direction.DOWN);
        }
    }

    @Inject(
        method = "pickBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handlePickItemFromBlock(Lnet/minecraft/core/BlockPos;Z)V"))
    private void cyclePickedItem(CallbackInfo ci, @Local BlockHitResult hitResult) {
        if (HotbarCycleClient.getConfig().getCycleWhenPickingBlock() && level != null) {
            var pickStack = level.getBlockState(hitResult.getBlockPos()).getCloneItemStack(level, hitResult.getBlockPos(), hasControlDown());
            pickStackCycle(pickStack);
        }
    }

    @Inject(
        method = "pickBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handlePickItemFromEntity(Lnet/minecraft/world/entity/Entity;Z)V"))
    private void cyclePickedItem(CallbackInfo ci, @Local EntityHitResult hitResult) {
        if (HotbarCycleClient.getConfig().getCycleWhenPickingBlock()) {
            var pickStack = hitResult.getEntity().getPickResult();
            pickStackCycle(pickStack);
        }
    }

    @Unique
    private void pickStackCycle(ItemStack pickedStack) {
        if (player == null || pickedStack == null || pickedStack.isEmpty()) {
            return;
        }
        int slot = player.getInventory().findSlotMatchingItem(pickedStack);
        if (slot == -1) {
            return;
        }
        int x, y;

        if (8 < slot && HotbarCycleClient.isColumnEnabled(x = slot % 9) && HotbarCycleClient.isRowEnabled(y = slot / 9)) {
            final Minecraft client = (Minecraft) (Object) this;
            int direction = -1;
            for (int i = 1; i < y; ++i) {
                if (HotbarCycleClient.isRowEnabled(i)) {
                    direction--;
                }
            }

            if (HotbarCycleClient.getConfig().getPickCyclesWholeHotbar()) {
                HotbarCycleClient.shiftRows(client, direction);
            } else {
                HotbarCycleClient.shiftSingle(client, x, direction);
            }
        }
    }
}
