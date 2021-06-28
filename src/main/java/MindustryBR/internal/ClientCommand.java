package MindustryBR.internal;

import mindustry.gen.Player;

public class ClientCommand {
    /**
     * Command name
     */
    public String name;

    /**
     * Whether or not the command can only be used by admins
     */
    public Boolean adminOnly = false;

    /**
     * Function called when executing the command
     * @param args Command arguments
     * @param player Player that executed the command
     */
    public void run(String[] args, Player player) {}
}
