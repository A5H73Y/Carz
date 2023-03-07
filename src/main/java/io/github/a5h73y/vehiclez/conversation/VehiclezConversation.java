package io.github.a5h73y.vehiclez.conversation;

import io.github.a5h73y.vehiclez.Vehiclez;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;

public abstract class VehiclezConversation implements ConversationAbandonedListener {

    private final ConversationFactory conversationFactory;
    private final Conversable player;

    public abstract Prompt getEntryPrompt();

    /**
     * Vehiclez Conversation abstract class.
     * Each conversation must take a Conversable in the constructor.
     *
     * @param player conversable {@link Conversable}
     */
    public VehiclezConversation(Conversable player) {
        this.player = player;

        conversationFactory = new ConversationFactory(Vehiclez.getInstance())
                .withEscapeSequence("cancel")
                .withTimeout(30)
                .addConversationAbandonedListener(this)
                .withFirstPrompt(getEntryPrompt());

        player.sendRawMessage(ChatColor.GRAY + "Note: Enter 'cancel' to quit the conversation.");
    }

    public static void sendErrorMessage(ConversationContext context, String message) {
        context.getForWhom().sendRawMessage(ChatColor.RED + message + ". Please try again...");
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent event) {
        if (!event.gracefulExit()) {
            event.getContext().getForWhom().sendRawMessage(Vehiclez.getPrefix() + "Conversation aborted...");
        }
    }

    public void begin() {
        Conversation convo = conversationFactory.buildConversation(player);
        convo.begin();
    }
}
