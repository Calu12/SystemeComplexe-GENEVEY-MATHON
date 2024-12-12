package firefighting;

import java.util.Random;

public class Grid {
    private static final int EMPTY = 0;
    private static final int TREE = 1;
    private static final int FIRE = 2;
    private static final int ASH = 3;
    private static final int SURVIVOR = 4;

    private final int gridSize;
    private final double fireSpreadProb;
    private final int burnTime;
    private final double treePercentage;
    private final int[][] grid;
    private final int[][] burnTimes;
    private final Robot[] robots;
    private final Random random;

    public Grid(int gridSize, double fireSpreadProb, int burnTime, double treePercentage, int initialFires, int numSurvivors, int numRobots) {
        this.gridSize = gridSize;
        this.fireSpreadProb = fireSpreadProb;
        this.burnTime = burnTime;
        this.treePercentage = treePercentage;
        this.grid = new int[gridSize][gridSize];
        this.burnTimes = new int[gridSize][gridSize];
        this.random = new Random();
        this.robots = new Robot[numRobots];

        initializeGrid(initialFires, numSurvivors, numRobots);
    }

    private void initializeGrid(int initialFires, int numSurvivors, int numRobots) {
        // Fill grid with trees
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = random.nextDouble() < treePercentage ? TREE : EMPTY;
                burnTimes[i][j] = 0;
            }
        }

        // Add initial fires
        for (int i = 0; i < initialFires; i++) {
            int x, y;
            do {
                x = random.nextInt(gridSize);
                y = random.nextInt(gridSize);
            } while (grid[x][y] != TREE);
            grid[x][y] = FIRE;
            burnTimes[x][y] = burnTime;
        }

        // Add survivors
        for (int i = 0; i < numSurvivors; i++) {
            int x, y;
            do {
                x = random.nextInt(gridSize);
                y = random.nextInt(gridSize);
            } while (grid[x][y] != EMPTY);
            grid[x][y] = SURVIVOR;
        }

        // Add robots
        for (int i = 0; i < numRobots; i++) {
            robots[i] = new Robot(0, 0);
        }
    }

    public void simulateStep() {
        // Update fire spread and burning trees
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (grid[i][j] == FIRE) {
                    burnTimes[i][j]--;
                    if (burnTimes[i][j] <= 0) {
                        grid[i][j] = ASH;
                    }
                } else if (grid[i][j] == TREE) {
                    if (isAdjacentToFire(i, j) && random.nextDouble() < fireSpreadProb) {
                        grid[i][j] = FIRE;
                        burnTimes[i][j] = burnTime;
                    }
                }
            }
        }

        // Update robots
        for (Robot robot : robots) {
            robot.act(grid);
        }
    }

    private boolean isAdjacentToFire(int x, int y) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize && grid[nx][ny] == FIRE) {
                return true;
            }
        }
        return false;
    }

    public void printGrid() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                boolean robotHere = false;
                for (Robot robot : robots) {
                    if (robot.getX() == i && robot.getY() == j) {
                        System.out.print("R");
                        robotHere = true;
                        break;
                    }
                }
                if (!robotHere) {
                    switch (grid[i][j]) {
                        case EMPTY -> System.out.print(".");
                        case TREE -> System.out.print("T");
                        case FIRE -> System.out.print("F");
                        case ASH -> System.out.print("A");
                        case SURVIVOR -> System.out.print("S");
                    }
                }
            }
            System.out.println();
        }
    }
}

