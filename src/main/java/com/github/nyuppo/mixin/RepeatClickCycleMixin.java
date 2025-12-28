package com.github.nyuppo.mixin;

import com.github.nyuppo.HotbarCycleClient;
import com.github.nyuppo.config.HotbarCycleConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class RepeatClickCycleMixin {
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

    @WrapOperation(
        method = "pickBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handlePickItemFromBlock(Lnet/minecraft/core/BlockPos;Z)V"))
    private void cyclePickedItem(MultiPlayerGameMode instance, BlockPos pos, boolean includeData, Operation<Void> original) {
        if (level == null) {
            original.call(instance, pos, includeData);
            return;
        }
        var pickedStack = level.getBlockState(pos).getCloneItemStack(level, pos, includeData);
        pickStackCycle(pickedStack);
        original.call(instance, pos, includeData);
    }

    @WrapOperation(
        method = "pickBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handlePickItemFromEntity(Lnet/minecraft/world/entity/Entity;Z)V"))
    private void cyclePickedItem(MultiPlayerGameMode instance, Entity entity, boolean includeData, Operation<Void> original) {
        var pickedStack = entity.getPickResult();
        if (pickedStack == null) {
            return;
        }
        pickStackCycle(pickedStack);
        original.call(instance, entity, includeData);
    }

    @Unique
    private void pickStackCycle(ItemStack pickedStack) {
        if (player == null) {
            return;
        }
        int slot = player.getInventory().findSlotMatchingItem(pickedStack);
        if (slot == -1) {
            return;
        }
        final HotbarCycleConfig config = HotbarCycleClient.getConfig();
        int x, y;

        if (8 < slot && config.getCycleWhenPickingBlock() && HotbarCycleClient.isColumnEnabled(x = slot % 9) && HotbarCycleClient.isRowEnabled(y = slot / 9)) {
            final Minecraft client = (Minecraft) (Object) this;
            int direction = -1;
            for (int i = 1; i < y; ++i) {
                if (HotbarCycleClient.isRowEnabled(i)) {
                    direction--;
                }
            }

            if (config.getPickCyclesWholeHotbar()) {
                HotbarCycleClient.shiftRows(client, direction);
            } else {
                HotbarCycleClient.shiftSingle(client, x, direction);
            }
        }
    }
}
