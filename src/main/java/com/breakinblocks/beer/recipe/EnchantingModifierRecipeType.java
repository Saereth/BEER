package com.breakinblocks.beer.recipe;

import com.breakinblocks.beer.compat.EnchantingModifierRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class EnchantingModifierRecipeType implements Recipe<RecipeInput> {
    public static final Serializer SERIALIZER = new Serializer();
    
    private final Ingredient mainhandInput;
    private final Ingredient offhandInput;
    private final boolean consumesOffhand;
    private final String effectKey;
    private final String descriptionKey;
    private final EnchantingModifierRecipe.ModifierType modifierType;

    public EnchantingModifierRecipeType(Ingredient mainhandInput, Ingredient offhandInput, boolean consumesOffhand,
                                       String effectKey, String descriptionKey, EnchantingModifierRecipe.ModifierType modifierType) {
        this.mainhandInput = mainhandInput;
        this.offhandInput = offhandInput;
        this.consumesOffhand = consumesOffhand;
        this.effectKey = effectKey;
        this.descriptionKey = descriptionKey;
        this.modifierType = modifierType;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        // Allow recipe to be recognized by the recipe manager for datapack loading
        // This recipe defines valid enchanting table modifier combinations
        return true;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        // This recipe doesn't use a crafting grid, so dimensions don't matter
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BeerRecipes.ENCHANTING_MODIFIER_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(mainhandInput);
        if (offhandInput != Ingredient.EMPTY) {
            ingredients.add(offhandInput);
        }
        return ingredients;
    }

    // Convert to JEI recipe format
    public EnchantingModifierRecipe toJEIRecipe() {
        return new EnchantingModifierRecipe(mainhandInput, offhandInput, consumesOffhand, effectKey, descriptionKey, modifierType);
    }

    // Getters
    public Ingredient getMainhandInput() { return mainhandInput; }
    public Ingredient getOffhandInput() { return offhandInput; }
    public boolean consumesOffhand() { return consumesOffhand; }
    public String getEffectKey() { return effectKey; }
    public String getDescriptionKey() { return descriptionKey; }
    public EnchantingModifierRecipe.ModifierType getModifierType() { return modifierType; }

    public static class Serializer implements RecipeSerializer<EnchantingModifierRecipeType> {
        
        public static final MapCodec<EnchantingModifierRecipeType> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("mainhand_input").forGetter(EnchantingModifierRecipeType::getMainhandInput),
                Ingredient.CODEC.optionalFieldOf("offhand_input", Ingredient.EMPTY).forGetter(EnchantingModifierRecipeType::getOffhandInput),
                Codec.BOOL.optionalFieldOf("consumes_offhand", false).forGetter(EnchantingModifierRecipeType::consumesOffhand),
                Codec.STRING.fieldOf("effect_key").forGetter(EnchantingModifierRecipeType::getEffectKey),
                Codec.STRING.fieldOf("description_key").forGetter(EnchantingModifierRecipeType::getDescriptionKey),
                Codec.stringResolver(EnchantingModifierRecipe.ModifierType::name, EnchantingModifierRecipe.ModifierType::valueOf)
                    .fieldOf("modifier_type").forGetter(EnchantingModifierRecipeType::getModifierType)
            ).apply(instance, (mainhand, offhand, consumes, effect, desc, type) -> {
                com.mojang.logging.LogUtils.getLogger().info("[BEER] Creating EnchantingModifierRecipeType: {} ({})", effect, type);
                return new EnchantingModifierRecipeType(mainhand, offhand, consumes, effect, desc, type);
            })
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, EnchantingModifierRecipeType> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, EnchantingModifierRecipeType::getMainhandInput,
            Ingredient.CONTENTS_STREAM_CODEC, EnchantingModifierRecipeType::getOffhandInput,
            ByteBufCodecs.BOOL, EnchantingModifierRecipeType::consumesOffhand,
            ByteBufCodecs.STRING_UTF8, EnchantingModifierRecipeType::getEffectKey,
            ByteBufCodecs.STRING_UTF8, EnchantingModifierRecipeType::getDescriptionKey,
            ByteBufCodecs.STRING_UTF8.map(EnchantingModifierRecipe.ModifierType::valueOf, EnchantingModifierRecipe.ModifierType::name), EnchantingModifierRecipeType::getModifierType,
            EnchantingModifierRecipeType::new
        );

        @Override
        public MapCodec<EnchantingModifierRecipeType> codec() {
            com.mojang.logging.LogUtils.getLogger().info("[BEER] EnchantingModifierRecipeType codec() called");
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EnchantingModifierRecipeType> streamCodec() {
            com.mojang.logging.LogUtils.getLogger().info("[BEER] EnchantingModifierRecipeType streamCodec() called");
            return STREAM_CODEC;
        }
    }
}