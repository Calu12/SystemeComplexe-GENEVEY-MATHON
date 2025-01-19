package firefighting;

public class Point {
    public final int x;
    public final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }
    
    // Méthode pour comparer deux objets Point. Deux points sont égaux si leurs x et y sont identiques.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return x == point.x && y == point.y;
    }
    
    // Méthode pour générer un code de hachage (hash code) unique pour un point.
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}