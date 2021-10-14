package MindustryBR.internal.classes.history.entry;

public class JoinLeaveEntry implements BaseEntry {
    boolean join;
    String name;

    JoinLeaveEntry(String name) {
        this(name, true);
    }

    JoinLeaveEntry(String name, boolean join) {
        this.name = name;
        this.join = join;
    }

    @Override
    public String getMessage() {
        return join ? "+ [green]Conectou[white] usando o nome " + this.name + "[white]" : "- [red]Desconectou[white]";
    }
}
