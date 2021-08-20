package MindustryBR.internal.util;

import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.core.GameState;
import mindustry.gen.Player;
import mindustry.io.SaveIO;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static mindustry.Vars.*;

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
     * @param shortTime if true, return only minutes and seconds
     * @return A string of the form "X Hours Y Minutes Z Seconds".
     */
    public static String msToDuration(long millis, boolean shortTime) {
        if(millis < 0) throw new IllegalArgumentException("Duration must be greater than zero!");

        if (!shortTime) {
            long hours = TimeUnit.MILLISECONDS.toHours(millis);
            millis -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            millis -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

            return(hours + " Horas " + minutes + " Minutos " + seconds + " Segundos");
        } else {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            millis -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

            return(minutes + " Minutos " + seconds + " Segundos");
        }
    }

    /**
     * Convert a millisecond duration to a string format
     *
     * @param millis A duration to convert to a string form
     * @param shortTime if true, return only minutes and seconds
     * @return A string of the form "X Hours Y Minutes Z Seconds".
     */
    public static String msToDuration(float millis, boolean shortTime) {
        if (millis < 0) throw new IllegalArgumentException("Duration must be greater than zero!");

        if (!shortTime) {
            long hours = TimeUnit.MILLISECONDS.toHours((long) millis);
            millis -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes((long) millis);
            millis -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds((long) millis);

            return(hours + " Horas " + minutes + " Minutos " + seconds + " Segundos");
        } else {
            long minutes = TimeUnit.MILLISECONDS.toMinutes((long) millis);
            millis -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds((long) millis);

            return(minutes + " Minutos " + seconds + " Segundos");
        }
    }

    /**
     * Merge "source" into "target". If fields have equal name, merge them recursively.
     * adapted from https://stackoverflow.com/a/15070484
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
     * Handle discord markdown
     *
     * @param str String to handle
     * @return Handled string
     */
    public static String handleDiscordMD(String str) {
        return str.replaceAll("_", "\\_").replaceAll("\\*", "\\*").replaceAll("~", "\\~");
    }

    /**
     * Handle discord markdown
     *
     * @param str String to handle
     * @param remove if true, remove all markdown notations. If false, escape it
     * @return Handled string
     */
    public static String handleDiscordMD(String str, boolean remove) {
        if (remove) {
            str = str.replaceAll("_", "").replaceAll("\\*", "").replaceAll("~", "");
        } else {
            str = str.replaceAll("_", "\\_").replaceAll("\\*", "\\*").replaceAll("~", "\\~");
        }

        return str;
    }

    /**
     * Handle player name
     *
     * @param name Player name
     * @param removeColor Whether or not to remove color tags
     * @return Handled name
     */
    public static String handleName(String name, boolean removeColor) {
        if (name.toLowerCase().contains("admin") || name.toLowerCase().contains("adm")) name = "retardado";
        else if (name.toLowerCase().contains("dono")) name = "retardado²";

        if (removeColor) name = Strings.stripColors(name);

        return name;
    }

    /**
     * Handle player name
     *
     * @param name Player name
     * @param removeColor Whether or not to remove color tags
     * @param discord Whether or not to handle discord markdown
     * @return Handled name
     */
    public static String handleName(String name, boolean removeColor, boolean discord) {
        name = handleName(name, removeColor);

        if (discord) name = handleDiscordMD(name);

        return name;
    }

    /**
     * Handle player name
     *
     * @param player Player to handle
     * @param removeColor Whether or not to remove color tags
     * @return Handled name
     */
    public static String handleName(Player player, boolean removeColor) {
        String name = player.name;

        if (!player.admin) if (player.name.toLowerCase().contains("admin") || player.name.toLowerCase().contains("adm"))
            player.name = "retardado";
        else if (player.name.toLowerCase().contains("dono")) player.name = "retardado²";

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
    public static String handleName(Player player, boolean removeColor, boolean discord) {
        String name = handleName(player, removeColor);

        if (discord) name = handleDiscordMD(name);

        return name;
    }

    /**
     * Save game if the server is open and its not paused
     */
    public static void saveGame(){
        if(!Vars.state.is(GameState.State.playing) || Vars.state.serverPaused) return;

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("_yyyy-MM-dd-hh-mm");
        String fname = "rollback_";
        Fi file = saveDirectory.child(fname + Strings.stripColors(state.map.name().replaceAll(" ", "-")) + "_" + state.wave + ft.format(dNow) + ".msav");
        Fi latest = saveDirectory.child("latest.msav");
        Fi[] files = saveDirectory.list((dir, name) -> name.startsWith(fname));
        Arrays.sort(files, (f1, f2) -> {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0)
                return -1;
            else if (diff == 0)
                return 0;
            else
                return 1;
        });
        if(files.length >= 10){
            for(int index = 9; index < files.length; index++){
                files[index].delete();
            }
        }
        Core.app.post(() -> {
            SaveIO.save(file);
            SaveIO.save(latest);
            Log.info("Saved to @", file);
        });
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
            Items.blastCompound.id,     // 0
            Items.coal.id,              // 1
            Items.copper.id,            // 2
            Items.graphite.id,          // 3
            Items.lead.id,              // 4
            Items.metaglass.id,         // 5
            Items.phaseFabric.id,       // 6
            Items.plastanium.id,        // 7
            Items.pyratite.id,          // 8
            Items.sand.id,              // 9
            Items.scrap.id,             // 10
            Items.silicon.id,           // 11
            Items.sporePod.id,          // 12
            Items.surgeAlloy.id,        // 13
            Items.thorium.id,           // 14
            Items.titanium.id           // 15
    };

    /**
     * Get the discord emoji tag corresponding to the given resource
     * @param resourceID Resource in-game ID
     * @param config Plugin config
     * @return Discord emoji ID
     */
    public static String getResourceEmojiID(short resourceID, JSONObject config) {
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
        else if (resourceID == resourcesID[15]) return emojis.getString(resourcesRawName[15]).isBlank() ? "" : emojis.getString(resourcesRawName[15]);
        else return "";
    }
}
