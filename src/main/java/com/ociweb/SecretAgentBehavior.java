package com.ociweb;

import com.ociweb.gl.api.ShutdownListener;
import com.ociweb.gl.api.StartupListener;
import com.ociweb.iot.maker.DigitalListener;
import com.ociweb.iot.maker.FogCommandChannel;
import com.ociweb.iot.maker.FogRuntime;
import com.ociweb.iot.maker.Port;
import com.ociweb.iot.grove.lcd_rgb.Grove_LCD_RGB;
import static com.ociweb.iot.maker.FogCommandChannel.I2C_WRITER;

public class SecretAgentBehavior implements DigitalListener, ShutdownListener, StartupListener 
{

	private final FogCommandChannel buzzerChannel;
	private final FogCommandChannel RedLEDChannel;
	private final FogCommandChannel GreenLEDChannel;
	private final FogCommandChannel LCDChannel;
	private final Port buzzerPort;
	private final Port buttonPort;
	private final Port GreenLEDPort;
	private final Port RedLEDPort;
	private final Port motionPort;

	private long startTime;
	private long stopTime;
	private boolean gameOn = false;

	private final int off = 0;

	public SecretAgentBehavior(FogRuntime runtime, Port buzzerPort, Port GreenLEDPort, Port RedLEDPort, Port motionPort,
			Port buttonPort) {
		this.buzzerChannel = runtime.newCommandChannel(FogRuntime.PIN_WRITER);
		this.RedLEDChannel = runtime.newCommandChannel(FogRuntime.PIN_WRITER);
		this.GreenLEDChannel = runtime.newCommandChannel(FogRuntime.PIN_WRITER);
		this.buzzerPort = buzzerPort;
		this.GreenLEDPort = GreenLEDPort;
		this.RedLEDPort = RedLEDPort;
		this.motionPort = motionPort;
		this.buttonPort = buttonPort;
		LCDChannel = runtime.newCommandChannel(I2C_WRITER | FogRuntime.PIN_WRITER);
	}

	@Override
	public void digitalEvent(Port port, long time, long duration, int value) {
		if (port == motionPort) {
			Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "Detected motion!\nGame over", 255, 0, 0);
			gameOn = false;
			RedLEDChannel.setValue(RedLEDPort, value == 1);
			buzzerChannel.setValueAndBlock(buzzerPort, value == 1, 1000);
			GreenLEDChannel.setValue(GreenLEDPort, value != 1);
		}
		if (port == buttonPort && value == 1) {
			if (gameOn) {
				stopTime = System.currentTimeMillis();
				if (stopTime - startTime > 10000) {
					RedLEDChannel.setValue(RedLEDPort, value == 1);
					buzzerChannel.setValueAndBlock(buzzerPort, value == 1, 1000);
					GreenLEDChannel.setValue(GreenLEDPort, value != 1);
					Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "Too slow!\nYou lose", 255, 0, 0);
					gameOn = false;
				} else {
					Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "You win!\nNice", 0, 255, 0);
					gameOn = false;
				}
			} else {
				gameOn = true;
				Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "Game started!\nPush the button", 0, 191, 255);
				startTime = System.currentTimeMillis();
			}
			RedLEDChannel.setValue(RedLEDPort, value != 1);
			buzzerChannel.setValue(buzzerPort, value != 1);
			GreenLEDChannel.setValue(GreenLEDPort, value == 1);
		}
	}

	@Override
	public void startup() {
		Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "Secret Agent\nGame", 255, 215, 0);
	}

	public boolean acceptShutdown() {
		buzzerChannel.setValue(buzzerPort, off);
		GreenLEDChannel.setValue(GreenLEDPort, off);
		RedLEDChannel.setValue(RedLEDPort, off);
		return true;
	}

}
