package de.fintasys.the_15_puzzle_game.interfaces;


public interface IGameLogic {
    int[] onAction(int x, int y);

    int[] shuffle();

    void reset();

    boolean isSolved();

    int getNumTiles();

    int getTileSize();

    int getSides();

    int[] getTiles();

    void setTiles(int[] tiles);

    int getBlankPos();

    void setBlankPos(int blankPos);

    void setSize(int size);
}
