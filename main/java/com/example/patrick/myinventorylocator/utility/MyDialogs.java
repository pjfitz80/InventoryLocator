package com.example.patrick.myinventorylocator.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import com.example.patrick.myinventorylocator.R;
import com.example.patrick.myinventorylocator.activity.MapsActivity;

/**
 * A class that contains all the dialogs needed to
 * properly navigate the features of this app.
 *
 * @author  Patrick Fitzgerald
 * @version 1.0
 * @since   2018-02-23
 */
public class MyDialogs {

    private Context mContext;
    private AlertDialog.Builder alertDialogBuilder;
    private MediaPlayer mp; // To add a little fun to things.

    /**
     * The constructor.
     * @param theContext
     */
    public MyDialogs(Context theContext) {
        mContext = theContext;
        alertDialogBuilder = new AlertDialog.Builder(mContext);
    }

    /**
     * A dialog to handle the event of the barcode scanner
     * not capturing a complete barcode.
     */
    public void incompleteStockNumberDialog() {
        alertDialogBuilder.setTitle(R.string.incomplete_scan_dialog_title);
        alertDialogBuilder.setMessage(R.string.incomplete_scan_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.incomplete_scan_dialog_positive,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.incomplete_scan_dialog_negative,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        returnToMapActivity();
                    }
                });
        dialogSound();
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /** A dialog to handle the event of the user entering a stock number
     * that is not yet part of the inventory.
     */
    public void stockNumberNotFoundDialog() {
        alertDialogBuilder.setTitle(R.string.stock_number_not_found_dialog_title);
        alertDialogBuilder.setMessage(R.string.stock_number_not_found_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.stock_number_not_found_dialog_positive,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        mp.release();
                        dialog.cancel();
                    }
                });
        dialogSound();
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * A dialog for controlling the flow during vehicle location scanning.
     */
    public void scanVehicleLocationDialog() {
        alertDialogBuilder.setTitle(R.string.vehicle_location_scan_dialog_title);
        alertDialogBuilder.setMessage(R.string.vehicle_location_scan_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.vehicle_location_scan_dialog_positive,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.vehicle_location_scan_dialog_negative,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        returnToMapActivity();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /** A dialog for editing a Vehicles attributes. WIll make use of
     * the Vehicle object's getters/setters to fill in previously null fields.
     */
    public void vehicleEditorDialog() {

    }

    /**
     * This method plays a sound effect when a dialog is created and shown.
     */
    private void dialogSound() {
        mp = MediaPlayer.create(mContext, R.raw.mario_negative);
        mp.start();
    }

    /**
     *  A method that start the MapsActivity.
     */
    private void returnToMapActivity() {
        Intent mIntent = new Intent(mContext, MapsActivity.class);
        mContext.startActivity(mIntent);
    }
}
