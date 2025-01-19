package firefighting;

// La classe Communication gère l'échange d'informations entre le robot et la grille globale (Grid).
public class Communication {
    private final Grid grid; // Référence à la grille globale partagée par tous les robots.
    private final int[][] localMap; // Carte locale que le robot utilise pour ses actions.
    private final int gridSize;
    private long lastUpdateTime; // Temps de la dernière mise à jour de la carte locale.
    private static final long UPDATE_INTERVAL = 1000; // Intervalle minimum entre deux mises à jour en millisecondes.

    public Communication(Grid grid) {
        this.grid = grid;
        this.gridSize = grid.getGridSize();
        this.localMap = new int[gridSize][gridSize];
        updateLocalMap();
    }

    // Met à jour la carte locale en copiant les données de la grille globale.
    public void updateLocalMap() {
        for (int i = 0; i < gridSize; i++) {
            // Copie les lignes de la grille globale dans la carte locale.
            System.arraycopy(grid.getGrid()[i], 0, localMap[i], 0, gridSize);
        }
        lastUpdateTime = System.currentTimeMillis();
    }

    // Vérifie si la carte locale a besoin d'être mise à jour.
    public boolean needsUpdate() {
        // Compare le temps écoulé depuis la dernière mise à jour avec l'intervalle défini.
        return System.currentTimeMillis() - lastUpdateTime >= UPDATE_INTERVAL;
    }

    // Retourne la carte locale actuelle.
    public int[][] getLocalMap() {
        return localMap;
    }
}