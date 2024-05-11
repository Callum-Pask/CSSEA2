package sheep.core;

/**
 * Indicates whether an update action was successful or not.
 * <p>
 * If the action is successful {@link UpdateResponse#isSuccess()} returns true.
 * Otherwise, {@link UpdateResponse} will store a message ({@link UpdateResponse#getMessage()})
 * to indicate what went wrong.
 * @invariant ({@link UpdateResponse#isSuccess()} &amp;&amp; {@link UpdateResponse#getMessage()} == null) ||
 *            (!{@link UpdateResponse#isSuccess()} &amp;&amp; {@link UpdateResponse#getMessage()} != null)
 */
public class UpdateResponse {
    private final boolean success;
    private final String message;

    private UpdateResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Construct a successful {@link UpdateResponse}.
     *
     * @return An {@link UpdateResponse} which returns true for
     * {@link UpdateResponse#isSuccess()} and has no message.
     */
    public static UpdateResponse success() {
        return new UpdateResponse(true, null);
    }

    /**
     * Construct a failed {@link UpdateResponse}.
     *
     * @param message A message indicating what went wrong with the attempted update.
     * @requires message != null
     * @return An {@link UpdateResponse} which returns false for
     * {@link UpdateResponse#isSuccess()} and the provided message for
     * {@link UpdateResponse#getMessage()}.
     */
    public static UpdateResponse fail(String message) {
        return new UpdateResponse(false, message);
    }

    /**
     * Returns true if the update was successful, otherwise false.
     * @return True if the update was successful, otherwise false.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns A message to explain why an update failed.
     * If the update was successful, this will return null.
     * @return A message to explain why an update failed.
     */
    public String getMessage() {
        return message;
    }
}
