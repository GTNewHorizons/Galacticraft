package micdoodle8.mods.galacticraft.core.nei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class IngotCompressorRecipeHandler extends TemplateRecipeHandler {

    private static final ResourceLocation ingotCompressorTexture = new ResourceLocation(
            GalacticraftCore.ASSET_PREFIX,
            "textures/gui/ingotCompressor.png");
    private static int ticksPassed;

    public String getRecipeId() {
        return "galacticraft.ingotcompressor";
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    public Set<Entry<ArrayList<PositionedStack>, PositionedStack>> getRecipes() {
        final HashMap<ArrayList<PositionedStack>, PositionedStack> recipes = new HashMap<>();

        for (final Entry<HashMap<Integer, PositionedStack>, PositionedStack> stack : NEIGalacticraftConfig
                .getIngotCompressorRecipes()) {
            final ArrayList<PositionedStack> inputStacks = new ArrayList<>();

            for (final Map.Entry<Integer, PositionedStack> input : stack.getKey().entrySet()) {
                inputStacks.add(input.getValue());
            }

            recipes.put(inputStacks, stack.getValue());
        }

        return recipes.entrySet();
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(IngotCompressorRecipeHandler.ingotCompressorTexture);
        GuiDraw.drawTexturedModalRect(20, 25, 18, 17, 137, 78);

        if (IngotCompressorRecipeHandler.ticksPassed % 70 > 26) {
            GuiDraw.drawTexturedModalRect(103, 36, 176, 0, 17, 13);
        }

        GuiDraw.drawTexturedModalRect(79, 44, 176, 13, Math.min(IngotCompressorRecipeHandler.ticksPassed % 70, 53), 17);

        final int yOffset = (int) Math
                .floor(IngotCompressorRecipeHandler.ticksPassed % 48 * 0.29166666666666666666666666666667D);

        GuiDraw.drawTexturedModalRect(83, 35 + yOffset, 176, 30 + yOffset, 14, 14 - yOffset);
    }

    @Override
    public void onUpdate() {
        IngotCompressorRecipeHandler.ticksPassed += 1;
        super.onUpdate();
    }

    @Override
    public void loadTransferRects() {}

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getRecipeId())) {
            for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
                this.arecipes.add(new CompressorRecipe(irecipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getValue().item, result)) {
                this.arecipes.add(new CompressorRecipe(irecipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
            for (final PositionedStack pstack : irecipe.getKey()) {
                if (pstack.contains(ingredient)) {
                    this.arecipes.add(new CompressorRecipe(irecipe));
                    break;
                }
            }
        }
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return super.newInstance();
    }

    @Override
    public ArrayList<PositionedStack> getIngredientStacks(int recipe) {
        return (ArrayList<PositionedStack>) this.arecipes.get(recipe).getIngredients();
    }

    @Override
    public PositionedStack getResultStack(int recipe) {
        if (IngotCompressorRecipeHandler.ticksPassed % 70 >= 53) {
            return this.arecipes.get(recipe).getResult();
        }

        return null;
    }

    public class CompressorRecipe extends TemplateRecipeHandler.CachedRecipe {

        public ArrayList<PositionedStack> input;
        public PositionedStack output;

        @Override
        public ArrayList<PositionedStack> getIngredients() {
            return (ArrayList<PositionedStack>) this
                    .getCycledIngredients(IngotCompressorRecipeHandler.this.cycleticks / 20, this.input);
        }

        @Override
        public PositionedStack getResult() {
            return this.output;
        }

        public CompressorRecipe(ArrayList<PositionedStack> pstack1, PositionedStack pstack2) {
            this.input = pstack1;
            this.output = pstack2;
        }

        public CompressorRecipe(Map.Entry<ArrayList<PositionedStack>, PositionedStack> recipe) {
            this(new ArrayList<>(recipe.getKey()), recipe.getValue().copy());
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            final ArrayList<PositionedStack> stacks = new ArrayList<>();
            final PositionedStack stack = this.getOtherStack();
            if (stack != null) {
                stacks.add(stack);
            }
            return stacks;
        }

        @Override
        public PositionedStack getOtherStack() {
            return FurnaceRecipeHandler.afuels
                    .get(IngotCompressorRecipeHandler.ticksPassed / 48 % FurnaceRecipeHandler.afuels.size()).stack;
        }
    }

    @Override
    public String getRecipeName() {
        return GCCoreUtil.translate("tile.machine.3.name");
    }

    @Override
    public String getGuiTexture() {
        return GalacticraftCore.TEXTURE_PREFIX + "textures/gui/ingotCompressor.png";
    }

    @Override
    public void drawForeground(int recipe) {}
}
