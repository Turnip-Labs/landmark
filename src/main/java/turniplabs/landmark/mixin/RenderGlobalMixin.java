package turniplabs.landmark.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turniplabs.landmark.Waypoint;

import java.util.Objects;

@Mixin(value = RenderGlobal.class, remap = false)
public class RenderGlobalMixin {

    @Shadow
    private RenderEngine renderEngine;

    @Shadow
    private Minecraft mc;

    @Unique
    private double lastTickAngle = 0;

    @Unique
    private double tickAngle = 0;

    @Inject(method = "renderClouds", at = @At("HEAD"))
    private void deathlog_renderClouds(float renderPartialTicks, CallbackInfo ci) {
        for (Waypoint waypoint : Waypoint.waypoints.values()) {
            if (!Objects.equals(waypoint.dimId, this.mc.renderViewEntity.worldObj.dimension.dimId)) {
                continue;
            }
            renderWaypoint(renderPartialTicks, waypoint);
        }
    }

    private void renderWaypoint(float renderPartialTicks, Waypoint waypoint) {
        double pixel = 1.0 / 16.0;

        double eyeX = this.mc.renderViewEntity.lastTickPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.lastTickPosX) * (double) renderPartialTicks;
        double eyeY = this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double) renderPartialTicks;
        double eyeZ = this.mc.renderViewEntity.lastTickPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.lastTickPosZ) * (double) renderPartialTicks;

        double radians = Math.toRadians(lastTickAngle + (tickAngle - lastTickAngle) * (double) renderPartialTicks);

        for (int i = 0; i < ((MinecraftInterface) this.mc).getTimer().elapsedTicks; i++) {
            lastTickAngle = tickAngle;
            tickAngle += 1.0f;
        }

        float opacity = 0.8f;

        double distance = this.mc.renderViewEntity.getDistance(waypoint.position[0], waypoint.position[1], waypoint.position[2]);
        double horizontalDistance = this.mc.renderViewEntity.getDistance(waypoint.position[0], this.mc.renderViewEntity.posY, waypoint.position[2]);


        if (distance < 2.5) {
            double a = 0.4 * distance - 0.4;
            if (a < 0) {
                a = 0;
            }

            opacity = (float) a;
        } else if (horizontalDistance < 4) {
            double b = 0.1333 * horizontalDistance + 0.2666;
            if (b < 0.6) {
                b = 0.6;
            }

            opacity = (float) b;
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/environment/waypoint.png"));
        drawBeam(pixel * 10, pixel * 256, waypoint.position[0], waypoint.position[1], waypoint.position[2], eyeX, eyeY, eyeZ, radians, waypoint.color, opacity);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);

    }

    private void drawName(String name, double x, double y, double z) {
        FontRenderer fontrenderer = this.mc.fontRenderer;
        RenderManager renderManager = RenderManager.instance;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y + 2.3F, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-0.026666671F, -0.026666671F, 0.026666671F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.instance;
        byte offset = 0;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        int nameWidth = fontrenderer.getStringWidth(name) / 2;
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
        tessellator.addVertex(-nameWidth - 1, -1 + offset, 0.0D);
        tessellator.addVertex(-nameWidth - 1, 8 + offset, 0.0D);
        tessellator.addVertex(nameWidth + 1, 8 + offset, 0.0D);
        tessellator.addVertex(nameWidth + 1, -1 + offset, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, offset, 553648127);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, offset, 0xFFFFFF);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void drawBeam(double width, double height, double waypointX, double waypointY, double waypointZ, double eyeX, double eyeY, double eyeZ, double radians, int colorMultiplier, float opacity) {
        double center = width / 2.0;

        double modelX = 0 - center;
        double modelXwidth = width - center;
        double modelZ = 0 - center;
        double modelZwidth = width - center;

        double x1 = rotateX(modelX, modelZ, radians) + waypointX - eyeX;
        double x2 = rotateX(modelXwidth, modelZ, radians) + waypointX - eyeX;
        double x3 = rotateX(modelXwidth, modelZwidth, radians) + waypointX - eyeX;
        double x4 = rotateX(modelX, modelZwidth, radians) + waypointX - eyeX;

        double z1 = rotateZ(modelX, modelZ, radians) + waypointZ - eyeZ;
        double z2 = rotateZ(modelXwidth, modelZ, radians) + waypointZ - eyeZ;
        double z3 = rotateZ(modelXwidth, modelZwidth, radians) + waypointZ - eyeZ;
        double z4 = rotateZ(modelX, modelZwidth, radians) + waypointZ - eyeZ;

        float r = (float)(colorMultiplier >> 16 & 255) / 255.0F;
        float g = (float)(colorMultiplier >> 8 & 255) / 255.0F;
        float b = (float)(colorMultiplier & 255) / 255.0F;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(r, g, b, opacity);

        for (int chunkY = 0; chunkY < 16; chunkY++) {
            double y = chunkY * height - eyeY;
            double yh = y + height;

            tessellator.addVertexWithUV(x1, yh, z1, 1, 0);
            tessellator.addVertexWithUV(x2, yh, z2, 0, 0);
            tessellator.addVertexWithUV(x2, y, z2, 0, 1);
            tessellator.addVertexWithUV(x1, y, z1, 1, 1);

            tessellator.addVertexWithUV(x3, yh, z3, 1, 0);
            tessellator.addVertexWithUV(x4, yh, z4, 0, 0);
            tessellator.addVertexWithUV(x4, y, z4, 0, 1);
            tessellator.addVertexWithUV(x3, y, z3, 1, 1);

            tessellator.addVertexWithUV(x4, yh, z4, 1, 0);
            tessellator.addVertexWithUV(x1, yh, z1, 0, 0);
            tessellator.addVertexWithUV(x1, y, z1, 0, 1);
            tessellator.addVertexWithUV(x4, y, z4, 1, 1);

            tessellator.addVertexWithUV(x2, yh, z2, 1, 0);
            tessellator.addVertexWithUV(x3, yh, z3, 0, 0);
            tessellator.addVertexWithUV(x3, y, z3, 0, 1);
            tessellator.addVertexWithUV(x2, y, z2, 1, 1);

        }

        tessellator.draw();
    }

    private double rotateX(double x, double z, double radians) {
        return x * Math.cos(radians) - z * Math.sin(radians);
    }

    private double rotateZ(double x, double z, double radians) {
        return z * Math.cos(radians) + x * Math.sin(radians);
    }

}
