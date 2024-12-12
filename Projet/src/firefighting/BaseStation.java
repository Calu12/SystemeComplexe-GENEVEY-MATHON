package firefighting;

public class BaseStation {
    private int x, y; // Position de la base

    public BaseStation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "BaseStation at (" + x + ", " + y + ")";
    }
}
