import java.util.Random;

public class FireZone {
    private boolean[][] fireGrid;
    private boolean[][] survivorGrid;
    private int width;
    private int height;

    public FireZone(int width, int height, int initialFires, int initialSurvivors) {
        this.width = width;
        this.height = height;
        fireGrid = new boolean[width][height];
        survivorGrid = new boolean[width][height];
        initializeFiresAndSurvivors(initialFires, initialSurvivors);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void initializeFiresAndSurvivors(int initialFires, int initialSurvivors) {
        Random random = new Random();
        for (int i = 0; i < initialFires; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            fireGrid[x][y] = true;
        }

        for (int i = 0; i < initialSurvivors; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            while (fireGrid[x][y]) { // Avoid placing survivors on fire
                x = random.nextInt(width);
                y = random.nextInt(height);
            }
            survivorGrid[x][y] = true;
        }
    }

    public boolean isOnFire(int x, int y) {
        return fireGrid[x][y];
    }

    public void extinguishFire(int x, int y) {
        if (fireGrid[x][y]) {
            fireGrid[x][y] = false;
        }
    }

    public boolean hasSurvivor(int x, int y) {
        return survivorGrid[x][y];
    }

    public void removeSurvivor(int x, int y) {
        survivorGrid[x][y] = false;
    }

    public void spreadFire() {
        Random random = new Random();
        boolean[][] newFireGrid = new boolean[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newFireGrid[i][j] = fireGrid[i][j];
                if (fireGrid[i][j]) {
                    boolean allNeighborsOnFire = true;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int nx = i + dx;
                            int ny = j + dy;
                            if (nx >= 0 && nx < width && ny >= 0 && ny < height && !(dx == 0 && dy == 0)) {
                                if (!fireGrid[nx][ny]) {
                                    allNeighborsOnFire = false;
                                    if (random.nextDouble() < 0.25) {
                                        newFireGrid[nx][ny] = true;
                                    }
                                }
                            }
                        }
                    }
                    if (allNeighborsOnFire) {
                        newFireGrid[i][j] = false;
                    }
                }
            }
        }
        fireGrid = newFireGrid;
    }
}
