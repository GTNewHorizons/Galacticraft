package micdoodle8.mods.galacticraft.core.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import java.io.File;
import java.util.Map;

/**
 * Holds the rest of GC's class patching code.
 */
@IFMLLoadingPlugin.TransformerExclusions("micdoodle8.mods.galacticraft.core.asm")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.Name("GalacticraftCoreMod2")
@SortingIndex(1500)
public class GCLoadingPlugin implements IFMLLoadingPlugin {
    static boolean dev;
    static File debugOutputDir;

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {"micdoodle8.mods.galacticraft.core.asm.GCTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        dev = !(boolean) data.get("runtimeDeobfuscationEnabled");
        debugOutputDir = new File((File) data.get("mcLocation"), ".asm");
        //noinspection ResultOfMethodCallIgnored
        debugOutputDir.mkdir();
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
