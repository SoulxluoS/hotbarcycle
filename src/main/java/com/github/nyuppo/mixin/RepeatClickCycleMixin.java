package com.github.nyuppo.mixin;

import com.github.nyuppo.HotbarCycleClient;
import com.github.nyuppo.config.HotbarCycleConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class RepeatClickCycleMixin {
    @Final
    @Mutable
    @Shadow
    public GameOptions options;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Inject(
        method = "handleInputEvents",
        at = @At("HEAD"))
    private void repeatClickCycleMixin(CallbackInfo ci) {
        if (player == null) {
            return;
        }
        if (HotbarCycleClient.getConfig().getRepeatSlotToCycle() && this.options.hotbarKeys[this.player.getInventory().getSelectedSlot()].wasPressed()) {
            HotbarCycleClient.shiftSingle(((MinecraftClient) (Object) this), this.player.getInventory().getSelectedSlot(), HotbarCycleClient.Direction.DOWN);
        }
    }

    @WrapOperation(
        method = "doItemPick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;pickItemFromBlock(Lnet/minecraft/util/math/BlockPos;Z)V"))
    private void cyclePickedItem(ClientPlayerInteractionManager instance, BlockPos pos, boolean includeData, Operation<Void> original) {
        if (world == null) {
            original.call(instance, pos, includeData);
            return;
        }
        var pickedStack = world.getBlockState(pos).getPickStack(world, pos, includeData);
        pickStackCycle(pickedStack);
        original.call(instance, pos, includeData);
    }

    @WrapOperation(
        method = "doItemPick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;pickItemFromEntity(Lnet/minecraft/entity/Entity;Z)V"))
    private void cyclePickedItem(ClientPlayerInteractionManager instance, Entity entity, boolean includeData, Operation<Void> original) {
        var pickedStack = entity.getPickBlockStack();
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
        int slot = player.getInventory().getSlotWithStack(pickedStack);
        if (slot == -1) {
            return;
        }
        final HotbarCycleConfig config = HotbarCycleClient.getConfig();
        int x, y;

        if (8 < slot && config.getCycleWhenPickingBlock() && HotbarCycleClient.isColumnEnabled(x = slot % 9) && HotbarCycleClient.isRowEnabled(y = slot / 9)) {
            final MinecraftClient client = (MinecraftClient) (Object) this;
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
