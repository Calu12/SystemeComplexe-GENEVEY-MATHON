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
                
                // Augmenter la priorité des zones non explorées
                if (explorationMap[i][j] == 0) {
                    priorityMap[i][j] += 2;
                }
                
                // Priorité élevée pour les incendies proches de survivants
                if (hasNearbyFire(i, j) && hasNearbySurvivor(i, j)) {
                    priorityMap[i][j] += 5;
                }
                
                // Priorité moyenne pour les incendies isolés
                if (grid.getGrid()[i][j] == Grid.getFIRE()) {
                    priorityMap[i][j] += 3;
                }
                
                // Priorité maximale pour les survivants en danger immédiat (proches d'un incendie)
                if (grid.getGrid()[i][j] == Grid.getSURVIVOR() && hasAdjacentFire(i, j)) {
                    priorityMap[i][j] += 10;
                }
            }
        }
    }
    
    /**
    * Assigne une nouvelle cible à un robot en fonction des priorités.
    * @param robot Le robot pour lequel une cible doit être assignée.
    * @return La position de la cible assignée.
    */
    public Point assignTarget(Robot robot) {
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
    
    /**
    * Vérifie si une position est déjà ciblée par un autre robot.
    * @param x La coordonnée X de la position.
    * @param y La coordonnée Y de la position.
    * @param currentRobot Le robot actuel.
    * @return Vrai si la position est ciblée par un autre robot, sinon faux.
    */
    private boolean isTargetedByOtherRobot(int x, int y, Robot currentRobot) {
        for (Robot robot : robots) {
            if (robot != currentRobot && robot.getTargetX() == x && robot.getTargetY() == y) {
                return true;
            }
        }
        return false;
    }
    
    /**
    * Met à jour la carte d'exploration autour d'une position donnée.
    * @param x La coordonnée X du centre.
    * @param y La coordonnée Y du centre.
    * @param radius Le rayon d'exploration.
    */
    public void updateExplorationMap(int x, int y, int radius) {
        for (int i = Math.max(0, x - radius); i < Math.min(grid.getGridSize(), x + radius + 1); i++) {
            for (int j = Math.max(0, y - radius); j < Math.min(grid.getGridSize(), y + radius + 1); j++) {
                explorationMap[i][j] = 1;
            }
        }
    }
    
    // Méthodes auxiliaires pour vérifier l'état des cellules voisines

    /**
    * Vérifie si un incendie est proche d'une position donnée.
    */
    private boolean hasNearbyFire(int x, int y) {
        return checkSurroundingCells(x, y, Grid.getFIRE(), 2);
    }
    
    /**
    * Vérifie si un survivant est proche d'une position donnée.
    */
    private boolean hasNearbySurvivor(int x, int y) {
        return checkSurroundingCells(x, y, Grid.getSURVIVOR(), 2);
    }
    
    /**
    * Vérifie si un incendie est adjacent à une position donnée.
    */
    private boolean hasAdjacentFire(int x, int y) {
        return checkSurroundingCells(x, y, Grid.getFIRE(), 1);
    }
    
    /**
    * Vérifie si une cellule cible est présente dans un rayon donné.
    * @param x La coordonnée X de la cellule centrale.
    * @param y La coordonnée Y de la cellule centrale.
    * @param targetValue La valeur recherchée (incendie ou survivant).
    * @param radius Le rayon de recherche.
    * @return Vrai si une cellule cible est trouvée, sinon faux.
    */
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

    /**
    * Définit une nouvelle grille pour le coordinateur.
    */
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    /**
    * Définit les robots gérés par le coordinateur.
    */
    public void setRobots(Robot[] robots) {
        this.robots = robots;
    }
}