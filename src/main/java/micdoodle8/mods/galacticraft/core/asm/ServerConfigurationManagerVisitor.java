package micdoodle8.mods.galacticraft.core.asm;

import static micdoodle8.mods.galacticraft.core.asm.GCLoadingPlugin.dev;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.NEW;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * This cannot be a mixin, as it'd otherwise break on thermos server
 *
 * Scrubs ServerConfigurationManager for new EntityPlayerMP and replace it with new GCEntityPlayerMP
 */
public class ServerConfigurationManagerVisitor extends ClassVisitor {

    public ServerConfigurationManagerVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new EntityPlayerMPReplacer(api, super.visitMethod(access, name, desc, signature, exceptions), name);
    }

    private static class EntityPlayerMPReplacer extends MethodVisitor {
        private static final String REPLACEMENT_CLASS_INTERNAL_NAME =
                "micdoodle8/mods/galacticraft/core/entities/player/GCEntityPlayerMP";
        private final String methodName;

        public EntityPlayerMPReplacer(int api, MethodVisitor mv, String methodName) {
            super(api, mv);
            this.methodName = methodName;
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            if (opcode == NEW
                    && (dev ? "net/minecraft/entity/player/EntityPlayerMP".equals(type) : "mw".equals(type))) {
                GCLoadingPlugin.LOGGER.debug(
                        "Replacing NEW {} with NEW micdoodle8/mods/galacticraft/core/entities/player/GCEntityPlayerMP in {}",
                        type,
                        methodName);
                super.visitTypeInsn(NEW, REPLACEMENT_CLASS_INTERNAL_NAME);
            } else super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (opcode == INVOKESPECIAL
                    && "<init>".equals(name)
                    && (dev ? "net/minecraft/entity/player/EntityPlayerMP".equals(owner) : "mw".equals(owner))) {
                GCLoadingPlugin.LOGGER.debug(
                        "Replacing INVOKESPECIAL {}{}{} with NEW micdoodle8/mods/galacticraft/core/entities/player/GCEntityPlayerMP{}{} in {}",
                        owner,
                        name,
                        desc,
                        name,
                        desc,
                        methodName);
                super.visitMethodInsn(opcode, REPLACEMENT_CLASS_INTERNAL_NAME, name, desc, itf);
            } else super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
}
