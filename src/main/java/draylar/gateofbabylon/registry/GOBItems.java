package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.item.*;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

/*
 * Wooden DPS: 6.4
 * Golden DPS: 6.4
 * Stone DPS: 8
 * Iron DPS: 9.6
 * Diamond DPS: 11.2
 * Netherite DPS: 12.8
 *
 * Damage: 1 + material damage + damage
 */
public final class GOBItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GateOfBabylon.MODID);

    // Daggers are medium-speed weapons with medium damage.
    public static final RegistryObject<DaggerItem> WOODEN_DAGGER = register("wooden_dagger", () -> new DaggerItem(Tiers.WOOD, 3.5f, 2.1f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<DaggerItem> STONE_DAGGER = register("stone_dagger", () -> new DaggerItem(Tiers.STONE, 4.5f, 2.1f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<DaggerItem> IRON_DAGGER = register("iron_dagger", () -> new DaggerItem(Tiers.IRON, 5.5f, 2f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<DaggerItem> GOLDEN_DAGGER = register("golden_dagger", () -> new DaggerItem(Tiers.GOLD, 3.5f, 2.1f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<DaggerItem> DIAMOND_DAGGER = register("diamond_dagger", () -> new DaggerItem(Tiers.DIAMOND, 6.5f, 2f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<DaggerItem> NETHERITE_DAGGER = register("netherite_dagger", () -> new DaggerItem(Tiers.NETHERITE, 6.5f, 2.3f, new Item.Properties().stacksTo(1).fireResistant()));

    // Spears are ranged weapons, similar to Tridents.
    public static final RegistryObject<SpearItem> WOODEN_SPEAR = register("wooden_spear", () -> new SpearItem(Tiers.WOOD, 3.0f, 1.4f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<SpearItem> STONE_SPEAR = register("stone_spear", () -> new SpearItem(Tiers.STONE, 4.0f, 1.4f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<SpearItem> IRON_SPEAR = register("iron_spear", () -> new SpearItem(Tiers.IRON, 6.0f, 1.3f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<SpearItem> GOLDEN_SPEAR = register("golden_spear", () -> new SpearItem(Tiers.GOLD, 3.0f, 1.2f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<SpearItem> DIAMOND_SPEAR = register("diamond_spear", () -> new SpearItem(Tiers.DIAMOND, 7.0f, 1.1f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<SpearItem> NETHERITE_SPEAR = register("netherite_spear", () -> new SpearItem(Tiers.NETHERITE, 8.0f, 1.0f, new Item.Properties().stacksTo(1).fireResistant()));

    public static final RegistryObject<BroadswordItem> WOODEN_BROADSWORD = register("wooden_broadsword", () -> new BroadswordItem(Tiers.WOOD, 6f, 1.0f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BroadswordItem> STONE_BROADSWORD = register("stone_broadsword", () -> new BroadswordItem(Tiers.STONE, 8f, 1.0f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BroadswordItem> IRON_BROADSWORD = register("iron_broadsword", () -> new BroadswordItem(Tiers.IRON, 10f, 1.0f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BroadswordItem> GOLDEN_BROADSWORD = register("golden_broadsword", () -> new BroadswordItem(Tiers.GOLD, 6f, 1.0f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BroadswordItem> DIAMOND_BROADSWORD = register("diamond_broadsword", () -> new BroadswordItem(Tiers.DIAMOND, 12f, 1.0f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BroadswordItem> NETHERITE_BROADSWORD = register("netherite_broadsword", () -> new BroadswordItem(Tiers.NETHERITE, 14f, 1.0f, new Item.Properties().stacksTo(1).fireResistant()));

    // Rapiers are close-range weapons with a very quick attack speed.
    public static final RegistryObject<RapierItem> WOODEN_RAPIER = register("wooden_rapier", () -> new RapierItem(Tiers.WOOD, 2f, 3f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<RapierItem> STONE_RAPIER = register("stone_rapier", () -> new RapierItem(Tiers.STONE, 2f, 3.25f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<RapierItem> IRON_RAPIER = register("iron_rapier", () -> new RapierItem(Tiers.IRON, 3f, 3f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<RapierItem> GOLDEN_RAPIER = register("golden_rapier", () -> new RapierItem(Tiers.GOLD, 2f, 3f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<RapierItem> DIAMOND_RAPIER = register("diamond_rapier", () -> new RapierItem(Tiers.DIAMOND, 3f, 3.5f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<RapierItem> NETHERITE_RAPIER = register("netherite_rapier", () -> new RapierItem(Tiers.NETHERITE, 4f, 4f, new Item.Properties().stacksTo(1).fireResistant()));

    public static final RegistryObject<HaladieItem> WOODEN_HALADIE = register("wooden_haladie", () -> new HaladieItem(Tiers.WOOD, 2f, 1.4f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<HaladieItem> STONE_HALADIE = register("stone_haladie", () -> new HaladieItem(Tiers.STONE, 3f, 1.4f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<HaladieItem> IRON_HALADIE = register("iron_haladie", () -> new HaladieItem(Tiers.IRON, 4f, 1.3f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<HaladieItem> GOLDEN_HALADIE = register("golden_haladie", () -> new HaladieItem(Tiers.GOLD, 2f, 1.2f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<HaladieItem> DIAMOND_HALADIE = register("diamond_haladie", () -> new HaladieItem(Tiers.DIAMOND, 5f, 1.2f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<HaladieItem> NETHERITE_HALADIE = register("netherite_haladie", () -> new HaladieItem(Tiers.NETHERITE, 6f, 1.2f, new Item.Properties().stacksTo(1).fireResistant()));

    public static final RegistryObject<WaraxeItem> WOODEN_WARAXE = register("wooden_waraxe", () -> new WaraxeItem(Tiers.WOOD, 6, .5f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<WaraxeItem> STONE_WARAXE = register("stone_waraxe", () -> new WaraxeItem(Tiers.STONE, 8, .5f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<WaraxeItem> IRON_WARAXE = register("iron_waraxe", () -> new WaraxeItem(Tiers.IRON, 11, .5f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<WaraxeItem> GOLDEN_WARAXE = register("golden_waraxe", () -> new WaraxeItem(Tiers.GOLD, 6, .5f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<WaraxeItem> DIAMOND_WARAXE = register("diamond_waraxe", () -> new WaraxeItem(Tiers.DIAMOND, 13, .5f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<WaraxeItem> NETHERITE_WARAXE = register("netherite_waraxe", () -> new WaraxeItem(Tiers.NETHERITE, 15, .5f, new Item.Properties().stacksTo(1).fireResistant()));

    public static final RegistryObject<KatanaItem> WOODEN_KATANA = register("wooden_katana", () -> new KatanaItem(Tiers.WOOD, 5, 1.3f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<KatanaItem> STONE_KATANA = register("stone_katana", () -> new KatanaItem(Tiers.STONE, 6, 1.3f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<KatanaItem> IRON_KATANA = register("iron_katana", () -> new KatanaItem(Tiers.IRON, 8, 1.4f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<KatanaItem> GOLDEN_KATANA = register("golden_katana", () -> new KatanaItem(Tiers.GOLD, 5, 1.4f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<KatanaItem> DIAMOND_KATANA = register("diamond_katana", () -> new KatanaItem(Tiers.DIAMOND, 8, 1.4f, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<KatanaItem> NETHERITE_KATANA = register("netherite_katana", () -> new KatanaItem(Tiers.NETHERITE, 9, 1.5f, new Item.Properties().stacksTo(1).fireResistant()));

    public static final RegistryObject<CustomBowItem> STONE_BOW = register("stone_bow", () -> new CustomBowItem(Tiers.STONE, new Item.Properties().stacksTo(1).durability(425), 30.0F, 1.0));
    public static final RegistryObject<CustomBowItem> IRON_BOW = register("iron_bow", () -> new CustomBowItem(Tiers.IRON, new Item.Properties().stacksTo(1).durability(750), 25.0F, 1.1));
    public static final RegistryObject<CustomBowItem> GOLDEN_BOW = register("golden_bow", () -> new CustomBowItem(Tiers.GOLD, new Item.Properties().stacksTo(1).durability(150), 10.0F, 1.0, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GOLD_BLOCK.defaultBlockState())));
    public static final RegistryObject<CustomBowItem> DIAMOND_BOW = register("diamond_bow", () -> new CustomBowItem(Tiers.DIAMOND, new Item.Properties().stacksTo(1).durability(1561), 20.0F, 1.25, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIAMOND_BLOCK.defaultBlockState())));
    public static final RegistryObject<CustomBowItem> NETHERITE_BOW = register("netherite_bow", () -> new CustomBowItem(Tiers.NETHERITE, new Item.Properties().stacksTo(1).durability(2031).fireResistant(), 15.0F, 1.5, ParticleTypes.SOUL_FIRE_FLAME));

    public static final RegistryObject<CustomShieldItem> STONE_SHIELD = register("stone_shield", () -> new CustomShieldItem(new Item.Properties().durability(425)));
    public static final RegistryObject<CustomShieldItem> IRON_SHIELD = register("iron_shield", () -> new CustomShieldItem(new Item.Properties().durability(750)));
    public static final RegistryObject<CustomShieldItem> GOLDEN_SHIELD = register("golden_shield", () -> new CustomShieldItem(new Item.Properties().durability(150)));
    public static final RegistryObject<CustomShieldItem> DIAMOND_SHIELD = register("diamond_shield", () -> new CustomShieldItem(new Item.Properties().durability(1561)));
    public static final RegistryObject<CustomShieldItem> NETHERITE_SHIELD = register("netherite_shield", () -> new CustomShieldItem(new Item.Properties().durability(2031).fireResistant()));

    // Yo-Yos are fun ranged weapons.
    public static final RegistryObject<YoyoItem> WOODEN_YOYO = register("wooden_yoyo", () -> new YoyoItem(new Item.Properties().stacksTo(1), Tiers.WOOD));
    public static final RegistryObject<YoyoItem> STONE_YOYO = register("stone_yoyo", () -> new YoyoItem(new Item.Properties().stacksTo(1), Tiers.STONE));
    public static final RegistryObject<YoyoItem> IRON_YOYO = register("iron_yoyo", () -> new YoyoItem(new Item.Properties().stacksTo(1), Tiers.IRON));
    public static final RegistryObject<YoyoItem> GOLDEN_YOYO = register("golden_yoyo", () -> new YoyoItem(new Item.Properties().stacksTo(1), Tiers.GOLD));
    public static final RegistryObject<YoyoItem> DIAMOND_YOYO = register("diamond_yoyo", () -> new YoyoItem(new Item.Properties().stacksTo(1), Tiers.DIAMOND));
    public static final RegistryObject<YoyoItem> NETHERITE_YOYO = register("netherite_yoyo", () -> new YoyoItem(new Item.Properties().stacksTo(1).fireResistant(), Tiers.NETHERITE));

    // Boomerangs are high-skill medium-ranged weapons.
    public static final RegistryObject<BoomerangItem> WOODEN_BOOMERANG = register("wooden_boomerang", () -> new BoomerangItem(new Item.Properties().stacksTo(1).durability(150), Tiers.WOOD));
    public static final RegistryObject<BoomerangItem> STONE_BOOMERANG = register("stone_boomerang", () -> new BoomerangItem(new Item.Properties().stacksTo(1).durability(425), Tiers.STONE));
    public static final RegistryObject<BoomerangItem> IRON_BOOMERANG = register("iron_boomerang", () -> new BoomerangItem(new Item.Properties().stacksTo(1).durability(750), Tiers.IRON));
    public static final RegistryObject<BoomerangItem> GOLDEN_BOOMERANG = register("golden_boomerang", () -> new BoomerangItem(new Item.Properties().stacksTo(1).durability(150), Tiers.GOLD));
    public static final RegistryObject<BoomerangItem> DIAMOND_BOOMERANG = register("diamond_boomerang", () -> new BoomerangItem(new Item.Properties().stacksTo(1).durability(1561), Tiers.DIAMOND));
    public static final RegistryObject<BoomerangItem> NETHERITE_BOOMERANG = register("netherite_boomerang", () -> new BoomerangItem(new Item.Properties().stacksTo(1).durability(2031).fireResistant(), Tiers.NETHERITE));

    public static final RegistryObject<Item> EXTENDED_STICK = register("extended_stick", () -> new Item(new Item.Properties()));

    public static final List<RegistryObject<? extends Item>> CREATIVE_TAB_ITEMS = List.of(
            WOODEN_DAGGER, STONE_DAGGER, IRON_DAGGER, GOLDEN_DAGGER, DIAMOND_DAGGER, NETHERITE_DAGGER,
            WOODEN_SPEAR, STONE_SPEAR, IRON_SPEAR, GOLDEN_SPEAR, DIAMOND_SPEAR, NETHERITE_SPEAR,
            WOODEN_BROADSWORD, STONE_BROADSWORD, IRON_BROADSWORD, GOLDEN_BROADSWORD, DIAMOND_BROADSWORD, NETHERITE_BROADSWORD,
            WOODEN_RAPIER, STONE_RAPIER, IRON_RAPIER, GOLDEN_RAPIER, DIAMOND_RAPIER, NETHERITE_RAPIER,
            WOODEN_HALADIE, STONE_HALADIE, IRON_HALADIE, GOLDEN_HALADIE, DIAMOND_HALADIE, NETHERITE_HALADIE,
            WOODEN_WARAXE, STONE_WARAXE, IRON_WARAXE, GOLDEN_WARAXE, DIAMOND_WARAXE, NETHERITE_WARAXE,
            WOODEN_KATANA, STONE_KATANA, IRON_KATANA, GOLDEN_KATANA, DIAMOND_KATANA, NETHERITE_KATANA,
            STONE_BOW, IRON_BOW, GOLDEN_BOW, DIAMOND_BOW, NETHERITE_BOW,
            STONE_SHIELD, IRON_SHIELD, GOLDEN_SHIELD, DIAMOND_SHIELD, NETHERITE_SHIELD,
            WOODEN_YOYO, STONE_YOYO, IRON_YOYO, GOLDEN_YOYO, DIAMOND_YOYO, NETHERITE_YOYO,
            WOODEN_BOOMERANG, STONE_BOOMERANG, IRON_BOOMERANG, GOLDEN_BOOMERANG, DIAMOND_BOOMERANG, NETHERITE_BOOMERANG,
            EXTENDED_STICK
    );

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

    public static void init() {
        // NO-OP; referencing this class initializes the deferred entries.
    }

    private GOBItems() {
    }
}
