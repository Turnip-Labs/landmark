package turniplabs.landmark;

import net.minecraft.client.Minecraft;
import net.minecraft.src.WorldInfo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

public class Waypoint {
    public static HashMap<String, Waypoint> waypoints = new HashMap<>();
    public String name;
    public double[] position;
    public int color;
    public int dimId;

    public Waypoint(String name, double[] position, int color, int dimId) {
        this.name = name;
        this.position = position;
        this.color = color;
        this.dimId = dimId;
        waypoints.put(name, this);
    }

    public static String getUniqueWorldName() {
        WorldInfo worldInfo = Minecraft.getMinecraft().theWorld.getWorldInfo();
        return worldInfo.getRandomSeed() + worldInfo.getWorldName();
    }

    public static void writeWaypoints() {
        File waypointsDir = new File(Minecraft.getMinecraftDir(), "waypoints");
        if (waypointsDir.mkdir() || waypointsDir.exists()) {
            File waypointFile = new File(waypointsDir, getUniqueWorldName());
            try {
                if (waypointFile.createNewFile() || waypointFile.exists()) {
                    PrintWriter printWriter = new PrintWriter(waypointFile);
                    printWriter.print("");
                    for (Waypoint waypoint : waypoints.values()) {
                        printWriter.printf("%s, %f, %f, %f, %d, %d\n", waypoint.name, waypoint.position[0], waypoint.position[1], waypoint.position[2], waypoint.color, waypoint.dimId);
                    }
                    printWriter.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void readWaypoints() {
        File waypointsDir = new File(Minecraft.getMinecraftDir(), "waypoints");
        if (waypointsDir.exists()) {
            File waypointFile = new File(waypointsDir, getUniqueWorldName());
            try {
                if (waypointFile.exists()) {
                    Scanner scanner = new Scanner(waypointFile);
                    scanner.useDelimiter(", |\n");

                    while (scanner.hasNext()) {

                        String name = scanner.next();

                        double[] position = new double[3];
                        for (int i = 0; i < 3; i++) {
                            position[i] = scanner.nextDouble();
                        }

                        int color = scanner.nextInt();

                        int dimId = scanner.nextInt();

                        new Waypoint(name, position, color, dimId);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
