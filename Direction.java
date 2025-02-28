package de.tum.cit.ase.bomberquest.map;

/**
 * Represents the cardinal directions in the game. Useful for bomb and enemy.
 * Each direction has predefined x and y offsets used for movement calculations.
 */
public enum Direction {
    // Enum constants with associated x and y offsets
    UP(0, 1),   // UP direction with no x offset and y offset of 1
    DOWN(0, -1),  // DOWN direction with no x offset and y offset of -1
    LEFT(-1, 0), // LEFT direction with x offset of -1 and no y offset
    RIGHT(1, 0); // RIGHT direction with x offset of 1 and no y offset

    // Private fields to hold the x and y offsets
    private final int offsetX;
    private final int offsetY;

    // Constructor to set the x and y offsets for each direction
    Direction(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    // Getter method to retrieve the x offset
    public int getOffsetX() {
        return offsetX;
    }
    // Getter method to retrieve the y offset
    public int getOffsetY() {
        return offsetY;
    }
}
