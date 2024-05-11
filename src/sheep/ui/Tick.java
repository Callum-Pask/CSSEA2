package sheep.ui;

/**
 * Implementations of the Tick interface will be invoked at each
 * tick of the user interface.
 */
public interface Tick {
    /**
     * The onTick method is called whenever a tick occurs in the user interface.
     *
     * @param prompt Provide a mechanism to interact with the user interface
     *               after a tick occurs, if required.
     * @return True iff the spreadsheet needs to be re-rendered
     *         (i.e. if any cells changed).
     */
    boolean onTick(Prompt prompt);
}
