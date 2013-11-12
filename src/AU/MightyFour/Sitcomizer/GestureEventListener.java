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
public class GestureEventListener
{
	public GestureEventListener() {}

	public void addHandler(Integer event, Integer handler)
	{
		_handlers.put(event, handler);
	}

	public void setActive()
	{
		Log.v(TAG, "set to active");
		_isActive = true;
	}

	public void setInactive()
	{
		Log.v(TAG, "set to inactive");
		_isActive = false;
	}

	public SensorEventListener accelerationEventListener()
	{
		return _accelerationEventListener;
	}

	public SensorEventListener gyroscopeEventListener()
	{
		return _gyroscopeEventListener;
	}

	private final String TAG = "GestureEventListener";
	private HashMap<Integer, Integer> _handlers = new HashMap<Integer, Integer>();
	private boolean _isActive = false;

	private final int X = 0;
	private final int Y = 1;
	private final int Z = 2;

	private void handleGesture(Integer event)
	{
		if (!_isActive)
			return;
		Integer handler = _handlers.get(event);
		if (handler != null)
			MusicPlayer.createAndLaunchPlayer(handler);
	}

	private SensorEventListener _accelerationEventListener = new SensorEventListener()
	{
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

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		private void updateAccelerationData(SensorEvent event)
		{
			Log.v(TAG, "acceleration sensor value changed");
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
	};

	private SensorEventListener _gyroscopeEventListener = new SensorEventListener()
	{
		@Override
		public void onSensorChanged(SensorEvent event)
		{
			Log.v(TAG, "gyroscope sensor value changed");
			updateValues(event);
			if (identifyLeftTilt())
			{
				handleGesture(GestureTypes.TILT_LEFT_GESTURE);
				return;
			}
			if (identifyRightTilt())
			{
				handleGesture(GestureTypes.TILT_RIGHT_GESTURE);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int i)
		{
		}

		private void updateValues(SensorEvent event)
		{
			if (_firstRead)
			{
				_firstRead = false;
				yPrev = yCur = event.values[Y];
			}
			else
			{
				yPrev = yCur;
				yCur = event.values[Y];
			}
		}

		private boolean identifyLeftTilt()
		{
			return yPrev - yCur > BORDER_SPEED;
		}

		private boolean identifyRightTilt()
		{
			return yCur - yPrev > BORDER_SPEED;
		}


		private final double BORDER_SPEED = 4;

		private boolean _firstRead = true;
		private double yPrev = 0;
		private double yCur = 0;

		private final String TAG = "TiltEventListener";
	};

}