package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;

public class Endgame {

    Joystick PS4;
    Solenoid backLift;
    boolean Touchpad;

    public Endgame() {

        PS4 = new Joystick(1);
        backLift = new Solenoid(2);
        Touchpad = false;
    }

    // public void TouchpadPressed() {
    //     if(PS4.getRawButton(14) == true && Touchpad == false) {
    //         Touchpad = true;
    //         Timer.delay(.2);
    //     }
    //     if(PS4.getRawButton(14) == true && Touchpad == true) {
    //         Touchpad = false;
    //         Timer.delay(.2);
    //     }
    // }

    public void Hab2() {

        if (PS4.getRawButton(14) == true) {
            backLift.set(true);
        }
        if (PS4.getRawButton(14) == false) {
            backLift.set(false);
        }
    }
}