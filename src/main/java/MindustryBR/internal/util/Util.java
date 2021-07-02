package MindustryBR.internal.util;

import arc.util.Strings;
import mindustry.content.Items;
import mindustry.gen.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
        if(millis < 0) throw new IllegalArgumentException("Duration must be greater than zero!");

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
            // existing value for "key" - recursively deep merge:
            // new value for "key":
            if (!target.has(key)) target.put(key, value);
            else if (value instanceof JSONObject) {
                JSONObject valueJson = (JSONObject) value;
                mergeJson(valueJson, target.getJSONObject(key));
            } else target.put(key, value);
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
        if (name.toLowerCase().contains("admin") || name.toLowerCase().contains("adm")) name = "retardado";
        else if (name.toLowerCase().contains("dono")) name = "retardado²";

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

        if (!player.admin) if (player.name.toLowerCase().contains("admin") || player.name.toLowerCase().contains("adm"))
            player.name = "retardado";
        else if (player.name.toLowerCase().contains("dono")) player.name = "retardado²";

        if (removeColor) name = Strings.stripColors(name);

        return name;
    }

    public static String[] resourcesRawName = {
            "blast_compound",
            "coal",
            "copper",
            "graphite",
            "lead",
            "metaglass",
            "phase_fabric",
            "plastanium",
            "pyratite",
            "sand",
            "scrap",
            "silicon",
            "spore_pod",
            "surge_alloy",
            "thorium",
            "titanium"
    };

    public static String[] resourcesName = {
            "Composto de explosao", // Blast compound
            "Carvao",               // Coal
            "Cobre",                // Copper
            "Grafite",              // Graphite
            "Chumbo",               // Lead
            "Metavidro",            // Metaglass
            "Tecido de fase",       // Phase fabric
            "Plastanio",            // Plastanium
            "Piratita",             // Pyratite
            "Areia",                // Sand
            "Sucata",               // Scrap
            "Silicio",              // Silicon
            "Capsula de esporos",   // Spore pod
            "Liga de surto",        // Surge alloy
            "Torio",                // Thorium
            "Titanio"               // Titanium
    };

    public static short[] resourcesID = {
            Items.blastCompound.id,
            Items.coal.id,
            Items.copper.id,
            Items.graphite.id,
            Items.lead.id,
            Items.metaglass.id,
            Items.phaseFabric.id,
            Items.plastanium.id,
            Items.pyratite.id,
            Items.sand.id,
            Items.scrap.id,
            Items.silicon.id,
            Items.sporePod.id,
            Items.surgeAlloy.id,
            Items.thorium.id,
            Items.titanium.id
    };

    /**
     * Get the discord emoji tag corresponding to the given resource
     * @param resourceID Resource in-game ID
     * @param config Plugin config
     * @return Discord emoji tag
     */
    public static String getResourceEmoji(short resourceID, JSONObject config) {
        JSONObject emojis = config.getJSONObject("discord").getJSONObject("emojis");

        if (resourceID == resourcesID[0]) return emojis.getString(resourcesRawName[0]).isBlank() ? "" : emojis.getString(resourcesRawName[0]);
        else if (resourceID == resourcesID[1]) return emojis.getString(resourcesRawName[1]).isBlank() ? "" : emojis.getString(resourcesRawName[1]);
        else if (resourceID == resourcesID[2]) return emojis.getString(resourcesRawName[2]).isBlank() ? "" : emojis.getString(resourcesRawName[2]);
        else if (resourceID == resourcesID[3]) return emojis.getString(resourcesRawName[3]).isBlank() ? "" : emojis.getString(resourcesRawName[3]);
        else if (resourceID == resourcesID[4]) return emojis.getString(resourcesRawName[4]).isBlank() ? "" : emojis.getString(resourcesRawName[4]);
        else if (resourceID == resourcesID[5]) return emojis.getString(resourcesRawName[5]).isBlank() ? "" : emojis.getString(resourcesRawName[5]);
        else if (resourceID == resourcesID[6]) return emojis.getString(resourcesRawName[6]).isBlank() ? "" : emojis.getString(resourcesRawName[6]);
        else if (resourceID == resourcesID[7]) return emojis.getString(resourcesRawName[7]).isBlank() ? "" : emojis.getString(resourcesRawName[7]);
        else if (resourceID == resourcesID[8]) return emojis.getString(resourcesRawName[8]).isBlank() ? "" : emojis.getString(resourcesRawName[8]);
        else if (resourceID == resourcesID[9]) return emojis.getString(resourcesRawName[9]).isBlank() ? "" : emojis.getString(resourcesRawName[9]);
        else if (resourceID == resourcesID[10]) return emojis.getString(resourcesRawName[10]).isBlank() ? "" : emojis.getString(resourcesRawName[10]);
        else if (resourceID == resourcesID[11]) return emojis.getString(resourcesRawName[11]).isBlank() ? "" : emojis.getString(resourcesRawName[11]);
        else if (resourceID == resourcesID[12]) return emojis.getString(resourcesRawName[12]).isBlank() ? "" : emojis.getString(resourcesRawName[12]);
        else if (resourceID == resourcesID[13]) return emojis.getString(resourcesRawName[13]).isBlank() ? "" : emojis.getString(resourcesRawName[13]);
        else if (resourceID == resourcesID[14]) return emojis.getString(resourcesRawName[14]).isBlank() ? "" : emojis.getString(resourcesRawName[14]);
        else return "";
    }
}
