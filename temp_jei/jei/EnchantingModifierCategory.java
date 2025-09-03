package com.breakinblocks.beer.compat.jei;

import com.breakinblocks.beer.compat.BeerJEIPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EnchantingModifierCategory implements IRecipeCategory<EnchantingModifierRecipe> {

    public static final RecipeType<EnchantingModifierRecipe> RECIPE_TYPE = 
        RecipeType.create("beer", "enchanting_modifiers", EnchantingModifierRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public EnchantingModifierCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(150, 80);
        this.icon = guiHelper.createDrawableIngredient(net.minecraft.world.item.Items.ENCHANTING_TABLE);
        this.title = Component.translatable("beer.jei.category.enchanting_modifiers");
    }

    @Override
    public RecipeType<EnchantingModifierRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EnchantingModifierRecipe recipe, IFocusGroup focuses) {
        // Main modifier item slot
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 5)
               .addItemStack(recipe.getModifierItem());

        // Enchanting table icon
        builder.addSlot(RecipeIngredientRole.CATALYST, 35, 5)
               .addItemStack(new ItemStack(Items.ENCHANTING_TABLE));

        // If not decrease mode, show the quartz as optional offhand item
        if (recipe.getAxis() != EnchantingModifierRecipe.ModifierAxis.DECREASE) {
            builder.addSlot(RecipeIngredientRole.INPUT, 65, 5)
                   .addItemStack(new ItemStack(Items.QUARTZ))
                   .addTooltipCallback((view, tooltip) -> {
                       tooltip.add(Component.translatable("beer.jei.modifier.offhand_decrease"));
                   });
        }
    }

    @Override
    public void draw(EnchantingModifierRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        // Draw the effect name with appropriate color
        int color = recipe.getAxis().getColor();
        guiGraphics.drawString(font, recipe.getEffectName(), 5, 35, color, false);

        // Draw description
        Component description = Component.translatable(recipe.getDescriptionKey());
        
        // Word wrap the description if it's too long
        String descText = description.getString();
        if (descText.length() > 25) {
            String[] words = descText.split(" ");
            StringBuilder line1 = new StringBuilder();
            StringBuilder line2 = new StringBuilder();
            int charCount = 0;
            boolean firstLine = true;
            
            for (String word : words) {
                if (charCount + word.length() <= 25 && firstLine) {
                    if (line1.length() > 0) line1.append(" ");
                    line1.append(word);
                    charCount += word.length() + 1;
                } else {
                    firstLine = false;
                    if (line2.length() > 0) line2.append(" ");
                    line2.append(word);
                }
            }
            
            guiGraphics.drawString(font, line1.toString(), 5, 50, 0x666666, false);
            if (line2.length() > 0) {
                guiGraphics.drawString(font, line2.toString(), 5, 62, 0x666666, false);
            }
        } else {
            guiGraphics.drawString(font, descText, 5, 50, 0x666666, false);
        }

        // Draw instruction text
        String instruction = "Shift + Right-click";
        guiGraphics.drawString(font, instruction, 5, 70, 0x999999, false);
    }
}