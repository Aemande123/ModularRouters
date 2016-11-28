package me.desht.modularrouters.client.fx;

import me.desht.modularrouters.ModularRouters;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayDeque;
import java.util.Queue;

public class FXSparkle extends Particle {

    public static final ResourceLocation particles = new ResourceLocation(ModularRouters.modId + ":textures/misc/particleblob.png");

    private static final Queue<FXSparkle> queuedRenders = new ArrayDeque<>();
    private static final Queue<FXSparkle> queuedCorruptRenders = new ArrayDeque<>();

    // Queue values
    private float f;
    private float f1;
    private float f2;
    private float f3;
    private float f4;
    private float f5;

    public boolean noClip = false;

    public FXSparkle(World world, double x, double y, double z, float size, float red, float green, float blue, int m) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);

        particleRed = red;
        particleGreen = green;
        particleBlue = blue;
        particleAlpha = 0.5F; // So MC renders us on the alpha layer, value not actually used
        particleGravity = 0;
        motionX = motionY = motionZ = 0;
        particleScale *= size;
        particleMaxAge = 3 * m;
        multiplier = m;
        noClip = false;
        setSize(0.01F, 0.01F);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
    }

    public static void dispatchQueuedRenders(Tessellator tessellator) {
        ParticleRenderDispatcher.sparkleFxCount = 0;
        ParticleRenderDispatcher.fakeSparkleFxCount = 0;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
        Minecraft.getMinecraft().renderEngine.bindTexture(particles);

        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        for(FXSparkle sparkle : queuedRenders)
            sparkle.renderQueued(tessellator);
        tessellator.draw();

        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        for(FXSparkle sparkle : queuedCorruptRenders)
            sparkle.renderQueued(tessellator);
        tessellator.draw();

        queuedRenders.clear();
        queuedCorruptRenders.clear();
    }

    private void renderQueued(Tessellator tessellator) {
        if(fake)
            ParticleRenderDispatcher.fakeSparkleFxCount++;
        else ParticleRenderDispatcher.sparkleFxCount++;

        int part = particle + particleAge/multiplier;

        float var8 = part % 8 / 8.0F;
        float var9 = var8 + 0.0624375F*2;
        float var10 = part / 8 / 8.0F;
        float var11 = var10 + 0.0624375F*2;
        float var12 = 0.1F * particleScale;
        if (shrink) var12 *= (particleMaxAge-particleAge+1)/(float)particleMaxAge;
        float var13 = (float)(prevPosX + (posX - prevPosX) * f - interpPosX);
        float var14 = (float)(prevPosY + (posY - prevPosY) * f - interpPosY);
        float var15 = (float)(prevPosZ + (posZ - prevPosZ) * f - interpPosZ);
        float var16 = 1.0F;

        tessellator.getBuffer().pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1).endVertex();
        tessellator.getBuffer().pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1).endVertex();
        tessellator.getBuffer().pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1).endVertex();
        tessellator.getBuffer().pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1).endVertex();

    }

    @Override
    public void renderParticle(VertexBuffer worldRendererIn, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.f = f;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.f4 = f4;
        this.f5 = f5;

        if(corrupt)
            queuedCorruptRenders.add(this);
        else queuedRenders.add(this);
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (particleAge++ >= particleMaxAge)
            setExpired();

        motionY -= 0.04D * particleGravity;

        if (!noClip && !fake)
            wiggleAround(posX, (getEntityBoundingBox().minY + getEntityBoundingBox().maxY) / 2.0D, posZ);

        posX += motionX;
        posY += motionY;
        posZ += motionZ;

        if (slowdown) {
            motionX *= 0.908000001907348633D;
            motionY *= 0.908000001907348633D;
            motionZ *= 0.908000001907348633D;

            if (isCollided) {
                motionX *= 0.69999998807907104D;
                motionZ *= 0.69999998807907104D;
            }
        }

        if(fake && particleAge > 1)
            setExpired();
    }

    public void setGravity(float value) {
        particleGravity = value;
    }

    // Copy of Entity.pushOutOfBlocks with several important changes
    private boolean wiggleAround(double x, double y, double z)
    {
        BlockPos blockpos = new BlockPos(x, y, z);
        double d0 = x - (double)blockpos.getX();
        double d1 = y - (double)blockpos.getY();
        double d2 = z - (double)blockpos.getZ();

        // Botania - change collision box items check to !airblock check
        if (!worldObj.isAirBlock(blockpos))
        {
            EnumFacing enumfacing = EnumFacing.UP;
            double d3 = Double.MAX_VALUE;

            if (!this.worldObj.isBlockFullCube(blockpos.west()) && d0 < d3)
            {
                d3 = d0;
                enumfacing = EnumFacing.WEST;
            }

            if (!this.worldObj.isBlockFullCube(blockpos.east()) && 1.0D - d0 < d3)
            {
                d3 = 1.0D - d0;
                enumfacing = EnumFacing.EAST;
            }


            if (!this.worldObj.isBlockFullCube(blockpos.north()) && d2 < d3)
            {
                d3 = d2;
                enumfacing = EnumFacing.NORTH;
            }

            if (!this.worldObj.isBlockFullCube(blockpos.south()) && 1.0D - d2 < d3)
            {
                d3 = 1.0D - d2;
                enumfacing = EnumFacing.SOUTH;
            }

            if (!this.worldObj.isBlockFullCube(blockpos.up()) && 1.0D - d1 < d3)
            {
                d3 = 1.0D - d1;
                enumfacing = EnumFacing.UP;
            }

            float f = this.rand.nextFloat() * 0.05F + 0.01F; // Botania - made multiplier and add both smaller
            float f1 = (float)enumfacing.getAxisDirection().getOffset();
            float secondary = (rand.nextFloat() - rand.nextFloat()) * 0.1F; // Botania - Make and use a secondary movement variable below

            if (enumfacing.getAxis() == EnumFacing.Axis.X)
            {
                this.motionX += (double)(f1 * f);
                this.motionY = this.motionZ = secondary;
            }
            else if (enumfacing.getAxis() == EnumFacing.Axis.Y)
            {
                this.motionY += (double)(f1 * f);
                this.motionX = this.motionZ = secondary;
            }
            else if (enumfacing.getAxis() == EnumFacing.Axis.Z)
            {
                this.motionZ += (double)(f1 * f);
                this.motionX = this.motionY = secondary;
            }

            return true;
        }

        return false;
    }

    public boolean corrupt = false;
    public boolean fake = false;
    private int multiplier = 2;
    private final boolean shrink = true;
    public final int particle = 16;
    public boolean tinkle = false;
    public final boolean slowdown = true;
    public int currentColor = 0;
}