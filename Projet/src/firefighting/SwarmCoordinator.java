package firefighting;


/**
 * Classe responsable de la coordination des robots dans un essaim.
 * Elle gère les priorités et les affectations des cibles en fonction de l'état de la grille.
 */

public class SwarmCoordinator {
    private Robot[] robots;
    private Grid grid;
    private final int[][] explorationMap; // Carte indiquant les zones déjà explorées
    private final int[][] priorityMap;    // Carte des priorités pour les différentes zones de la grille
    
    /**
     * Constructeur de la classe SwarmCoordinator.
     * @param robots Les robots à coordonner.
     * @param grid La grille sur laquelle opèrent les robots.
     */
    public SwarmCoordinator(Robot[] robots, Grid grid) {
        this.robots = robots;
        this.grid = grid;
        this.explorationMap = new int[grid.getGridSize()][grid.getGridSize()];
        this.priorityMap = new int[grid.getGridSize()][grid.getGridSize()];
    }
    
    /**
    * Met à jour la carte des priorités en fonction de l'état actuel de la grille.
    */
    public void updatePriorityMap() {
        for (int i = 0; i < grid.getGridSize(); i++) {
            for (int j = 0; j < grid.getGridSize(); j++) {
                priorityMap[i][j] = 0; // Réinitialisation des priorités
                
                // Increase priority for unexplored areas
                if (explorationMap[i][j] == 0) {
                    priorityMap[i][j] += 2;
                }
                
                // High priority for fires near survivors
                if (hasNearbyFire(i, j) && hasNearbySurvivor(i, j)) {
                    priorityMap[i][j] += 5;
                }
                
                // Medium priority for standalone fires
                if (grid.getGrid()[i][j] == Grid.getFIRE()) {
                    priorityMap[i][j] += 3;
                }
                
                // Highest priority for survivors in immediate danger
                if (grid.getGrid()[i][j] == Grid.getSURVIVOR() && hasAdjacentFire(i, j)) {
                    priorityMap[i][j] += 10;
                }
            }
        }
    }
    
    public Point assignTarget(Robot robot) {
        // Find highest priority unexplored area not targeted by other robots
        Point bestTarget = null;
        int highestPriority = -1;
        
        for (int i = 0; i < grid.getGridSize(); i++) {
            for (int j = 0; j < grid.getGridSize(); j++) {
                if (priorityMap[i][j] > highestPriority && !isTargetedByOtherRobot(i, j, robot)) {
                    highestPriority = priorityMap[i][j];
                    bestTarget = new Point(i, j);
                }
            }
        }
        
        return bestTarget;
    }
    
    private boolean isTargetedByOtherRobot(int x, int y, Robot currentRobot) {
        for (Robot robot : robots) {
            if (robot != currentRobot && robot.getTargetX() == x && robot.getTargetY() == y) {
                return true;
            }
        }
        return false;
    }
    
    public void updateExplorationMap(int x, int y, int radius) {
        for (int i = Math.max(0, x - radius); i < Math.min(grid.getGridSize(), x + radius + 1); i++) {
            for (int j = Math.max(0, y - radius); j < Math.min(grid.getGridSize(), y + radius + 1); j++) {
                explorationMap[i][j] = 1;
            }
        }
    }
    
    // Helper methods for checking surrounding cells
    private boolean hasNearbyFire(int x, int y) {
        return checkSurroundingCells(x, y, Grid.getFIRE(), 2);
    }
    
    private boolean hasNearbySurvivor(int x, int y) {
        return checkSurroundingCells(x, y, Grid.getSURVIVOR(), 2);
    }
    
    private boolean hasAdjacentFire(int x, int y) {
        return checkSurroundingCells(x, y, Grid.getFIRE(), 1);
    }
    
    private boolean checkSurroundingCells(int x, int y, int targetValue, int radius) {
        for (int i = Math.max(0, x - radius); i < Math.min(grid.getGridSize(), x + radius + 1); i++) {
            for (int j = Math.max(0, y - radius); j < Math.min(grid.getGridSize(), y + radius + 1); j++) {
                if (grid.getGrid()[i][j] == targetValue) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public void setRobots(Robot[] robots) {
        this.robots = robots;
    }
}