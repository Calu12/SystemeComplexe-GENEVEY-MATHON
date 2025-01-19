package firefighting;

import java.util.Random;

public class Grid {
    public static final int EMPTY = 0;
    public static final int TREE = 1;
    public static final int FIRE = 2;
    public static final int ASH = 3;
    public static final int SURVIVOR = 4;
    public static final double FIRE_SPREAD_DECAY = 0.1; // Facteur de réduction de la probabilité de propagation du feu.

    private final int gridSize;
    private final double fireSpreadProb;
    private final int burnTime;
    private final double treePercentage;
    private final int[][] grid;
    private final int[][] burnTimes;
    private Robot[] robots;
    private final Random random;
    private SwarmCoordinator coordinator;

    public Grid(int gridSize, double fireSpreadProb, int burnTime, double treePercentage, int initialFires, int numSurvivors, int numRobots) {
        this.gridSize = gridSize;
        this.fireSpreadProb = fireSpreadProb;
        this.burnTime = burnTime;
        this.treePercentage = treePercentage;
        this.grid = new int[gridSize][gridSize];
        this.burnTimes = new int[gridSize][gridSize];
        this.random = new Random();
        this.robots = new Robot[numRobots];
        // this.coordinator = null;
            
        // Create SwarmCoordinator instance first
        // this.coordinator = new SwarmCoordinator(robots, this);
        
        // Initialize robots with the coordinator
        // for (int i = 0; i < numRobots; i++) {
        //     robots[i] = new Robot(i, 0, 0, this, coordinator);
        // }
        initializeGrid(initialFires, numSurvivors, numRobots);
    }
    
    // Définir les robots de la simulation.
    public void setRobots(Robot[] robots) {
        this.robots = robots;
    }
    
    // Associer le coordinateur des robots.
    public void setCoordinator(SwarmCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    // Retourne l'état actuel de la grille.
    public int[][] getGrid() {
        return grid;
    }
    
    // Retourne la taille de la grille.
    public int getGridSize() {
        return gridSize;
    }

    // Vérifie si une case est adjacente à une case en feu (en tenant compte de la probabilité de propagation réduite).    
    private boolean isAdjacentToFire(int x, int y) {
        int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};
        for (int i = 0; i < 8; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize && grid[nx][ny] == FIRE) {
                return random.nextDouble() < (fireSpreadProb * (1 - FIRE_SPREAD_DECAY * 
                    Math.sqrt(dx[i]*dx[i] + dy[i]*dy[i])));
            }
        }
        return false;
    }

    // Initialise la grille avec les arbres, les feux initiaux, les survivants et les robots.
    private void initializeGrid(int initialFires, int numSurvivors, int numRobots) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = random.nextDouble() < treePercentage ? TREE : EMPTY;
                burnTimes[i][j] = 0;
            }
        }

        // Ajout des feux initiaux.
        for (int i = 0; i < initialFires; i++) {
            Point location;
            do {
                location = new Point(random.nextInt(gridSize), random.nextInt(gridSize));
            } while (grid[location.x][location.y] != TREE);
            grid[location.x][location.y] = FIRE;
            burnTimes[location.x][location.y] = burnTime;
        }

        // Ajout des survivants.
        for (int i = 0; i < numSurvivors; i++) {
            Point location;
            do {
                location = new Point(random.nextInt(gridSize), random.nextInt(gridSize));
            } while (grid[location.x][location.y] != EMPTY);
            grid[location.x][location.y] = SURVIVOR;
        }

        // Ajout des robots.
        for (int i = 0; i < numRobots; i++) {
            robots[i] = new Robot(i, 0, 0, this, coordinator);
        }
    }

    // Simule une étape de la propagation du feu et les actions des robots.
    public void simulateStep() {
        // Mise à jour des cases en feu.
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (grid[i][j] == FIRE) {
                    burnTimes[i][j]--; // Réduit le temps de combustion.
                    if (burnTimes[i][j] <= 0) {
                        grid[i][j] = ASH; // Devient de la cendre.
                    }
                } else if (grid[i][j] == TREE) {
                    // Propagation du feu à un arbre.
                    if (isAdjacentToFire(i, j) && random.nextDouble() < fireSpreadProb) {
                        grid[i][j] = FIRE;
                        burnTimes[i][j] = burnTime;
                    }
                }
            }
        }

        // Actions des robots.
        for (Robot robot : robots) {
            robot.act(this);
        }
    }

    private boolean isAdjacentToFire(Point location) {
        int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};

        for (int i = 0; i < 4; i++) {
            int nx = location.x + dx[i];
            int ny = location.y + dy[i];
            if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize && grid[nx][ny] == FIRE) {
                return random.nextDouble() < (fireSpreadProb * (1 - FIRE_SPREAD_DECAY * 
                    Math.sqrt(dx[i]*dx[i] + dy[i]*dy[i])));
            }
        }
        return false;
    }
    
    // Affiche la grille actuelle dans la console.
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
    public static int getSURVIVOR() {
        return SURVIVOR;
    }

    public static int getFIRE() {
        return FIRE;
    }

    public static int getASH() {
        return ASH;
    }

    public static int getEMPTY() {
        return EMPTY;
    }
}

