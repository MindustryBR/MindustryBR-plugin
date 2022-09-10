package MindustryBR.internal.Menus;

import mindustry.gen.Player;

public interface BaseMenu {
    static void exec(Player player, int option) {};
    static void menu(Player player, String[] args) {};
}
