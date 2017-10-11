package com.ociweb;

import static com.ociweb.iot.grove.simple_digital.SimpleDigitalTwig.Buzzer;
import static com.ociweb.iot.grove.simple_digital.SimpleDigitalTwig.LED;
import static com.ociweb.iot.grove.simple_digital.SimpleDigitalTwig.MotionSensor;
import static com.ociweb.iot.grove.simple_digital.SimpleDigitalTwig.Button;

import com.ociweb.iot.maker.*;
import static com.ociweb.iot.maker.Port.*;

public class IoTApp implements FogApp
{
    private static Port MOTION_PORT = D5;
    private static Port GREEN_LED_PORT = D6;
    private static Port RED_LED_PORT = D3;
    private static Port BUZZER_PORT = D4;
    private static Port BUTTON_PORT = D2;

    @Override
    public void declareConnections(Hardware hardware) {
        hardware.connect(Buzzer, BUZZER_PORT);
        hardware.connect(LED, GREEN_LED_PORT);
        hardware.connect(LED, RED_LED_PORT);
        hardware.connect(Button, BUTTON_PORT);
        hardware.connect(MotionSensor, MOTION_PORT);
        hardware.setTimerPulseRate(1000);
    }

    @Override
    public void declareBehavior(FogRuntime runtime) {
    	runtime.registerListener(new SecretAgentBehavior(runtime, BUZZER_PORT, GREEN_LED_PORT, RED_LED_PORT, MOTION_PORT, BUTTON_PORT));
    }
          
}
