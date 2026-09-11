package draylar.gateofbabylon.enchantment;

import draylar.gateofbabylon.api.ValidatingEnchantment;
import draylar.gateofbabylon.item.KatanaItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;

public class KatanaSlashEnchantment extends Enchantment implements ValidatingEnchantment {

    private final SoundEvent sound;
    private final ParticleOptions particle;
    protected final HitExecutor onHit;

    public KatanaSlashEnchantment(SoundEvent sound, ParticleOptions particle) {
        this(sound, particle, (entity, player, stack) -> {});
    }

    public KatanaSlashEnchantment(SoundEvent sound, ParticleOptions particle, HitExecutor onHit) {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
        this.sound = sound;
        this.particle = particle;
        this.onHit = onHit;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof KatanaItem;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof KatanaSlashEnchantment);
    }

    public SoundEvent getSound() {
        return sound;
    }

    public ParticleOptions getParticle() {
        return particle;
    }

    public void onHit(LivingEntity target, Player source, ItemStack stack) {
        onHit.run(target, source, stack);
    }

    public interface HitExecutor {
        void run(LivingEntity target, Player source, ItemStack stack);
    }
}

