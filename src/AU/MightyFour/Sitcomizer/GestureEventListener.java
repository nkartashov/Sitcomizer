package AU.MightyFour.Sitcomizer;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: nikita_kartashov
 * Date: 12/11/2013
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public class GestureEventListener implements SensorEventListener
{
	public GestureEventListener()
	{
		for (int i = 0; i < 3; i++)
			_previousAccel[i] = _currentAccel[i] = SensorManager.GRAVITY_EARTH;
	}

	public void addHandler(Integer event, Integer handler)
	{
		_handlers.put(event, handler);
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		updateAccelerationData(event);

		if (_firstUpdateHappened)
		{
			if (identifyUpwardGesture())
			{
				handleGesture(GestureTypes.WINNER_GESTURE);
				return;
			}
			if (identifyForwardGesture())
			{
				handleGesture(GestureTypes.SHAKE_GESTURE);
			}
		}
	}

	private void handleGesture(Integer event)
	{

		Integer handler = _handlers.get(event);
		if (handler != null)
			MusicPlayer.createAndLaunchPlayer(handler);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	private void updateAccelerationData(SensorEvent event)
	{
		if (!_firstUpdateHappened)
		{
			for (int i = 0; i < 3; i++)
			{
				_previousAccel[i] = event.values[i];
				_currentAccel[i] = event.values[i];
			}

			_firstUpdateHappened = true;
		}
		else
			for (int i = 0; i < 3; i++)
			{
				_previousAccel[i] = _currentAccel[i];
				_currentAccel[i] = event.values[i];
			}
	}

	private boolean identifyForwardGesture()
	{
		return  (Math.abs(_previousAccel[X] - _currentAccel[X]) > BORDER_SHAKE_ACCELERATION);
	}

	private boolean identifyUpwardGesture()
	{
		return (_previousAccel[Y] - _currentAccel[Y] > BORDER_WINNER_ACCELERATION);
	}

	private double _previousAccel[] = new double[3];
	private double _currentAccel[] = new double[3];

	private boolean _firstUpdateHappened = false;

	private final double BORDER_SHAKE_ACCELERATION = 11;
	private final double BORDER_WINNER_ACCELERATION = 11;

	private final String TAG = "ShakeEventListener";

	private final int X = 0;
	private final int Y = 1;
	private final int Z = 2;

	private HashMap<Integer, Integer> _handlers = new HashMap<Integer, Integer>();
}