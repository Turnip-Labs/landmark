package turniplabs.landmark.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turniplabs.landmark.Waypoint;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {

    @Shadow
    public World theWorld;

    @Inject(method = "changeWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/src/World;isNewWorld:Z"))
    private void deathlog_changeWorld(World world, String s, EntityPlayer entityplayer, CallbackInfo ci) {
        if (theWorld != null) {
            Waypoint.waypoints.clear();
            Waypoint.readWaypoints();
        }
    }
}
