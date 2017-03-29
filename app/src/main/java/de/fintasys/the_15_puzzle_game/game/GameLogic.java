package de.fintasys.the_15_puzzle_game.game;

import java.util.Random;

import de.fintasys.the_15_puzzle_game.interfaces.IGameLogic;

/**
 * Algorithm from https://rosettacode.org/wiki/15_Puzzle_Game#Java
 */

public class GameLogic implements IGameLogic {

    private final int numTiles = 15;
    private final int side = 4;

    private Random rand = new Random();
    private int[] tiles = new int[numTiles + 1];
    private int tileSize, blankPos, gridSize;

    public GameLogic() {
    }

    /**
     * Takes X, Y Coordinates to figure out what tile was pressed
     * Calculates new position of tiles
     * @param x
     * @param y
     * @return new list of tiles
     */
    @Override
    public int[] onAction(int x, int y) {
        if (x < 0 || x > gridSize || y < 0 || y > gridSize)
            return tiles;

        int c1 = x / tileSize;
        int r1 = y / tileSize;
        int c2 = blankPos % side;
        int r2 = blankPos / side;

        if ((c1 == c2 && Math.abs(r1 - r2) == 1)
                || (r1 == r2 && Math.abs(c1 - c2) == 1)) {

            int clickPos = r1 * side + c1;
            tiles[blankPos] = tiles[clickPos];
            tiles[clickPos] = 0;
            blankPos = clickPos;
        }

        return tiles;
    }

    /**
     * Mixes up the tiles randomly
     * @return new list of tiles
     */
    @Override
    public int[] shuffle() {
        do {
            reset();
            // don't include the blank space in the shuffle, leave it
            // in the home position
            int n = numTiles;
            while (n > 1) {
                int r = rand.nextInt(n--);
                int tmp = tiles[r];
                tiles[r] = tiles[n];
                tiles[n] = tmp;
            }
        } while (!isSolvable());
        return tiles;
    }

    /**
     * Resets the tiles
     */
    @Override
    public void reset() {
        for (int i = 0; i < tiles.length; i++)
            tiles[i] = (i + 1) % tiles.length;
        blankPos = numTiles;
    }

    /**
     * Check if current tiles are solvable
     * @return
     */
    private boolean isSolvable() {
        int countInversions = 0;
        for (int i = 0; i < numTiles; i++) {
            for (int j = 0; j < i; j++) {
                if (tiles[j] > tiles[i])
                    countInversions++;
            }
        }
        return countInversions % 2 == 0;
    }

    /**
     * Checks if the current tiles set is in correct order
     * @return boolean if game is solved or not
     */
    @Override
    public boolean isSolved() {
        boolean solved = true;
        for(int i = 0; i < numTiles; i++) {
            if(tiles[i] != (i+1)) {
                solved = false;
                break;
            }
        }
        return solved;
    }

    @Override
    public int getNumTiles() {
        return numTiles;
    }

    @Override
    public int getTileSize() {
        return tileSize;
    }

    @Override
    public int getSides() {
        return side;
    }

    @Override
    public int[] getTiles() {
        return tiles;
    }

    @Override
    public void setTiles(int[] tiles) {
        this.tiles = tiles;
        for(int i = 0; i < tiles.length; i++) {
            if(tiles[i] == 0) {
                setBlankPos(i);
                break;
            }
        }
    }

    @Override
    public int getBlankPos() {
        return blankPos;
    }

    @Override
    public void setBlankPos(int blankPos) {
        this.blankPos = blankPos;
    }

    @Override
    public void setSize(int size) {
        gridSize = size;
        tileSize = gridSize / side;
    }
}
