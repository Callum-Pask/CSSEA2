package sheep.ui;

/**
 * The Perform interface is used to implement callbacks
 * for user interactions.
 */
public interface Perform {
    /**
     * Called when the user performs the pre-configured interaction.
     *
     * @param row The currently selected row of the user, or -2 if none selected.
     * @param column The currently selected column of the user, or -2 if none selected.
     * @param prompt Provides a mechanism to interact with the user interface
     *               after an interaction, if required.
     */
    void perform(int row, int column, Prompt prompt);
}
