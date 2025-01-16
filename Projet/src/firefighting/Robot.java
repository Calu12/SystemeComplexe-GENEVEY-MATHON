package firefighting;

import java.util.Random;

public class Robot {
    private Point position;
    private boolean carryingSurvivor; // Indique si le robot transporte un survivant
    private final Random random = new Random();
    private final Battery battery;
    private final WaterTank waterTank;
    private final Communication communication;
    private final int id;

    public Robot(int id, int x, int y, Grid grid) {
        this.id = id;
        this.position = new Point(x, y);
        this.carryingSurvivor = false;
        this.battery = new Battery();
        this.waterTank = new WaterTank();
        this.communication = new Communication(grid);
    }

    public void act(Grid grid) {
        // Skip action if charging or deploying water
        if (battery.isCharging()) {
            if (battery.updateChargingStatus()) {
                System.out.println("Robot " + id + " finished charging");
            }
            return;
        }

        if (waterTank.isDeploying()) {
            if (waterTank.updateDeploymentStatus()) {
                System.out.println("Robot " + id + " finished deploying water");
            }
            return;
        }

        // Check if battery needs charging
        if (battery.needsCharging()) {
            moveToBase();
            if (x == 0 && y == 0) {
                battery.startCharging();
                System.out.println("Robot " + id + " started charging");
            }
            return;
        }

        // Update local map when at base
        if (x == 0 && y == 0 && communication.needsUpdate()) {
            communication.updateLocalMap();
            System.out.println("Robot " + id + " updated local map");
        }

        // Standard movement logic
        moveAndAct(grid);
        
        // Consume battery
        battery.consume(1);
    }

private void moveAndAct(Grid grid) {
    int gridSize = grid.getGridSize();
    int[][] localGrid = communication.getLocalMap();
    
    // Priority 1: Rescue survivors in danger
    if (!carryingSurvivor) {
        Point nearestSurvivor = findNearest(localGrid, SURVIVOR);
        if (nearestSurvivor != null && isNearFire(nearestSurvivor.x, nearestSurvivor.y, localGrid)) {
            moveTo(nearestSurvivor.x, nearestSurvivor.y);
            carrySurvivor(grid.getGrid());
            return;
        }
    }
    
    // Priority 2: Return survivor to base
    if (carryingSurvivor) {
        moveToBase();
        if (x == 0 && y == 0) {
            carryingSurvivor = false;
            System.out.println("Robot " + id + " safely delivered survivor to base");
        }
        return;
    }
    
    // Priority 3: Fight fires near survivors
    Point nearestFireNearSurvivor = findFireNearSurvivor(localGrid);
    if (nearestFireNearSurvivor != null) {
        moveTo(nearestFireNearSurvivor.x, nearestFireNearSurvivor.y);
        waterTank.startDeployment();
        extinguishFire(grid.getGrid());
        return;
    }
    
    // Priority 4: Explore unexplored areas
    Point unexploredPoint = findUnexplored(localGrid);
    if (unexploredPoint != null) {
        moveTo(unexploredPoint.x, unexploredPoint.y);
        return;
    }
    
    // Last resort: Move randomly
    moveRandomly(gridSize);
}

private Point findNearest(int[][] grid, int target) {
    int minDist = Integer.MAX_VALUE;
    Point nearest = null;
    
    for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[i][j] == target) {
                int dist = Math.abs(x - i) + Math.abs(position.y - j);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = new Point(i, j);
                }
            }
        }
    }
    return nearest;
}

private boolean isNearFire(int x, int y, int[][] grid) {
    int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
    int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
    
    for (int i = 0; i < 8; i++) {
        int nx = location.x + dx[i];
        int ny = location.y + dy[i];
        if (isValid(nx, ny, grid.length) && grid[nx][ny] == FIRE) {
            return true;
        }
    }
    return false;
}

private Point findFireNearSurvivor(int[][] grid) {
    for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
            Point location = new Point(i, j);
            if (grid[i][j] == SURVIVOR && isNearFire(location, grid)) {
                return findNearest(grid, FIRE);
            }
        }
    }
    return null;
}

private Point findUnexplored(int[][] grid) {
    int radius = 3;
    int[][] visited = new int[grid.length][grid[0].length];
    
    for (Robot robot : robots) {
        int rx = robot.getX();
        int ry = robot.getY();
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int nx = rx + i;
                int ny = ry + j;
                if (isValid(nx, ny, grid.length)) {
                    visited[nx][ny] = 1;
                }
            }
        }
    }
    
    // Find nearest unexplored point
    Point nearest = null;
    int minDist = Integer.MAX_VALUE;
    for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
            if (visited[i][j] == 0) {
                int dist = Math.abs(x - i) + Math.abs(y - j);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = new Point(i, j);
                }
            }
        }
    }
    return nearest;
}

    private void extinguishFire(int[][] grid) {
        if (grid[x][y] == 2) { // Si la case actuelle est en feu
            grid[x][y] = 3; // Le feu est éteint, la case devient cendre
            System.out.println("Robot a éteint un feu à (" + x + ", " + y + ")");
        }
    }

    private void carrySurvivor(int[][] grid) {
        if (grid[x][y] == 4) { // Si la case actuelle contient un survivant
            grid[x][y] = 0; // Enlève le survivant de la grille
            carryingSurvivor = true;
            System.out.println("Robot a pris un survivant à (" + x + ", " + y + ")");
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
