package me.zeroeightsix.botframework.plugin.command;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.google.common.collect.Lists;
import me.zeroeightsix.botframework.MinecraftBot;
import me.zeroeightsix.botframework.plugin.Plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public abstract class ChatCommand implements IChatCommand {

    String label;
    String description = "Descriptionless";
    protected Plugin parent = null;

    boolean admin;
    List<Predicate<String>> permissions = Lists.newArrayList(); // permissions that admin might not me a appropriate for

    protected void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdminCommand() {
        return admin;
    }

    protected void addPermission(Predicate<String> condition) {
        permissions.add(condition);
    }

    public Collection<Predicate<String>> getPermissions() {
        return Collections.unmodifiableCollection(permissions);
    }

    public ChatCommand(String label) {
        this.label = label;
    }

    public ChatCommand(String label, Plugin parent) {
        this(label);
        this.parent = parent;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public static ChatCommand createCommand(String name, IChatCommand run, Plugin parent) {
        return new ChatCommand(name, parent) {
            @Override
            public void call(String username, String[] args) {
                try{
                    run.call(username, args);
                }catch (Exception e){
                    MinecraftBot.getLogger().severe("Error running command " + name + "!");
                    e.printStackTrace();
                    sendChatMessage("Oops, an error occured running that command! :(");
                }
            }
        };
    }

    public void sendChatMessage(String message){
        if (parent != null)
            parent.sendChatMessage(message);
        else
            MinecraftBot.getInstance().getClient().getSession().send(new ClientChatPacket(message));
    }

}
