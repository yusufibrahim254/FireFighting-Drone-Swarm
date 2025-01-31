package org.example.FireIncidentSubsystem.Helpers;

public class Coordinates {
    private int x;
    private int y;

    /**
     * The coordinates of the zone
     * @param x x coordinates
     * @param y y coordinates
     */
    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x coordinates
     * @return the x coordinates
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coordinates
     * @return the y coordinates
     */
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        if (x != that.x) return false;
        return y == that.y;
    }

}
