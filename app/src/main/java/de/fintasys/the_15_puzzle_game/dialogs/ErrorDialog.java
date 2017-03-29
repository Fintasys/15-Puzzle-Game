package de.fintasys.the_15_puzzle_game.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import de.fintasys.the_15_puzzle_game.R;

public class ErrorDialog {

    AlertDialog.Builder builder;
    AlertDialog dialog;
    String mMessage;

    public ErrorDialog(Context context, String message) {
        mMessage = message;
        builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.dialog_title_error);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.okay, null);

        dialog = builder.create();
    }

    public void show() {
        if(dialog != null)
            dialog.show();
    }

    public void hide() {
        if(dialog != null)
            dialog.dismiss();
    }

}
