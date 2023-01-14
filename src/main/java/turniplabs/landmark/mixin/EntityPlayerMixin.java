package turniplabs.landmark.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.helper.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turniplabs.landmark.Waypoint;

@Mixin(value = EntityPlayer.class, remap = false)
public abstract class EntityPlayerMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void deathlog_onDeath(Entity entity, CallbackInfo ci) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        double x = Utils.floor10(player.posX);
        double y = Utils.floor10(player.posY);
        double z = Utils.floor10(player.posZ);
        Minecraft.getMinecraft().ingameGUI.addChatMessage("§4" + player.username + " has died at " + x + " " + y + " " + z + " !!");
        new Waypoint("death", new double[]{x, y, z}, 0xfb3d54, player.worldObj.dimension.dimId);
        Waypoint.writeWaypoints();
    }

}
