package turniplabs.landmark.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Minecraft.class, remap = false)
public interface MinecraftInterface {

    @Accessor("timer")
    Timer getTimer();
}
