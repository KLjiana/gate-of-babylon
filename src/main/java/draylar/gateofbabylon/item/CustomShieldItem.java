package draylar.gateofbabylon.item;

import draylar.gateofbabylon.api.EnchantmentHandler;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

public class CustomShieldItem extends ShieldItem implements EnchantmentHandler {

    public CustomShieldItem(Item.Properties settings) {
        super(settings);

        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return stack.getTagElement("BlockEntityTag") != null ? this.getDescriptionId() + '.' + getColor(stack).getName() : super.getDescriptionId(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        BannerItem.appendHoverTextFromBannerBlockEntityTag(stack, tooltip);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        user.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(ItemTags.PLANKS) || super.isValidRepairItem(stack, ingredient);
    }

    public static DyeColor getColor(ItemStack stack) {
        return DyeColor.byId(stack.getOrCreateTagElement("BlockEntityTag").getInt("Base"));
    }
}

