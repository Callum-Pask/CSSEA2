package sheep.ui;


/**
 * A callback used whenever spreadsheet cell is updated.
 * This can be useful for triggering global events such as automatic saving.
 */
public interface OnChange {
    /**
     * The change method is called when the spreadsheet is updated.
     *
     * @param prompt Provide a way to interact with the user interface if
     *               required.
     */
    void change(Prompt prompt);
}
