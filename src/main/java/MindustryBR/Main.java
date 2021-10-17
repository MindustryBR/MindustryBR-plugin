package MindustryBR;

import MindustryBR.Discord.Bot;
import MindustryBR.Mindustry.Commands.client.dm;
import MindustryBR.Mindustry.Commands.client.history;
import MindustryBR.Mindustry.Commands.server.historyLog;
import MindustryBR.Mindustry.Commands.server.say;
import MindustryBR.Mindustry.Events.*;
import MindustryBR.Mindustry.Filters.ReactorFilter;
import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import arc.Core;
import arc.Events;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.game.EventType.*;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import java.io.IOException;

import static mindustry.Vars.netServer;

public class Main extends Plugin {
    public static JSONObject config = new JSONObject();
    public static JSONObject contentBundle = new JSONObject();
    public static JSONObject linkDB = new JSONObject();
    public static DiscordApi bot;
    public static LimitedQueue<BaseEntry>[][] worldHistory;
    public static Seq<Player> activeHistoryPlayers = new Seq<>();
    public static ObjectMap<String, LimitedQueue<BaseEntry>> playerHistory = new ObjectMap<>();
    public static StringMap knownIPs = new StringMap();
    public static boolean logHistory = false;

    public Main() throws IOException {
        // Misc events
        Events.on(ConfigEvent.class, e -> configEvent.run(bot, config, e));
        Events.on(TapEvent.class, e -> tap.run(bot, config, e));

        // Game events
        Events.on(GameOverEvent.class, e -> gameover.run(bot, config, e));
        Events.on(WaveEvent.class, e -> wave.run(bot, config, e));
        Events.on(WorldLoadEvent.class, e -> worldLoad.run(bot, config, e));

        // Build events
        Events.on(BlockBuildBeginEvent.class, e -> blockBuildBegin.run(bot, config, e));
        Events.on(BlockBuildEndEvent.class, e -> blockBuildEnd.run(bot, config, e));
        Events.on(BlockDestroyEvent.class, e -> blockDestroy.run(bot, config, e));

        // Units events
        Events.on(UnitCreateEvent.class, e -> unitCreate.run(bot, config, e));
        Events.on(UnitDestroyEvent.class, e -> unitDestroy.run(bot, config, e));
        Events.on(UnitDrownEvent.class, e -> unitDrown.run(bot, config, e));

        // Players event
        Events.on(PlayerJoin.class, e -> playerJoin.run(bot, config, e));
        Events.on(PlayerLeave.class, e -> playerLeave.run(bot, config, e));
        Events.on(PlayerChatEvent.class, e -> {
            try {
                playerChat.run(bot, config, e);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        Events.on(PlayerBanEvent.class, e -> {
            try {
                playerBan.run(bot, config, e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        Events.on(PlayerUnbanEvent.class, e -> {
            try {
                playerUnban.run(bot, config, e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }

    // Called when the server initializes
    @Override
    public void init() {
        createConfig();
        createContentBundle();
        createLinkDB();

        // Start the discord bot if token was provided
        if (!config.isEmpty() && !config.getJSONObject("discord").isEmpty() && !config.getJSONObject("discord").getString("token").isBlank()) {
            bot = Bot.run();
        }

        netServer.admins.addActionFilter(ReactorFilter::exec);
    }

    // Register commands that run on the server
    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("reloadconfig", "[MindustryBR] Reload plugin config", args -> {
            createConfig();
            createContentBundle();
            createLinkDB();
        });

        handler.register("saydc", "<message...>", "[MindustryBR] Send message as Server", args -> say.run(bot, config, args));

        handler.register("history", "[MindustryBR] Toggle history log in console", args -> historyLog.run(bot, config, args));

        handler.register("startbot", "[force]", "[MindustryBR] Start the discord bot if it isn't already online", args -> {
            // Start the discord bot if token was provided and the bot isn't online
            if (((!config.isEmpty() && !config.getJSONObject("discord").getString("token").isBlank()) || args.length > 0) && !Bot.logged) {
                if (args.length > 0) Log.info("force starting bot");
                bot = Bot.run();
            }
        });
    }

    // Register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("dm", "<player> <message...>", "Mande uma mensagem privada para um jogador.", (args, player) -> dm.run(bot, config, args, player));

        handler.<Player>register("history", "Ative o historico do bloco", (args, player) -> history.run(bot, config, args, player));
    }

    private void createContentBundle() {
        // Load config file if it already exists
        if (Core.settings.getDataDirectory().child("mods/MindustryBR/contentBundle.json").exists()) {
            loadContentBundle();
            return;
        }

        JSONObject defaultContentBundle = new JSONObject("{\n\"blast-compound\": \"[#FC7C5C]composto de explosao\",\n\"coal\": \"[#666666]carvao\",\n\"copper\": \"[#ECC4AC]cobre\",\n \"graphite\": \"[#94ACDC]grafite\",\n\"lead\": \"[#AC9CD4]chumbo\",\n\"metaglass\": \"[#ECECF4]metavidro\",\n\"phase-fabric\": \"[#FCD49C]tecido de fase\",\n\"plastanium\": \"[#CCDC7C]plastanio\",\n\"pyratite\": \"[#FCAC5C]piratita\",\n\"sand\": \"[#F4CCA4]areia\",\n\"scrap\": \"[#E4B48C]sucata\",\n\"silicon\": \"[#8C9494]silicio\",\n\"spore-pod\": \"[#9C7CDC]capsula de esporos\",\n\"surge-alloy\": \"[#F4EC7C]liga de surto\",\n\"thorium\": \"[#FCA4C4]torio\",\n\"titanium\": \"[#A4BCFC]titanio\",\n\"diversetech-hyper-alloy\": \"[#8C8C8C]hiper liga\",\n\"diversetech-nickel\": \"[#ABBCDE]niquel\",\n\"diversetech-nitinol\": \"[#8399E6]nitinol\",\n\"diversetech-steel-compound\": \"[#D4AC8C]composto de aco\",\n\"cryofluid\": \"[#84CCEC]fluido criogenico\",\n\"oil\": \"[#64645C]petroleo\",\n\"slag\": \"[#E48454]escoria\",\n\"water\": \"[#343C9C]agua\",\n\"dagger\": \"[orange]Dagger\",\n\"mace\": \"[orange]Mace\",\n\"fortress\": \"[orange]Fortress\",\n\"scepter\": \"[orange]Scepter\",\n\"reign\": \"[orange]Reign\",\n\"nova\": \"[green]Nova\",\n\"pulsar\": \"[green]Pulsar\",\n\"quasar\": \"[green]Quasar\",\n\"vela\": \"[green]Vela\",\n\"corvus\": \"[green]Corvus\",\n\"crawler\": \"[purple]Rastejador\",\n\"spiroct\": \"[purple]Spiroct\",\n\"arkyid\": \"[purple]Arkyid\",\n\"toxopid\": \"[purple]Toxopid\",\n\"flare\": \"[orange]Flare\",\n\"horizon\": \"[orange]Horizon\",\n\"zenith\": \"[orange]Zenith\",\n\"antumbra\": \"[orange]Antumbra\",\n\"eclipse\": \"[orange]Eclipse\",\n\"mono\": \"[green]Mono\",\n\"poly\": \"[green]Poly\",\n\"mega\": \"[green]Mega\",\n\"quad\": \"[green]Quad\",\n\"oct\": \"[green]Oct\",\n\"risso\": \"[orange]Risso\",\n\"minke\": \"[orange]Minke\",\n\"bryde\": \"[orange]Bryde\",\n\"sei\": \"[orange]Sei\",\n\"omura\": \"[orange]Omura\",\n\"alpha\": \"[orange]Alpha\",\n\"beta\": \"[orange]Beta\",\n\"gamma\": \"[orange]Gamma\",\n\"spawn\": \"Area inimiga\",\n\"cliff\": \"Relevo\",\n\"deep-water\": \"Agua profunda\",\n\"shallow-water\": \"Agua\",\n\"tainted-water\": \"Agua tinta\",\n\"darksand-tainted-water\": \"Agua tinta sobre areia escura\",\n\"sand-water\": \"Agua sobre areia\",\n\"darksand-water\": \"Agua sobre areia escura\",\n\"tar\": \"Piche\",\n\"molten-slag\": \"Escoria\",\n\"space\": \"Space\",\n\"stone\": \"Pedra\",\n\"crater-stone\": \"Crateras\",\n\"char\": \"Cinzas\",\n\"basalt\": \"Basalto\",\n\"hotrock\": \"Rocha quente\",\n\"magmarock\": \"Rocha de magma\",\n\"darksand\": \"Areia escura\",\n\"dirt\": \"Terra\",\n\"mud\": \"Lama\",\n\"dacite\": \"Dacito\",\n\"grass\": \"Grama\",\n\"salt\": \"Sal\",\n\"snow\": \"Neve\",\n\"ice\": \"Gelo\",\n\"ice-snow\": \"Gelo com Neve\",\n\"shale\": \"Folhelho\",\n\"stone-wall\": \"Parede de Pedra\",\n\"spore-wall\": \"Muro de Esporos\",\n\"dirt-wall\": \"Parede de Terra\",\n\"dacite-wall\": \"Parede de Dacito\",\n\"ice-wall\": \"Parede de Gelo\",\n\"snow-wall\": \"Parede de Neve\",\n\"dune-wall\": \"Duna\",\n\"sand-wall\": \"Muro de Areia\",\n\"salt-wall\": \"Parede de sal\",\n\"shrubs\": \"Arbusto\",\n\"shale-wall\": \"Parede de Folhelho\",\n\"spore-pine\": \"Pinheiro de esporo\",\n\"snow-pine\": \"Pinheiro com neve\",\n\"pine\": \"Pinheiro\",\n\"white-tree-dead\": \"Arvore branca morta\",\n\"white-tree\": \"Arvore branca\",\n\"spore-cluster\": \"Aglomerado de esporos\",\n\"boulder\": \"Rochedo\",\n\"snow-boulder\": \"Monte de neve\",\n\"shale-boulder\": \"Pedra de Folhelho\",\n\"sand-boulder\": \"Pedregulho de areia\",\n\"dacite-boulder\": \"Dacite Boulder\",\n\"moss\": \"Musgo\",\n\"spore-moss\": \"Musgo de Esporos\",\n\"metal-floor\": \"Chao de metal\",\n\"metal-floor-damaged\": \"Chao de metal danificado\",\n\"metal-floor-2\": \"Chao de metal 2\",\n\"metal-floor-3\": \"Chao de metal 3\",\n\"metal-floor-5\": \"Chao de metal 5\",\n\"dark-panel-1\": \"Painel escuro 1\",\n\"dark-panel-2\": \"Painel escuro 2\",\n\"dark-panel-3\": \"Painel escuro 3\",\n\"dark-panel-4\": \"Painel escuro 4\",\n\"dark-panel-5\": \"Painel escuro 5\",\n\"dark-panel-6\": \"Painel escuro 6\",\n\"dark-metal\": \"Metal escuro\",\n\"pebbles\": \"Pedrinhas\",\n\"tendrils\": \"Gavinhas\",\n\"graphite-press\": \"Prensa de grafite\",\n\"multi-press\": \"Multi-Prensa\",\n\"silicon-smelter\": \"Fundidora de silicio\",\n\"silicon-crucible\": \"Fornalha De Silicio\",\n\"kiln\": \"Forno\",\n\"plastanium-compressor\": \"Compressor de Plastanio\",\n\"phase-weaver\": \"Palheta de fase\",\n\"alloy-smelter\": \"Fundidora de liga\",\n\"cryofluid-mixer\": \"Misturador de Crio-Fluido\",\n\"pyratite-mixer\": \"Misturador de Piratita\",\n\"blast-mixer\": \"Misturador de Explosao\",\n\"melter\": \"Aparelho de fusao\",\n\"separator\": \"Separador\",\n\"disassembler\": \"Desmontador\",\n\"spore-press\": \"Prensa de Esporo\",\n\"pulverizer\": \"Pulverizador\",\n\"coal-centrifuge\": \"Centrifugador de Carvao\",\n\"incinerator\": \"Incinerador\",\n\"copper-wall\": \"Muro de Cobre\",\n\"copper-wall-large\": \"Muralha de Cobre\",\n\"titanium-wall\": \"Muro de Titanio\",\n\"titanium-wall-large\": \"Muralha de Titanio\",\n\"plastanium-wall\": \"Muro de Plastanio\",\n\"plastanium-wall-large\": \"Muralha de Plastanio\",\n\"thorium-wall\": \"Muro de Torio\",\n\"thorium-wall-large\": \"Muralha de Torio\",\n\"phase-wall\": \"Muro de Fase\",\n\"phase-wall-large\": \"Muralha de Fase\",\n\"surge-wall\": \"Muro de liga de surto\",\n\"surge-wall-large\": \"Muralha de liga de surto\",\n\"door\": \"Porta\",\n\"door-large\": \"Porta grande\",\n\"scrap-wall\": \"Muro de sucata\",\n\"scrap-wall-large\": \"Muro grande de sucata\",\n\"scrap-wall-huge\": \"Muro enorme de sucata\",\n\"scrap-wall-gigantic\": \"Muro gigante de sucata\",\n\"thruster\": \"Propulsor\",\n\"mender\": \"Reparador\",\n\"mend-projector\": \"Projetor de reparo\",\n\"overdrive-projector\": \"Projetor de sobrecarga\",\n\"overdrive-dome\": \"Domo de Sobrecarga\",\n\"force-projector\": \"Projetor de campo de forca\",\n\"shock-mine\": \"Mina de choque\",\n\"conveyor\": \"Esteira\",\n\"titanium-conveyor\": \"Esteira de titanio\",\n\"plastanium-conveyor\": \"Esteira de plastanio\",\n\"armored-conveyor\": \"Esteira blindada\",\n\"junction\": \"Juncao\",\n\"bridge-conveyor\": \"Esteira-Ponte\",\n\"phase-conveyor\": \"Esteira de Fase\",\n\"sorter\": \"Ordenador\",\n\"inverted-sorter\": \"Ordenador invertido\",\n\"router\": \"Roteador\",\n\"distributor\": \"Distribuidor\",\n\"overflow-gate\": \"Portao de Sobrecarga\",\n\"underflow-gate\": \"Portao de Sobrecarga Invertido\",\n\"mass-driver\": \"Catapulta eletromagnetica\",\n\"duct\": \"Duto\",\n\"duct-router\": \"Duto Roteador\",\n\"duct-bridge\": \"Duto-Ponte\",\n\"mechanical-pump\": \"Bomba Mecanica\",\n\"rotary-pump\": \"Bomba rotatoria\",\n\"thermal-pump\": \"Bomba termica\",\n\"conduit\": \"Cano\",\n\"pulse-conduit\": \"Cano de Pulso\",\n\"plated-conduit\": \"Cano Blindado\",\n\"liquid-router\": \"Roteador de Liquido\",\n\"liquid-tank\": \"Tanque de Liquido\",\n\"liquid-junction\": \"Juncao de Liquido\",\n\"bridge-conduit\": \"Cano Ponte\",\n\"phase-conduit\": \"Cano de Fase\",\n\"power-node\": \"Celula de energia\",\n\"power-node-large\": \"Celula de energia grande\",\n\"surge-tower\": \"Torre de surto\",\n\"diode\": \"Diodo\",\n\"battery\": \"Bateria\",\n\"battery-large\": \"Bateria grande\",\n\"combustion-generator\": \"Gerador a combustao\",\n\"thermal-generator\": \"Gerador termico\",\n\"steam-generator\": \"Gerador a vapor\",\n\"differential-generator\": \"Gerador diferencial\",\n\"rtg-generator\": \"Gerador GTR\",\n\"solar-panel\": \"Painel Solar\",\n\"solar-panel-large\": \"Painel Solar Grande\",\n\"thorium-reactor\": \"Reator nuclear\",\n\"impact-reactor\": \"Reator De impacto\",\n\"mechanical-drill\": \"Broca Mecanica\",\n\"pneumatic-drill\": \"Broca Pneumatica\",\n\"laser-drill\": \"Broca a Laser\",\n\"blast-drill\": \"Broca de impacto\",\n\"water-extractor\": \"Extrator de agua\",\n\"cultivator\": \"Cultivador\",\n\"oil-extractor\": \"Extrator de Petroleo\",\n\"core-shard\": \"Fragmento do nucleo\",\n\"core-foundation\": \"Fundacao do nucleo\",\n\"core-nucleus\": \"Centro do nucleo\",\n\"vault\": \"Cofre\",\n\"container\": \"Conteiner\",\n\"unloader\": \"Descarregador\",\n\"duo\": \"Duo\",\n\"scatter\": \"Dispersao\",\n\"scorch\": \"Lanca-chamas\",\n\"hail\": \"Artilharia\",\n\"wave\": \"Onda\",\n\"lancer\": \"Lanceiro\",\n\"arc\": \"Tesla\",\n\"parallax\": \"Paralaxe\",\n\"swarmer\": \"Enxame\",\n\"salvo\": \"Salvo\",\n\"segment\": \"Segmento\",\n\"tsunami\": \"Tsunami\",\n\"fuse\": \"Fusivel\",\n\"ripple\": \"Ondulacao\",\n\"cyclone\": \"Ciclone\",\n\"foreshadow\": \"Foreshadow\",\n\"spectre\": \"Espectro\",\n\"meltdown\": \"Fusao\",\n\"command-center\": \"Centro de Comando\",\n\"ground-factory\": \"Fabrica de unidades terrestres\",\n\"air-factory\": \"Fabrica de unidades aereas\",\n\"naval-factory\": \"Fabrica de unidades navais\",\n\"additive-reconstructor\": \"Reconstrutor Aditivo\",\n\"multiplicative-reconstructor\": \"Reconstrutor Multiplicativo\",\n\"exponential-reconstructor\": \"Reconstrutor Exponencial\",\n\"tetrative-reconstructor\": \"Reconstrutor Tetrativo\",\n\"repair-point\": \"Ponto de Reparo\",\n\"repair-turret\": \"Torre de Reparo\",\n\"payload-conveyor\": \"Esteira de Carga\",\n\"payload-router\": \"Roteador de Carga\",\n\"payload-propulsion-tower\": \"Torre de Propulsao De Cargas\",\n\"power-source\": \"Fonte de energia\",\n\"power-void\": \"Anulador de energia\",\n\"item-source\": \"Criador de itens\",\n\"item-void\": \"Destruidor de itens\",\n\"liquid-source\": \"Criador de liquidos\",\n\"liquid-void\": \"Destruidor de liquidos\",\n\"payload-source\": \"Criador de Cargas\",\n\"payload-void\": \"Destruidor de Cargas\",\n\"illuminator\": \"Iluminador\",\n\"launch-pad\": \"Plataforma de lancamento\",\n\"message\": \"Mensagem\",\n\"switch\": \"Alavanca\",\n\"micro-processor\": \"Micro Processador\",\n\"logic-processor\": \"Processador Logico\",\n\"hyper-processor\": \"Hiper Processador\",\n\"memory-cell\": \"Celula de Memoria\",\n\"memory-bank\": \"Banco de Memoria\",\n\"logic-display\": \"Monitor Logico\",\n\"large-logic-display\": \"Monitor logico grande\"\n}");

        Core.settings.getDataDirectory().child("mods/MindustryBR/contentBundle.json").writeString(defaultContentBundle.toString(4));
        contentBundle = defaultContentBundle;
    }

    private void loadContentBundle() {
        contentBundle = new JSONObject(Core.settings.getDataDirectory().child("mods/MindustryBR/contentBundle.json").readString());
        Log.info("[MindustryBR] Content bundle loaded");
    }

    private void createConfig() {
        // Load config file if it already exists
        if (Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").exists()) {
            loadConfig();
            return;
        }

        // Make default config
        JSONObject defaultConfig = new JSONObject();
        JSONObject defaultPrefix = new JSONObject();
        JSONObject defaultDiscordConfig = new JSONObject();

        defaultConfig.put("owner_id", "");
        defaultConfig.put("version", 1);
        defaultConfig.put("ip", "");

        defaultPrefix.put("owner_prefix", "[sky][Dono][] %1");
        defaultPrefix.put("admin_prefix", "[blue][Admin][] %1");
        defaultPrefix.put("user_prefix", "%1");

        String[] discordStrings = {
                "token",
                "channel_id",
                "log_channel_id",
                "mod_channel_id",
                "serverstatus_channel_id",
                "admin_role_id",
                "owner_role_id",
                "mod_role_id",
                "emoji-bank"
        };

        for (String ds : discordStrings) {
            defaultDiscordConfig.put(ds, "");
        }

        defaultDiscordConfig.put("prefix", "!");

        defaultConfig.put("discord", defaultDiscordConfig);
        defaultConfig.put("prefix", defaultPrefix);
        defaultConfig.put("name", "Survival");

        // Create config file
        Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").writeString(defaultConfig.toString(4));
        linkDB = defaultConfig;
    }

    private void loadConfig() {
        config = new JSONObject(this.getConfig().readString());
        Log.info("[MindustryBR] Config loaded");
    }

    private void createLinkDB() {
        // Load config file if it already exists
        if (Core.settings.getDataDirectory().child("mods/MindustryBR/linkDB.json").exists()) {
            loadLinkDB();
            return;
        }

        JSONObject defaultConfig = new JSONObject();

        // Create config file
        Core.settings.getDataDirectory().child("mods/MindustryBR/linkDB.json").writeString(defaultConfig.toString(4));
        config = defaultConfig;
    }

    private void loadLinkDB() {
        linkDB = new JSONObject(Core.settings.getDataDirectory().child("mods/MindustryBR/linkDB.json").readString());
        Log.info("[MindustryBR] Linked accounts DB loaded");
    }
}
