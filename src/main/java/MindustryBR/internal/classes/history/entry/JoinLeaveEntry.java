package MindustryBR.internal.classes.history.entry;

import mindustry.gen.Player;

public class JoinLeaveEntry implements BaseEntry {
    boolean join;
    Player player;

    JoinLeaveEntry(Player player) {
        this(player, true);
    }

    JoinLeaveEntry(Player player, boolean join) {
        this.player = player;
        this.join = join;
    }

    @Override
    public String getMessage() {
        return this.getMessage(true);
    }

    @Override
    public String getMessage(boolean withName) {
        return join ? "+ [green]Conectou[white] usando o nome " + this.player.name() + "[white]" : "- [red]Desconectou[white]";
    }
}
