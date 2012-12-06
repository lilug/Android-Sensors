/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lilug.android_sensors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class CompassActivity extends Activity {

	private class SampleView extends View {
		private final Paint mPaint = new Paint();
		private final Path mPath = new Path();

		public SampleView(final Context context) {
			super(context);

			// Construct a wedge-shaped path
			mPath.moveTo(0, -50);
			mPath.lineTo(-20, 60);
			mPath.lineTo(0, 50);
			mPath.lineTo(20, 60);
			mPath.close();
		}

		@Override
		protected void onDraw(final Canvas canvas) {
			final Paint paint = mPaint;

			canvas.drawColor(Color.WHITE);

			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.FILL);

			final int w = canvas.getWidth();
			final int h = canvas.getHeight();
			final int cx = w / 2;
			final int cy = h / 2;

			canvas.translate(cx, cy);
			if (mValues != null) {
				canvas.rotate(-mValues[0]);
			}
			canvas.drawPath(mPath, mPaint);
		}
	}

	private static final String TAG = "Compass";

	private final SensorEventListener mListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
		}

		@Override
		public void onSensorChanged(final SensorEvent event) {
			mValues = event.values;
			if (mView != null) {
				mView.invalidate();
			}
		}
	};

	private Sensor mSensor;
	private SensorManager mSensorManager;
	private float[] mValues;
	private SampleView mView;

	@Override
	protected void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mView = new SampleView(this);
		setContentView(mView);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mSensorManager.registerListener(mListener, mSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(mListener);
		super.onStop();
	}
}
