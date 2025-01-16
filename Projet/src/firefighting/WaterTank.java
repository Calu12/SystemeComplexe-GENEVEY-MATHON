package firefighting;

public class WaterTank {
    private static final int DEPLOYMENT_TIME = 2000; // 2 seconds in milliseconds
    private boolean isDeploying;
    private long deploymentStartTime;

    public WaterTank() {
        this.isDeploying = false;
    }

    public void startDeployment() {
        isDeploying = true;
        deploymentStartTime = System.currentTimeMillis();
    }

    public boolean isDeploying() {
        return isDeploying;
    }

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
