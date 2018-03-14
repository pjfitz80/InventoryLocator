package com.example.patrick.myinventorylocator.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.patrick.myinventorylocator.R;
import com.example.patrick.myinventorylocator.activity.MapsActivity;
import com.example.patrick.myinventorylocator.utility.MyDialogs;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BarcodeScanFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class BarcodeScanFragment extends Fragment {

    private static final int CAMERA_PERMISSION_CAMERA = 0x000001;

    private View rootView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private Button barcodeValue;
    private Vibrator myVib;
    private OnFragmentInteractionListener mListener;
    private MyDialogs myDialogs;
    private MediaPlayer mp;

    public BarcodeScanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_barcode_scan, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        myDialogs = new MyDialogs(getActivity());

        myVib = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);

        barcodeValue = (Button) rootView.findViewById(R.id.barcode_value); // Displays the value of a successful barcode scan.
        barcodeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity() , MapsActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });

        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.ITF) // Format can be set to match whatever type of barcode you plan to scan.
                .build();

        cameraView = (SurfaceView) rootView.findViewById(R.id.surface_view); // The camera preview.

        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                startCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                Log.d("Barcode Detected!!!", "Barcode Detected!!!");
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    Log.d("Barcode != 0 detected: ", "Barcode != 0 detected: ");

                    barcodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                            cameraSource.stop();
                            Log.d("CameraSource Stopped", "CameraSource Stopped");
                            validateBarcode(barcodes);
                        }
                    });
                }
            }
        });
        return rootView;
    }

    /**
     * This method starts the camera source after first checking that permissions have been granted.
     */
    private void startCamera() {
        try {
            // Request camera permissions if not already granted.
            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CAMERA);
                return;
            }
            cameraSource.start(cameraView.getHolder()); // Start the camera using the Surfaceview as a preview.
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method makes sure the barcode scanned is of the proper length and format for use throughout the app.
     * @param theSparseArray The array that holds barcodes successfully scanned using the camera resource.
     */
    private void validateBarcode(SparseArray<Barcode> theSparseArray) {
        Log.d("validate() start", "validate() start");

        // Create barcode as int from scanned String barcode.
        int barcodeAsInt = Integer.parseInt(theSparseArray.valueAt(0).displayValue);
        Log.d("Detected length= ", String.valueOf(String.valueOf(barcodeAsInt).length()));
        String barcodeAsString = String.valueOf(barcodeAsInt);

        // Ensure the full barcode was properly scanned producing a String of length 9.
        if(barcodeAsString.length() == 9) {
            Log.d("Barcode length = 9 ","Barcode length = 9 ");

            mp = MediaPlayer.create(getContext(), R.raw.mario_coin);
            mp.start();

            /* The scanned barcode is 9 digits, but we do not need the last digit for our purposes.
               This easily and effectively removes the last digit. */
            barcodeAsInt = barcodeAsInt / 10;

            barcodeAsString = String.valueOf(barcodeAsInt);
            mListener.onFragmentInteraction(barcodeAsString); // Send our valid String back to the Map Activity.
            barcodeValue.setText(barcodeAsString);

            startCamera();
        }
        else {
            Log.d("Barcode length != 9", "Barcode length != 9");
            for(int i = 0; i < 2; i++){ myVib.vibrate(20);};

            myDialogs.incompleteStockNumberDialog(); // Handle user decision upon improperly scanned barcode.
            startCamera();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        cameraSource.release();
        barcodeDetector.release();
    }
    
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String theStockNumber);
    }
}
