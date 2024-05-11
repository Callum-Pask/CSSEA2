package sheep.games.random;

import java.util.Random;

public class RandomTetrosTile implements RandomTile {
    private Random random;

    public RandomTetrosTile(Random random) {
        this.random = random;
    }

    @Override
    public int pick() {
        return random.nextInt(6);
    }
}
