package de.fintasys.the_15_puzzle_game.interfaces;

import android.content.Context;

import java.util.List;


public interface IMediaGalleryView {

    void showError(String message);

    void updateMedia(List<String> newMedia);

    Context getContext();
}
