package micdoodle8.mods.galacticraft.core.nei;

import com.github.vfyjxf.nee.processor.RecipeProcessor;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

public class NasaWorkbenchNeeSupport {

    public static final String MOD_ID_NEE = "neenergistics";

    private static boolean registered = false;

    public static void register() {
        if (registered) {
            return;
        }

        registered = true;
        if (!Loader.isModLoaded(MOD_ID_NEE)) return;
        registerProcessor(
                "Galacticraft",
                "galacticraft.rocketT1",
                "galacticraft.rocketT2",
                "galacticraft.rocketT3",
                "galacticraft.buggy",
                "galacticraft.cargoRocket",
                "galacticraft.astroMiner");
    }

    @Optional.Method(modid = NasaWorkbenchNeeSupport.MOD_ID_NEE)
    public static void registerProcessor(String processorId, String... overlayIdentifiers) {
        RecipeProcessor.recipeProcessors.add(new NasaWorkbenchRecipeProcessor(processorId, overlayIdentifiers));
    }
}
