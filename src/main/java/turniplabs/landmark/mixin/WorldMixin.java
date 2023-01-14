package turniplabs.landmark.mixin;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turniplabs.landmark.Waypoint;

@Mixin(value = World.class, remap = false)
public class WorldMixin {

    @Shadow
    protected IChunkProvider chunkProvider;

    @Inject(method = "saveWorld", at = @At("TAIL"))
    private void deathlog_saveWorld(boolean flag, IProgressUpdate iprogressupdate, CallbackInfo ci) {
        if(this.chunkProvider.canSave()) {
            Waypoint.writeWaypoints();
        }
    }

}
