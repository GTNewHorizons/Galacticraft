package micdoodle8.mods.galacticraft.planets.mars.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.mars.MarsModule;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CargoRocketRecipeHandler extends TemplateRecipeHandler {
    private static final ResourceLocation cargoRocketTexture =
            new ResourceLocation(MarsModule.ASSET_PREFIX, "textures/gui/schematic_rocket_GS1_Cargo.png");
    public static final int x = -1;
    public static final int y = -12;
    public static final int tX = 3;
    public static final int tY = 4;
    public static final int w = 168;
    public static final int h = 122;

    public String getRecipeId() {
        return "galacticraft.cargoRocket";
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    public Set<Entry<ArrayList<PositionedStack>, PositionedStack>> getRecipes() {
        return NEIGalacticraftMarsConfig.getCargoBenchRecipes();
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(CargoRocketRecipeHandler.cargoRocketTexture);
        GuiDraw.drawTexturedModalRect(x, y, tX, tY, w, h);
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(264, 184, 39, 89), this.getRecipeId()));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getRecipeId())) {
            for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
                this.arecipes.add(new CachedRocketRecipe(irecipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getValue().item, result)) {
                this.arecipes.add(new CachedRocketRecipe(irecipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
            for (final PositionedStack pstack : irecipe.getKey()) {
                if (NEIServerUtils.areStacksSameTypeCrafting(ingredient, pstack.item)) {
                    this.arecipes.add(new CachedRocketRecipe(irecipe));
                    break;
                }
            }
        }
    }

    public class CachedRocketRecipe extends TemplateRecipeHandler.CachedRecipe {
        public ArrayList<PositionedStack> input;
        public PositionedStack output;

        @Override
        public ArrayList<PositionedStack> getIngredients() {
            return this.input;
        }

        @Override
        public PositionedStack getResult() {
            return this.output;
        }

        public CachedRocketRecipe(ArrayList<PositionedStack> pstack1, PositionedStack pstack2) {
            super();
            this.input = pstack1;
            this.output = pstack2;
        }

        public CachedRocketRecipe(Map.Entry<ArrayList<PositionedStack>, PositionedStack> recipe) {
            this(recipe.getKey(), recipe.getValue());
        }
    }

    @Override
    public String getRecipeName() {
        return EnumColor.INDIGO + GCCoreUtil.translate("tile.rocketWorkbench.name");
    }

    @Override
    public String getGuiTexture() {
        return MarsModule.TEXTURE_PREFIX + "textures/gui/schematic_rocket_GS1_Cargo.png";
    }

    @Override
    public void drawForeground(int recipe) {}
}
