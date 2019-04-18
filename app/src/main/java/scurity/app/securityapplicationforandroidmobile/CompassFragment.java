package scurity.app.securityapplicationforandroidmobile;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Context.SENSOR_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompassFragment extends Fragment implements SensorEventListener {

    ImageView compass_img;
    TextView txt_compass;
    TextView degrees;
    int direction;
    //Variable of the sensor manager
    private SensorManager mSensorManager;
    //Variable of the sensor used
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    //To store the sensor information
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    //To see if we have or not the sensor
    //fisrt from accelerometer
    private boolean mLastAccelerometerSet = false;
    //From magnetometer
    private boolean mLastMagnetometerSet = false;
    public CompassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_compass, container, false);

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        //Hooking the image
        compass_img =  view.findViewById(R.id.img_compass);
        txt_compass =  view.findViewById(R.id.direction_txt);
        degrees = view.findViewById(R.id.derees);
        start();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            direction = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            direction = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        direction = Math.round(direction);
        compass_img.setRotation(-direction);

        String where = "NW";

        if (direction >= 350 || direction <= 10)
            where = "N";
        if (direction < 350 && direction > 280)
            where = "NW";
        if (direction <= 280 && direction > 260)
            where = "W";
        if (direction <= 260 && direction > 190)
            where = "SW";
        if (direction <= 190 && direction > 170)
            where = "S";
        if (direction <= 170 && direction > 100)
            where = "SE";
        if (direction <= 100 && direction > 80)
            where = "E";
        if (direction <= 80 && direction > 10)
            where = "NE";


        txt_compass.setText( where);
        degrees.setText(direction+" º");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void start(){
//Checking the sensors:gyro+magnetometer, and if not, check if we have the magnetometer and the accelerometer.
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null));

            else {
                //Sensor found
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                //Registering sensor
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{//If we have the rotation vector sensor
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            //Registering it
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

}
