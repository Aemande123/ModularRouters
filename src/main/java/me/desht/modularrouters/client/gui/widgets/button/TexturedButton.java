package me.desht.modularrouters.client.gui.widgets.button;

import com.mojang.blaze3d.platform.GlStateManager;
import me.desht.modularrouters.ModularRouters;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.ArrayList;
import java.util.List;

public abstract class TexturedButton extends GuiButtonExt implements ITooltipButton {
    static final ResourceLocation resourceLocation = new ResourceLocation(ModularRouters.MODID, "textures/gui/widgets.png");
    protected final List<String> tooltip1;

    public TexturedButton(int x, int y, int width, int height, IPressable pressable) {
        super(x, y, width, height, "", pressable);
        this.tooltip1 = new ArrayList<>();
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getYImage(this.isHovered);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            if (drawStandardBackground()) {
                this.blit(this.x, this.y, i * 16, 0, this.width, this.height);
            }
            this.blit(this.x, this.y, getTextureX(), getTextureY(), this.width, this.height);
        }
    }

    protected boolean drawStandardBackground() {
        return true;
    }

    protected abstract int getTextureX();

    protected abstract int getTextureY();

    public List<String> getTooltip() {
        return tooltip1;
    }
}
