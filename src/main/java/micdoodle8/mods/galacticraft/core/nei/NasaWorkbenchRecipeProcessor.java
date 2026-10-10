package micdoodle8.mods.galacticraft.core.nei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.github.vfyjxf.nee.processor.IRecipeProcessor;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.common.Optional;

@Optional.Interface(
        iface = "com.github.vfyjxf.nee.processor.IRecipeProcessor",
        modid = NasaWorkbenchNeeSupport.MOD_ID_NEE,
        striprefs = true)
public class NasaWorkbenchRecipeProcessor implements IRecipeProcessor {

    private final String processorId;
    private final Set<String> overlayIdentifiers;

    public NasaWorkbenchRecipeProcessor(String processorId, String... overlayIdentifiers) {
        this.processorId = processorId;
        this.overlayIdentifiers = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(overlayIdentifiers)));
    }

    @Nonnull
    @Override
    @Optional.Method(modid = NasaWorkbenchNeeSupport.MOD_ID_NEE)
    public Set<String> getAllOverlayIdentifier() {
        return this.overlayIdentifiers;
    }

    @Nonnull
    @Override
    @Optional.Method(modid = NasaWorkbenchNeeSupport.MOD_ID_NEE)
    public String getRecipeProcessorId() {
        return this.processorId;
    }

    @Nonnull
    @Override
    @Optional.Method(modid = NasaWorkbenchNeeSupport.MOD_ID_NEE)
    public List<PositionedStack> getRecipeInput(IRecipeHandler recipe, int recipeIndex, String identifier) {
        final List<PositionedStack> ingredients = recipe.getIngredientStacks(recipeIndex);

        if (ingredients == null) {
            return new ArrayList<>();
        }

        final List<PositionedStack> inputs = new ArrayList<>(ingredients.size());

        for (final PositionedStack ingredient : ingredients) {
            if (ingredient != null && ingredient.item != null) {
                inputs.add(ingredient);
            }
        }

        return inputs;
    }

    @Nonnull
    @Override
    @Optional.Method(modid = NasaWorkbenchNeeSupport.MOD_ID_NEE)
    public List<PositionedStack> getRecipeOutput(IRecipeHandler recipe, int recipeIndex, String identifier) {
        final List<PositionedStack> outputs = new ArrayList<>(1);
        final PositionedStack result = recipe.getResultStack(recipeIndex);

        if (result != null && result.item != null) {
            outputs.add(result);
        }

        return outputs;
    }

    @Override
    @Optional.Method(modid = NasaWorkbenchNeeSupport.MOD_ID_NEE)
    public boolean mergeStacks(IRecipeHandler recipe, int recipeIndex, String identifier) {
        return true;
    }
}
