package firefighting;

import java.util.Random;

public class Robot {
    private int x, y; // Position actuelle
    private boolean carryingSurvivor; // Indique si le robot transporte un survivant
    private final Random random = new Random();

    public Robot(int x, int y) {
        this.x = x;
        this.y = y;
        this.carryingSurvivor = false;
    }

    public void act(int[][] grid) {
        int gridSize = grid.length;

        // Vérifie les cases adjacentes
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        // Priorité 1 : Aller vers un survivant adjacent
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (isValid(nx, ny, gridSize) && grid[nx][ny] == 4) { // Survivant
                moveTo(nx, ny);
                carrySurvivor(grid);
                return;
            }
        }

        // Priorité 2 : Aller vers un feu adjacent
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (isValid(nx, ny, gridSize) && grid[nx][ny] == 2) { // Feu
                moveTo(nx, ny);
                extinguishFire(grid);
                return;
            }
        }

        // Priorité 3 : Si le robot transporte un survivant, retour à la base (0, 0)
        if (carryingSurvivor) {
            moveToBase();
            if (x == 0 && y == 0) { // Dépose le survivant à la base
                carryingSurvivor = false;
                System.out.println("Robot a déposé un survivant à la base !");
            }
            return;
        }

        // Déplacement aléatoire
        moveRandomly(gridSize);
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
        if (x > 0) x--;
        else if (x < 0) x++;
        if (y > 0) y--;
        else if (y < 0) y++;
    }

    private void moveTo(int nx, int ny) {
        x = nx;
        y = ny;
    }

    private void moveRandomly(int gridSize) {
        int nx, ny;
        do {
            int dx = random.nextInt(3) - 1; // -1, 0, ou 1
            int dy = random.nextInt(3) - 1; // -1, 0, ou 1
            nx = x + dx;
            ny = y + dy;
        } while (!isValid(nx, ny, gridSize)); // Vérifie que la nouvelle position est valide
        moveTo(nx, ny);
    }

    private boolean isValid(int nx, int ny, int gridSize) {
        return nx >= 0 && ny >= 0 && nx < gridSize && ny < gridSize;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isCarryingSurvivor() {
        return carryingSurvivor;
    }
}
