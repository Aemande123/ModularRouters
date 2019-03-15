package me.desht.modularrouters.logic.compiled;

import me.desht.modularrouters.block.tile.TileEntityItemRouter;
import me.desht.modularrouters.config.ConfigHandler;
import me.desht.modularrouters.core.ObjectRegistry;
import me.desht.modularrouters.item.module.ItemModule;
import me.desht.modularrouters.logic.ModuleTarget;
import me.desht.modularrouters.util.ModuleHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;

public class CompiledFlingerModule extends CompiledDropperModule {
    public static final String NBT_SPEED = "Speed";
    public static final String NBT_PITCH = "Pitch";
    public static final String NBT_YAW = "Yaw";

    private final float speed, pitch, yaw;

    public CompiledFlingerModule(TileEntityItemRouter router, ItemStack stack) {
        super(router, stack);

        NBTTagCompound compound = ModuleHelper.validateNBT(stack);
        for (String key : new String[] { NBT_SPEED, NBT_PITCH, NBT_YAW }) {
            if (!compound.contains(key)) {
                compound.putFloat(key, 0.0f);
            }
        }

        speed = compound.getFloat(NBT_SPEED);
        pitch = compound.getFloat(NBT_PITCH);
        yaw = compound.getFloat(NBT_YAW);
    }

    @Override
    public boolean execute(@Nonnull TileEntityItemRouter router) {
        boolean fired = super.execute(router);

        if (fired && ConfigHandler.MODULE.flingerEffects.get()) {
            ModuleTarget t = getTarget();
            int n = Math.round(speed * 5);
            if (router.getUpgradeCount(ObjectRegistry.MUFFLER_UPGRADE) < 2) {
                ((WorldServer) router.getWorld()).spawnParticle(Particles.LARGE_SMOKE,
                        t.pos.getX() + 0.5, t.pos.getY() + 0.5, t.pos.getZ() + 0.5, n,
                        0.0, 0.0, 0.0, 0.0);
            }
            router.playSound(null, t.pos, ObjectRegistry.SOUND_THUD, SoundCategory.BLOCKS, 0.5f + speed, 1.0f);
        }

        return fired;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    protected void setupItemVelocity(TileEntityItemRouter router, EntityItem item) {
        EnumFacing routerFacing = router.getAbsoluteFacing(ItemModule.RelativeDirection.FRONT);
        float basePitch = 0.0f;
        float baseYaw;
        switch (getDirection()) {
            case UP:
                basePitch = 90.0f;
                baseYaw = yawFromFacing(routerFacing);
                break;
            case DOWN:
                basePitch = -90.0f;
                baseYaw = yawFromFacing(routerFacing);
                break;
            default:
                baseYaw = yawFromFacing(getFacing());
                break;
        }

        double yawRad = Math.toRadians(baseYaw + yaw), pitchRad = Math.toRadians(basePitch + pitch);

        double x = (Math.cos(yawRad) * Math.cos(pitchRad));   // east is positive X
        double y = Math.sin(pitchRad);
        double z = -(Math.sin(yawRad) * Math.cos(pitchRad));  // north is negative Z

        item.motionX = x * speed;
        item.motionY = y * speed;
        item.motionZ = z* speed;
    }

    private float yawFromFacing(EnumFacing absoluteFacing) {
        switch (absoluteFacing) {
            case EAST: return 0.0f;
            case NORTH: return 90.0f;
            case WEST: return 180.0f;
            case SOUTH: return 270.0f;
        }
        return 0;
    }
}
