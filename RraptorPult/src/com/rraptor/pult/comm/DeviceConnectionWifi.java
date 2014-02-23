package com.rraptor.pult.comm;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class DeviceConnectionWifi implements DeviceConnection {
	private static class SingletonHolder {
		private static DeviceConnectionWifi instance = new DeviceConnectionWifi();
	}

	public static final String ADDRESS_DEFAULT = "192.168.43.191";

	public static final int PORT_DEFAULT = 44300;

	public static DeviceConnectionWifi getInstance() {
		return SingletonHolder.instance;
	}

	private Socket socket;

	private OutputStreamWriter out;
	private InputStreamReader in;

	private DeviceConnectionWifi() {
	}

	@Override
	public void connectToDevice(String address, int port) throws Exception {
		socket = new Socket(address, port);
		out = new OutputStreamWriter(socket.getOutputStream());
		in = new InputStreamReader(socket.getInputStream());
	}

	synchronized public void sendToDevice(final Context context,
			final Handler handler, final String address, final int port,
			final String cmd) {
		boolean connected = false;
		if (out != null) {
			try {
				System.out.println(cmd);
				writeToDevice(cmd);
				connected = true;
			} catch (IOException e) {
				System.out.println("Failed connection, try reconnect.");
			}
		}
		if (!connected) {
			try {
				connectToDevice(address, port);
				writeToDevice(cmd);
			} catch (Exception e) {
				e.printStackTrace();
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "Failed connection.",
								Toast.LENGTH_LONG).show();
					}
				});
			}
		}
	}

	@Override
	public void sendToDeviceBackground(final Context context,
			final Handler handler, final String cmd) {
		sendToDeviceBackground(context, handler, ADDRESS_DEFAULT, PORT_DEFAULT,
				cmd);
	}

	public void sendToDeviceBackground(final Context context,
			final Handler handler, final String address, final int port,
			final String cmd) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				sendToDevice(context, handler, address, port, cmd);
			}
		}).start();
	}

	public int sendToDeviceBlocked(final String address, final int port,
			String cmd, UnblockHook unblockHook) throws Exception {
		int returnCode = -1;
		if (out != null) {
			try {
				System.out.println(cmd);
				returnCode = writeToDeviceBlocked(cmd, unblockHook);
			} catch (IOException e) {
				System.out.println("Failed connection, try reconnect.");
				connectToDevice(address, port);
				returnCode = writeToDeviceBlocked(cmd, unblockHook);
			}
		}
		return returnCode;
	}

	@Override
	public int sendToDeviceBlocked(String cmd, UnblockHook unblockHook)
			throws Exception {
		return sendToDeviceBlocked(ADDRESS_DEFAULT, PORT_DEFAULT, cmd,
				unblockHook);
	}

	@Override
	synchronized public void writeToDevice(String cmd) throws IOException {
		out.write(cmd);
		out.flush();
	}

	@Override
	synchronized public int writeToDeviceBlocked(String cmd,
			final UnblockHook unblockHook) throws IOException {
		out.write(cmd);
		out.flush();

		// wait for reply
		while (!in.ready()
				&& (unblockHook != null ? unblockHook.isWaitingBlock() : true)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		int replyStatus;
		if (in.ready()) {
			replyStatus = in.read();
			System.out.println("CMD status: " + replyStatus);
		} else {
			throw new IOException("Interrupted");
		}
		return replyStatus;
	}
}
