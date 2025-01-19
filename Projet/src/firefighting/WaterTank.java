package firefighting;

/**
 * Classe représentant un réservoir d'eau utilisé pour déployer de l'eau
 * (par exemple, pour éteindre un incendie). 
 * Elle gère l'état du déploiement et la durée associée.
 */

public class WaterTank {
    private static final int DEPLOYMENT_TIME = 2000;     // Temps de déploiement en millisecondes (2 secondes)
    private boolean isDeploying;
    private long deploymentStartTime;

    public WaterTank() {
        this.isDeploying = false;
    }

    /**
    * Démarre le déploiement du réservoir.
    * Met à jour l'état et enregistre l'heure de début.
    */
    public void startDeployment() {
        isDeploying = true;
        deploymentStartTime = System.currentTimeMillis();
    }

    /**
    * Vérifie si le réservoir est en cours de déploiement.
    * @return Vrai si le déploiement est en cours, sinon faux.
    */
    public boolean isDeploying() {
        return isDeploying;
    }

    /**
    * Met à jour l'état du déploiement en fonction du temps écoulé.
    * Si le temps de déploiement est atteint, il met fin au déploiement.
    * @return Vrai si le déploiement est terminé, sinon faux.
    */
    public boolean updateDeploymentStatus() {
        if (isDeploying) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - deploymentStartTime >= DEPLOYMENT_TIME) {
                isDeploying = false;
                return true;
            }
        }
        return false;
    }
}
