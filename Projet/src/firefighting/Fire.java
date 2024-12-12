package firefighting;

public class Fire {
    private int x, y; // Position de l'incendie

    public Fire(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isExtinguished() {
        return true;
    }

    @Override
    public String toString() {
        return "Fire at (" + x + ", " + y + ") ";
    }
}
