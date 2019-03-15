package me.desht.modularrouters.logic.compiled;

import me.desht.modularrouters.block.tile.TileEntityItemRouter;
import me.desht.modularrouters.item.module.TargetedModule;
import me.desht.modularrouters.logic.ModuleTarget;
import me.desht.modularrouters.util.InventoryUtils;
import me.desht.modularrouters.util.MiscUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandler;

import java.util.Collections;
import java.util.List;

public class CompiledSenderModule2 extends CompiledSenderModule1 {
    public CompiledSenderModule2(TileEntityItemRouter router, ItemStack stack) {
        super(router, stack);
    }

    @Override
    protected List<ModuleTarget> setupTarget(TileEntityItemRouter router, ItemStack stack) {
        return Collections.singletonList(TargetedModule.getTarget(stack, !router.getWorld().isRemote));
    }

    @Override
    protected PositionedItemHandler findTargetInventory(TileEntityItemRouter router) {
        ModuleTarget target = getActualTarget(router);
        if (target == null || !validate(router, target)) {
            return null;
        }

        WorldServer w = MiscUtil.getWorldForDimensionId(target.dimId);
        if (w != null && w.getChunkProvider().chunkExists(target.pos.getX() >> 4, target.pos.getZ() >> 4)) {
            IItemHandler handler = InventoryUtils.getInventory(w, target.pos, target.face);
            return handler == null ? null : new PositionedItemHandler(target.pos, handler);
        }

        return null;
    }

    private boolean validate(TileEntityItemRouter router, ModuleTarget target) {
        return !(isRangeLimited() &&
                (MiscUtil.getDimensionForWorld(router.getWorld()) != target.dimId
                || router.getPos().distanceSq(target.pos) > getRangeSquared()));

    }

    boolean isRangeLimited() {
        return true;
    }

    @Override
    public ModuleTarget getActualTarget(TileEntityItemRouter router) {
        return getTarget();
    }
}
