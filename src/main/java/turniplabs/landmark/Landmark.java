package turniplabs.landmark;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.CommandHelper;


public class Landmark implements ModInitializer {
    public static final String MOD_ID = "landmark";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static String name(String name) {
        return Landmark.MOD_ID + "." + name;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Landmark initialized.");

        CommandHelper.createCommand(new WaypointCommand());
    }
}
