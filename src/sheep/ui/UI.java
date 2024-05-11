package sheep.ui;

import sheep.core.SheetUpdate;
import sheep.core.SheetView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UI represents an abstraction over an arbitrary user interface.
 * The base features are handled in the UI class
 * with the mechanism to render the UI handled by the abstract render method.
 */
public abstract class UI {
    /** A read-only view of a sheet */
    protected final SheetView view;
    /** An interface to update the sheet model */
    protected final SheetUpdate updater;

    /** Default tick speed in milliseconds */
    protected int tickSpeed = 1000;

    /** Methods to invoke whenever the sheet is updated. */
    protected final List<OnChange> changeCallbacks = new ArrayList<>();

    /** Methods to invoke on each tick of the spreadsheet. */
    protected final List<Tick> tickCallbacks = new ArrayList<>();

    /** Custom features registered in the UI */
    protected final Map<String, Feature> features = new HashMap<>();

    /** A mapping of keyboard shortcuts to custom features */
    protected final Map<String, List<Feature>> keys = new HashMap<>();

    /**
     * The feature class encapsulates a custom feature.
     *
     * @param name A human-identifiable label for the feature.
     * @param action A callback to invoke when the feature is triggered.
     */
    protected record Feature(String name, Perform action) {

    }

    /** Construct a new UI instance */
    public UI(SheetView view, SheetUpdate updater) {
        this.view = view;
        this.updater = updater;
    }

    /**
     * Register a method that should be invoked each time the spreadsheet
     * model is updated.
     * This is useful for features such as auto-saving.
     *
     * @param callback The method to invoke.
     */
    public void onChange(OnChange callback) {
        changeCallbacks.add(callback);
    }

    /**
     * Register a method that should be invoked on each tick.
     *
     * @param callback The method to invoke.
     */
    public void onTick(Tick callback) {
        tickCallbacks.add(callback);
    }

    /**
     * Modify the default tick speed (1000) of the sheet application.
     *
     * @param tickSpeed The new tick speed in milliseconds.
     * @requires tickSpeed > 0
     */
    public void setTickSpeed(int tickSpeed) {
        this.tickSpeed = tickSpeed;
    }

    /**
     * Register a new feature in the sheet interface.
     *
     * @param identifier A unique identifier of the feature.
     * @param name A human-identifiable label for the feature,
     *             this will be displayed to users in a list of features.
     * @param action A callback to invoke when the feature is triggered.
     */
    public void addFeature(String identifier, String name, Perform action) {
        this.features.put(identifier, new Feature(name, action));
    }

    /**
     * Register a new shortcut key binding.
     *
     * @param keyName The keyboard identifier of the shortcut, e.g. "a".
     * @param name A human-identifiable label for the feature,
     *             this may be displayed to users.
     * @param action A callback to invoke when the key is pressed.
     */
    public void onKey(String keyName, String name, Perform action) {
        if (!this.keys.containsKey(keyName)) {
            this.keys.put(keyName, new ArrayList<>());
        }
        this.keys.get(keyName).add(new Feature(name, action));
    }

    /**
     * An abstract method that will start the user interface.
     * This will be overridden in implementing classes.
     */
    public abstract void render();
}
