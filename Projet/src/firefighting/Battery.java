package firefighting;

// La classe Battery gère l'énergie d'un robot, y compris sa consommation, son état de charge, et son temps de recharge.
public class Battery {
    private static final int MAX_CHARGE = 3000; // Capacité maximale de la batterie en millisecondes (3 secondes).
    private static final int CHARGING_TIME = 1000; // Temps nécessaire pour recharger complètement la batterie (2 secondes).
    private int currentCharge; // Charge actuelle de la batterie.
    private boolean isCharging; // Indique si la batterie est en cours de recharge.
    private long chargingStartTime; // Moment où la recharge a commencé.

    public Battery() {
        this.currentCharge = MAX_CHARGE;
        this.isCharging = false;
    }

    public boolean needsCharging() {
        return currentCharge <= 0;
    }

    public void startCharging() {
        isCharging = true;
        chargingStartTime = System.currentTimeMillis(); // Enregistre l'heure actuelle comme début de recharge.
    }

    // Met à jour le statut de la recharge. Si le temps de recharge est terminé, la batterie est rechargée.
    public boolean updateChargingStatus() {
        if (isCharging) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - chargingStartTime >= CHARGING_TIME) { // Vérifie si le temps de recharge est écoulé.
                currentCharge = MAX_CHARGE;
                isCharging = false;
                return true;
            }
        }
        return false;
    }

    public void consume(int amount) {
        currentCharge = Math.max(0, currentCharge - amount);
    }

    public boolean isCharging() {
        return isCharging;
    }
}