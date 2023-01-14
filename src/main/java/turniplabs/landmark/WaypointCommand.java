package turniplabs.landmark;

import net.minecraft.client.Minecraft;
import net.minecraft.src.command.ClientCommand;
import net.minecraft.src.command.CommandHandler;
import net.minecraft.src.command.CommandSender;

import java.util.Objects;
import java.util.Random;

public class WaypointCommand extends ClientCommand {
    public WaypointCommand() {
        super(Minecraft.getMinecraft(), "waypoint");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
        if (args.length < 1) {
            return false;
        }

        if (Objects.equals(args[0], "add")) {
            if (args.length < 5) {
                return false;
            }

            String name = args[1];

            int color;

            double x = Double.parseDouble(args[2]);
            double y = Double.parseDouble(args[3]);
            double z = Double.parseDouble(args[4]);

            if (args.length < 6) {
                color = randomVibrantColor();
            } else {
                color = Integer.parseInt(args[5].replaceFirst("#", ""), 16);
            }

            new Waypoint(name, new double[]{x, y, z}, color, commandSender.getPlayer().worldObj.dimension.dimId);
            Waypoint.writeWaypoints();
            return true;
        }

        if (Objects.equals(args[0], "set")) {
            if (args.length > 3) {
                return false;
            }

            String name = args[1];
            Waypoint.waypoints.get(name).color = Integer.parseInt(args[2].replaceFirst("#", ""), 16);
            return true;
        }

        if (Objects.equals(args[0], "del")) {
            if (args.length > 2) {
                return false;
            }

            String name = args[1];
            Waypoint.waypoints.remove(name);
            return true;
        }

        if (Objects.equals(args[0], "list")) {
            if (Waypoint.waypoints.size() < 1) {
                commandSender.sendMessage("No waypoints exist!");
            } else {
                commandSender.sendMessage("Waypoints:");
            }

            for (Waypoint waypoint : Waypoint.waypoints.values()) {
                String dimensionName = "Unknown";
                switch (waypoint.dimId) {
                    case 0:
                        dimensionName = "Overworld";
                        break;
                    case 1:
                        dimensionName = "Nether";
                        break;
                    case 2:
                        dimensionName = "Paradise";
                        break;
                }
                commandSender.sendMessage("[" + waypoint.name + "] " + waypoint.position[0] + ", " + waypoint.position[1] + ", " + waypoint.position[2] + ", " + dimensionName);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean opRequired(String[] strings) {
        return false;
    }

    @Override
    public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
        commandSender.sendMessage("/waypoint list");
        commandSender.sendMessage("/waypoint add <name> <x> <y> <z> (color)");
        commandSender.sendMessage("/waypoint set <name> <color>");
        commandSender.sendMessage("/waypoint del <name>");
    }

    private int randomVibrantColor() {
        Random random = new Random();

        int r = (int) (random.nextFloat() * 255);
        int g = (int) (random.nextFloat() * 255);
        int b = (int) (random.nextFloat() * 255);

        return ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }
}
