package micdoodle8.mods.galacticraft.core.asm;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.mixins.Mixins;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Holds the rest of GC's class patching code.
 */
@TransformerExclusions("micdoodle8.mods.galacticraft.core.asm")
@MCVersion("1.7.10")
@Name(Constants.COREMOD_NAME_SIMPLE)
@SortingIndex(1500)
public class GCLoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

    static final Logger LOGGER = LogManager.getLogger(Constants.COREMOD_NAME_SIMPLE);
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
        // noinspection ResultOfMethodCallIgnored
        debugOutputDir.mkdir();
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String getMixinConfig() {
        return "mixins.GalacticraftCore.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        final List<String> mixins = new ArrayList<>();
        final List<String> notLoading = new ArrayList<>();
        for (final Mixins mixin : Mixins.values()) {
            if (mixin.phase == Mixins.Phase.EARLY) {
                if (mixin.shouldLoad(loadedCoreMods, Collections.emptySet())) {
                    mixins.addAll(mixin.mixinClasses);
                } else {
                    notLoading.addAll(mixin.mixinClasses);
                }
            }
        }
        LOGGER.info("Not loading the following EARLY mixins: {}", notLoading.toString());
        return mixins;
    }
}
