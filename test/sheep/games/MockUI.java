package sheep.games;

import sheep.core.SheetUpdate;
import sheep.core.SheetView;
import sheep.ui.OnChange;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

import java.util.*;

public class MockUI extends UI {

    private String expectingAsk = null;
    private String[] expectingAskMany = null;
    private String expectingAskYesNo = null;
    private String expectingMessage = null;

    private List<String> actualMessages = new ArrayList<>();

    private class MockPrompt implements Prompt {
        @Override
        public Optional<String> ask(String prompt) {
            if (expectingAsk == null) {
                throw new AssertionError("Unexpected ask: " + prompt);
            }
            if (!expectingAsk.equals(prompt)) {
                throw new AssertionError("Expected ask: " + expectingAsk + " but got " + prompt);
            }
            return Optional.empty();
        }

        @Override
        public Optional<String[]> askMany(String[] prompts) {
            if (expectingAskMany == null) {
                throw new AssertionError("Unexpected ask: " + Arrays.toString(prompts));
            }
            if (!Arrays.equals(expectingAskMany, prompts)) {
                throw new AssertionError("Expected ask: " + Arrays.toString(expectingAskMany) + " but got " + Arrays.toString(prompts));
            }
            return Optional.empty();
        }

        @Override
        public boolean askYesNo(String prompt) {
            if (expectingAskYesNo == null) {
                throw new AssertionError("Unexpected ask: " + prompt);
            }
            if (!expectingAskYesNo.equals(prompt)) {
                throw new AssertionError("Expected ask: " + expectingAskYesNo + " but got " + prompt);
            }
            return false;
        }

        @Override
        public void message(String prompt) {
            if (expectingMessage == null) {
                throw new AssertionError("Unexpected message: " + prompt);
            }
            if (!expectingMessage.equals(prompt)) {
                throw new AssertionError("Expected message: " + expectingMessage + " but got " + prompt);
            }
            actualMessages.add(prompt);
        }
    }

    public void expectAsk(String message) {
        expectingAsk = message;
    }

    public void expectAskMany(String[] messages) {
        expectingAskMany = messages;
    }

    public void expectAskYesNo(String message) {
        expectingAskYesNo = message;
    }

    public void expectMessage(String message) {
        expectingMessage = message;
    }

    public List<String> getActualMessages() {
        return new ArrayList<>(actualMessages);
    }

    private MockPrompt prompt = new MockPrompt();

    public MockUI(SheetView view, SheetUpdate updater) {
        super(view, updater);
    }

    public void simulatePress(String key, int row, int column) {
        List<Feature> bound = keys.get(key);
        for (Feature feature : bound) {
            feature.action().perform(row, column, prompt);
        }
    }

    public void simulateFeature(String key, int row, int column) {
        Feature bound = features.get(key);
        bound.action().perform(row, column, prompt);
    }

    public void simulateTick() {
        for (Tick change : tickCallbacks) {
            change.onTick(prompt);
        }
    }

    @Override
    public void render() {

    }
}
