package micdoodle8.mods.galacticraft.core.asm;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.mixins.Mixins;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.Name(Constants.COREMOD_NAME_SIMPLE)
public class GCLoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

    private static final Logger LOGGER = LogManager.getLogger(Constants.COREMOD_NAME_SIMPLE);

    @Override
    public String[] getASMTransformerClass() {
        return null;
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
    public void injectData(Map<String, Object> data) {}

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
