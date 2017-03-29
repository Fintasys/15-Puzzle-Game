package de.fintasys.the_15_puzzle_game.interfaces;

import java.util.List;

import io.reactivex.Observable;


public interface IImageModel {

    Observable<String> init();

    Observable<List<String>> getUserImages();
}
