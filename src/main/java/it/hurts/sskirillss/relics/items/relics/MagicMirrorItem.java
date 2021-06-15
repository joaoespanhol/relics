package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagicMirrorItem extends RelicItem<MagicMirrorItem.Stats> implements IHasTooltip {
    public MagicMirrorItem() {
        super(Rarity.RARE);
    }

    @Override
    public java.util.List<ITextComponent> getShiftTooltip() {
        java.util.List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.magic_mirror.shift_1"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (playerIn.getCooldowns().isOnCooldown(ItemRegistry.MAGIC_MIRROR.get())
                || worldIn.isClientSide()) return ActionResult.fail(stack);
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerIn;
        BlockPos pos = serverPlayer.getRespawnPosition();
        ServerWorld world = serverPlayer.getServer().getLevel(serverPlayer.getRespawnDimension());
        if (pos != null && world != null) {
            if (playerIn.getVehicle() != null) playerIn.stopRiding();
            serverPlayer.teleportTo(world, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F, playerIn.yRot, playerIn.xRot);
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (!playerIn.abilities.instabuild) playerIn.getCooldowns().addCooldown(ItemRegistry.MAGIC_MIRROR.get(), config.cooldown * 20);
        } else playerIn.displayClientMessage(new TranslationTextComponent("tooltip.relics.magic_mirror.invalid_location"), true);
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.CAVE;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    public static class Stats extends RelicStats {
        public int cooldown = 60;
    }
}