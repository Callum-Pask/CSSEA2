package sheep.ui;

import java.util.Optional;

/**
 * The Prompt interface is a generic way to interact with a user interface.
 * <p>
 * It allows common interactions with any user interface,
 * such as displaying a message or asking for input.
 */
public interface Prompt {
    /**
     * Ask the user to input a string.
     *
     * @param prompt Prompt will be displayed to the user with the input field.
     * @return The string the user entered or {@link Optional#empty()} if the
     * user cancelled the operation.
     */
    Optional<String> ask(String prompt);

    /**
     * Ask the user to input a sequence of strings.
     *
     * @param prompts Each prompt will be displayed in sequence when gathering
     *                the input sequence from the user.
     * @return The strings the user entered or {@link Optional#empty()} if the
     * user cancelled the operation.
     */
    Optional<String[]> askMany(String[] prompts);

    /**
     * Ask the user a yes or no question.
     *
     * @param prompt The question to ask the user.
     * @return True iff the user answers affirmatively.
     */
    boolean askYesNo(String prompt);

    /**
     * Display a string message to the user.
     *
     * @param prompt The message to display.
     */
    void message(String prompt);
}
