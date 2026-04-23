package com.breakinblocks.beer.compat;

import com.breakinblocks.beer.Config;
import com.breakinblocks.beer.recipe.EnchantingModifierRecipeType;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import static com.breakinblocks.beer.Beer.rl;

public class EnchantingTableCategory implements IRecipeCategory<EnchantingModifierRecipeType> {
    public static final IRecipeType<EnchantingModifierRecipeType> TYPE =
        IRecipeType.create("beer", "enchanting_modifiers", EnchantingModifierRecipeType.class);

    public static final Identifier TEXTURES = rl("textures/gui/enchanting_jei.png");

    private static final int WIDTH = 169;
    private static final int HEIGHT = 75;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable bgSheet;
    private final IDrawable singleInputOverlay;
    private final IDrawable tableIcon;
    private final IDrawable xpBottleIcon;
    private final Component title;

    public EnchantingTableCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableItemStack(new ItemStack(Blocks.ENCHANTING_TABLE));
        this.bgSheet = helper.drawableBuilder(TEXTURES, 0, 0, WIDTH, HEIGHT).setTextureSize(256, 256).build();
        this.singleInputOverlay = helper.drawableBuilder(TEXTURES, 0, 88, 28, 34).setTextureSize(256, 256).build();
        this.tableIcon = helper.createDrawableItemStack(new ItemStack(Blocks.ENCHANTING_TABLE));
        this.xpBottleIcon = helper.createDrawableItemStack(new ItemStack(Items.EXPERIENCE_BOTTLE));
        this.title = Component.translatable("beer.jei.category.enchanting_modifiers");
    }

    @Override
    public @NotNull IRecipeType<EnchantingModifierRecipeType> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EnchantingModifierRecipeType recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 11)
               .add(recipe.getMainhandInput());

        recipe.getOffhandInput().ifPresent(offhand ->
            builder.addSlot(RecipeIngredientRole.INPUT, 11, 48)
                   .add(offhand)
        );

        builder.addInvisibleIngredients(RecipeIngredientRole.CRAFTING_STATION)
               .add(new ItemStack(Blocks.ENCHANTING_TABLE));

        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
               .add(new ItemStack(Blocks.ENCHANTING_TABLE));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, EnchantingModifierRecipeType recipe, IFocusGroup focuses) {
        builder.addDrawable(bgSheet, 0, 0);
        if (recipe.getOffhandInput().isEmpty()) {
            builder.addDrawable(singleInputOverlay, 1, 31);
        }

        builder.addDrawable(tableIcon, 31, 29);

        Component effectText = Component.literal(recipe.getEffectKey())
            .withStyle(style -> style.withColor(0xFF000000));
        builder.addText(effectText, WIDTH, 10).setPosition(0, 34);

        if (Config.enableXpCosts) {
            boolean isDecrease = recipe.getOffhandInput().isPresent();
            builder.addDrawable(xpBottleIcon, 75, 50);

            Component xpText = isDecrease
                ? Component.translatable("beer.jei.modifier.xp_gain", Config.xpCostPerModifier)
                    .withStyle(ChatFormatting.GREEN)
                : Component.translatable("beer.jei.modifier.xp_cost", Config.xpCostPerModifier)
                    .withStyle(ChatFormatting.RED);

            builder.addText(xpText, 60, 10).setPosition(95, 54);
        }
    }
}
