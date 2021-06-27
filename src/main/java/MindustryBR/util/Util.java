package MindustryBR.util;

import arc.struct.StringMap;
import arc.util.Strings;
import arc.util.io.CounterInputStream;
import mindustry.content.Blocks;
import mindustry.gen.Player;
import mindustry.io.MapIO;
import mindustry.io.SaveIO;
import mindustry.io.SaveVersion;
import mindustry.maps.Map;
import mindustry.world.Block;
import mindustry.world.CachedTile;
import mindustry.world.Tile;
import mindustry.world.WorldContext;
import org.json.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.zip.InflaterInputStream;

public class Util {
    /**
     *
     * @return A random color
     */
    public static Color randomColor() {
        Random rand = new Random();

        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();

        return new Color(r, g, b);
    }

    /**
     * Convert a millisecond duration to a string format
     *
     * @param millis A duration to convert to a string form
     * @return A string of the form "X Horas Y Minutos Z Segundos".
     */
    public static String msToDuration(long millis) {
        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return(hours + " Horas " + minutes + " Minutos " + seconds + " Segundos");
    }

    /**
     * Merge "source" into "target". If fields have equal name, merge them recursively.
     * taken from https://stackoverflow.com/a/15070484
     *
     * @return the merged object (target).
     */
    public static JSONObject mergeJson(JSONObject source, JSONObject target) throws JSONException {
        for (String key: JSONObject.getNames(source)) {
            Object value = source.get(key);
            if (!target.has(key)) {
                // new value for "key":
                target.put(key, value);
            } else {
                // existing value for "key" - recursively deep merge:
                if (value instanceof JSONObject) {
                    JSONObject valueJson = (JSONObject)value;
                    mergeJson(valueJson, target.getJSONObject(key));
                } else {
                    target.put(key, value);
                }
            }
        }
        return target;
    }

    /**
     * Handle player name
     *
     * @param name Player name
     * @param removeColor Whether or not to remove color tags
     * @return Handled name
     */
    public static String handleName(String name, Boolean removeColor) {
        if (name.toLowerCase().contains("admin") || name.toLowerCase().contains("adm")) {
            name = "retardado";
        } else if (name.toLowerCase().contains("dono")) {
            name = "retardado²";
        }

        if (removeColor) name = Strings.stripColors(name);

        return name;
    }

    /**
     * Handle player name
     *
     * @param player Player to handle
     * @param removeColor Whether or not to remove color tags
     * @return Handled name
     */
    public static String handleName(Player player, Boolean removeColor) {
        String name = player.name;

        if (!player.admin) {
            if (player.name.toLowerCase().contains("admin") || player.name.toLowerCase().contains("adm")) {
                player.name = "retardado";
            } else if (player.name.toLowerCase().contains("dono")) {
                player.name = "retardado²";
            }
        }

        if (removeColor) name = Strings.stripColors(name);

        return name;
    }
}
