package com.rraptor.pult;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rraptor.pult.comm.DeviceConnection;
import com.rraptor.pult.comm.DeviceConnectionWifi;

public class ManualPultActivity extends Activity {
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
	private final Handler handler = new Handler();

	private final DeviceConnection deviceConnection = DeviceConnectionWifi
			.getInstance();

	private final OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				switch (v.getId()) {
				case R.id.x_forward_btn:
					deviceConnection.sendToDeviceBackground(
							ManualPultActivity.this, handler,
							DeviceConnection.CMD_X_FORWARD);
					break;
				case R.id.x_backward_btn:
					deviceConnection.sendToDeviceBackground(
							ManualPultActivity.this, handler,
							DeviceConnection.CMD_X_BACKWARD);
					break;
				case R.id.y_forward_btn:
					deviceConnection.sendToDeviceBackground(
							ManualPultActivity.this, handler,
							DeviceConnection.CMD_Y_FORWARD);
					break;
				case R.id.y_backward_btn:
					deviceConnection.sendToDeviceBackground(
							ManualPultActivity.this, handler,
							DeviceConnection.CMD_Y_BACKWARD);
					break;
				case R.id.z_forward_btn:
					deviceConnection.sendToDeviceBackground(
							ManualPultActivity.this, handler,
							DeviceConnection.CMD_Z_FORWARD);
					break;
				case R.id.z_backward_btn:
					deviceConnection.sendToDeviceBackground(
							ManualPultActivity.this, handler,
							DeviceConnection.CMD_Z_BACKWARD);
					break;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				deviceConnection.sendToDeviceBackground(
						ManualPultActivity.this, handler,
						DeviceConnection.CMD_STOP);
			}
			return false;
		}
	};

	/**
	 * http://www.youtube.com/watch?v=gGbYVvU0Z5s&feature=player_embedded
	 * http://developer.android.com/resources/articles/speech-input.html
	 */
	// TODO
	public void commandVoice() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		// Specify the calling package to identify your application
		// intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
		// getClass().getPackage().getName());

		// Display an hint to the user about what he should say.
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Произнесите команду");

		// Given an hint to the recognizer about what the user is going
		// to say
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

		// Specify how many results you want to receive. The results
		// will be sorted
		// where the first result is the one with higher confidence.
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

		// Specify the recognition language. This parameter has to be
		// specified only if the
		// recognition has to be done in a specific language and not the
		// default one (i.e., the
		// system locale). Most of the applications do not have to set
		// this parameter.
		// if (!mSupportedLanguageView.getSelectedItem().toString()
		// .equals("Default")) {
		// intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
		// mSupportedLanguageView.getSelectedItem().toString());
		// }

		try {
			startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		} catch (ActivityNotFoundException ex) {
			Toast.makeText(
					this,
					"Распознавание голоса не поддерживается на этом устройстве",
					Toast.LENGTH_LONG).show();
		}
	}

	private void handleVoiceCommand(String cmd) {
		System.out.println("Handle voice command: " + cmd);

		if (cmd.contains("вправо") || cmd.contains("право")
				|| cmd.contains("права")) {
			Toast.makeText(this, "Голосовая команда: " + "вправо",
					Toast.LENGTH_LONG).show();
			deviceConnection.sendToDeviceBackground(ManualPultActivity.this,
					handler, DeviceConnection.CMD_X_FORWARD);
		} else if (cmd.contains("влево") || cmd.contains("лево")
				|| cmd.contains("лего")) {
			Toast.makeText(this, "Голосовая команда: " + "влево",
					Toast.LENGTH_LONG).show();
			deviceConnection.sendToDeviceBackground(ManualPultActivity.this,
					handler, DeviceConnection.CMD_X_FORWARD);
		} else if (cmd.contains("вперед") || cmd.contains("перед")) {
			Toast.makeText(this, "Голосовая команда: " + "вперед",
					Toast.LENGTH_LONG).show();
			deviceConnection.sendToDeviceBackground(ManualPultActivity.this,
					handler, DeviceConnection.CMD_Y_FORWARD);
		} else if (cmd.contains("назад")) {
			Toast.makeText(this, "Голосовая команда: " + "назад",
					Toast.LENGTH_LONG).show();
			deviceConnection.sendToDeviceBackground(ManualPultActivity.this,
					handler, DeviceConnection.CMD_Y_FORWARD);
		} else if (cmd.contains("вверх") || cmd.contains("верх")) {
			Toast.makeText(this, "Голосовая команда: " + "вверх",
					Toast.LENGTH_LONG).show();
			deviceConnection.sendToDeviceBackground(ManualPultActivity.this,
					handler, DeviceConnection.CMD_Z_FORWARD);
		} else if (cmd.contains("вниз") || cmd.contains("низ")) {
			Toast.makeText(this, "Голосовая команда: " + "вниз",
					Toast.LENGTH_LONG).show();
			deviceConnection.sendToDeviceBackground(ManualPultActivity.this,
					handler, DeviceConnection.CMD_Z_BACKWARD);
		} else if (cmd.contains("стоп")) {
			Toast.makeText(this, "Голосовая команда: " + "стоп",
					Toast.LENGTH_LONG).show();
			deviceConnection.sendToDeviceBackground(ManualPultActivity.this,
					handler, DeviceConnection.CMD_STOP);
		} else {
			Toast.makeText(this, "Не понимаю: " + cmd, Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			final ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (matches.size() > 0) {
				handleVoiceCommand(matches.get(0));
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual_pult);

		final ImageButton btnXF = (ImageButton) findViewById(R.id.x_forward_btn);
		btnXF.setOnTouchListener(onTouchListener);
		final ImageButton btnXB = (ImageButton) findViewById(R.id.x_backward_btn);
		btnXB.setOnTouchListener(onTouchListener);
		final ImageButton btnYF = (ImageButton) findViewById(R.id.y_forward_btn);
		btnYF.setOnTouchListener(onTouchListener);
		final ImageButton btnYB = (ImageButton) findViewById(R.id.y_backward_btn);
		btnYB.setOnTouchListener(onTouchListener);
		final ImageButton btnZF = (ImageButton) findViewById(R.id.z_forward_btn);
		btnZF.setOnTouchListener(onTouchListener);
		final ImageButton btnZB = (ImageButton) findViewById(R.id.z_backward_btn);
		btnZB.setOnTouchListener(onTouchListener);

		final Button commandVoice = (Button) findViewById(R.id.command_voice_btn);
		commandVoice.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				commandVoice();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rraptor_pult, menu);
		return true;
	}
}
