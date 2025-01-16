package firefighting;

public class Communication {
    private final Grid grid;
    private final int[][] localMap;
    private final int gridSize;
    private long lastUpdateTime;
    private static final long UPDATE_INTERVAL = 1000; // Update interval in milliseconds

    public Communication(Grid grid) {
        this.grid = grid;
        this.gridSize = grid.getGridSize();
        this.localMap = new int[gridSize][gridSize];
        updateLocalMap();
    }

    public void updateLocalMap() {
        for (int i = 0; i < gridSize; i++) {
            System.arraycopy(grid.getGrid()[i], 0, localMap[i], 0, gridSize);
        }
        lastUpdateTime = System.currentTimeMillis();
    }

    public boolean needsUpdate() {
        return System.currentTimeMillis() - lastUpdateTime >= UPDATE_INTERVAL;
    }

    public int[][] getLocalMap() {
        return localMap;
    }
}