package firefighting;

import java.util.Random;

public class Robot {
    private Point position;
    private Point targetLocation;
    
    private boolean carryingSurvivor; // Indique si le robot transporte un survivant
    
    private final Random random = new Random();
    private final Battery battery;
    private final WaterTank waterTank;
    private final Communication communication;
    private final int id;
    private final SwarmCoordinator coordinator;

    public Robot(int id, int x, int y, Grid grid, SwarmCoordinator coordinator) {
        this.id = id;
        this.position = new Point(x, y);
        this.carryingSurvivor = false;
        this.battery = new Battery();
        this.waterTank = new WaterTank();
        this.communication = new Communication(grid);
        this.coordinator = coordinator;
        this.targetLocation = null;
    }

    /**
     * Action principale du robot. Elle est appelée à chaque tour de simulation.
     */
     public void act(Grid grid) {
        // Mise à jour de la carte d'exploration via le coordinateur
        coordinator.updateExplorationMap(position.x, position.y, 3);
        
        // Mise à jour de la cible si nécessaire
        if (targetLocation == null || reachedTarget()) {
            targetLocation = coordinator.assignTarget(this);
        }

        // Si la batterie est en charge, vérifier si elle est terminée
        if (battery.isCharging()) {
            if (battery.updateChargingStatus()) {
                System.out.println("Robot " + id + " finished charging");
            }
            return;
        }

        // Si le robot est en train de déployer de l'eau, vérifier si c'est terminé
        if (waterTank.isDeploying()) {
            if (waterTank.updateDeploymentStatus()) {
                System.out.println("Robot " + id + " finished deploying water");
            }
            return;
        }

        // Vérifier si la batterie nécessite une recharge
        if (battery.needsCharging()) {
            moveToBase();
            if (position.x == 0 && position.y == 0) {
                battery.startCharging();
                System.out.println("Robot " + id + " started charging");
            }
            return;
        }

        // Update local map when at base
        // if (position.x == 0 && position.y == 0 && communication.needsUpdate()) {
        //     communication.updateLocalMap();
        //     System.out.println("Robot " + id + " updated local map");
        // }

        // Standard movement logic
        moveAndAct(grid);
        
        // Consume battery
        battery.consume(1);
    }


    /**
     * Logique de déplacement et d'interaction avec la grille.
     */
    private void moveAndAct(Grid grid) {
        int gridSize = grid.getGridSize();
        int[][] localGrid = communication.getLocalMap();
        
        // Priorité 1 : Sauver les survivants en danger
        if (!carryingSurvivor) {
            Point nearestSurvivor = findNearest(localGrid, Grid.getSURVIVOR());
            if (nearestSurvivor != null && isNearFire(nearestSurvivor, localGrid)) {
                moveTo(new Point(nearestSurvivor.x, nearestSurvivor.y));
                carrySurvivor(grid.getGrid());
                return;
            }
        }
        
        // Priorité 2 : Ramener les survivants à la base
        if (carryingSurvivor) {
            moveToBase();
            if (position.x == 0 && position.y == 0) {
                carryingSurvivor = false;
                System.out.println("Robot " + id + " safely delivered survivor to base");
            }
            return;
        }
        
        // Priorité 3 : Éteindre les incendies proches des survivants
        Point nearestFireNearSurvivor = findFireNearSurvivor(localGrid);
        if (nearestFireNearSurvivor != null) {
            moveTo(new Point(nearestFireNearSurvivor.x, nearestFireNearSurvivor.y));
            waterTank.startDeployment();
            extinguishFire(grid.getGrid());
            return;
        }

        // Priorité 4 : Explorer les zones inconnues
        Point unexploredPoint = findUnexplored(localGrid);
        if (unexploredPoint != null) {
            moveTo(new Point(unexploredPoint.x, unexploredPoint.y));
            return;
        }
        
        // Dernier recours : Se déplacer aléatoirement
        moveRandomly(gridSize);
    }


    // Trouve l'élément le plus proche (incendie ou survivant)
    private Point findNearest(int[][] grid, int target) {
        int minDist = Integer.MAX_VALUE;
        Point nearest = null;
        
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == target) {
                    int dist = Math.abs(position.x - i) + Math.abs(position.y - j);
                    if (dist < minDist) {
                        minDist = dist;
                        nearest = new Point(i, j);
                    }
                }
            }
        }
        return nearest;
    }

    // Vérifie si un emplacement est proche d'un incendie
    private boolean isNearFire(Point location, int[][] grid) {
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
        
        for (int i = 0; i < 8; i++) {
            int nx = location.x + dx[i];
            int ny = location.y + dy[i];
            if (isValid(nx, ny, grid.length) && grid[nx][ny] == Grid.getFIRE()) {
                return true;
            }
        }
        return false;
    }

    private Point findFireNearSurvivor(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Point location = new Point(i, j);
                if (grid[i][j] == Grid.getSURVIVOR() && isNearFire(location, grid)) {
                    return findNearest(grid, Grid.getFIRE());
                }
            }
        }
        return null;
    }

    private Point findUnexplored(int[][] grid) {
        int radius = 3;
        int[][] visited = new int[grid.length][grid[0].length];

        // Marquer la position actuelle comme visitée
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int nx = position.x + i;
                int ny = position.y + j;
                if (isValid(nx, ny, grid.length)) {
                    visited[nx][ny] = 1;
                }
            }
        }
        
        // Trouver le point inexploré le plus proche
        Point nearest = null;
        int minDist = Integer.MAX_VALUE;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (visited[i][j] == 0) {
                    int dist = Math.abs(position.x - i) + Math.abs(position.y - j);
                    if (dist < minDist) {
                        minDist = dist;
                        nearest = new Point(i, j);
                    }
                }
            }
        }
        return nearest;
    }

    // Extinction d'un incendie
    private void extinguishFire(int[][] grid) {
        if (grid[position.x][position.y] == Grid.getFIRE()) {
            grid[position.x][position.y] = Grid.getASH();
            System.out.println("Robot " + id + " extinguished fire at (" + position.x + ", " + position.y + ")");
        }
    }

    // Récupération d'un survivant
    private void carrySurvivor(int[][] grid) {
    if (grid[position.x][position.y] == Grid.getSURVIVOR()) {
            grid[position.x][position.y] = Grid.getEMPTY();
            carryingSurvivor = true;
            System.out.println("Robot " + id + " picked up survivor at (" + position.x + ", " + position.y + ")");
        }
    }

    private void moveToBase() {
        int newX = position.x;
        int newY = position.y;
        
        if (newX  > 0) newX --;
        else if (newX  < 0) newX ++;
        if (newY > 0) newY--;
        else if (newY < 0) newY++;

        position = new Point(newX, newY);
    }

    private void moveTo(Point newPosition) {
        this.position = newPosition;
    }

    private void moveRandomly(int gridSize) {
        int nx, ny;
        do {
            int dx = random.nextInt(3) - 1; // -1, 0, or 1
            int dy = random.nextInt(3) - 1; // -1, 0, or 1
            nx = position.x + dx;
            ny = position.y + dy;
        } while (!isValid(nx, ny, gridSize));
        
        position = new Point(nx, ny);
    }

    private boolean reachedTarget() {
        return targetLocation != null && 
            position.x == targetLocation.x && 
            position.y == targetLocation.y;
    }

    public int getTargetX() {
        return targetLocation != null ? targetLocation.x : -1;
    }

    public int getTargetY() {
        return targetLocation != null ? targetLocation.y : -1;
    }

    private boolean isValid(int nx, int ny, int gridSize) {
        return nx >= 0 && ny >= 0 && nx < gridSize && ny < gridSize;
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    public boolean isCarryingSurvivor() {
        return carryingSurvivor;
    }
}
