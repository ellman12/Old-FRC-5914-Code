//This class is responsible for lifting the cube

package org.usfirst.frc.team5914.robot;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;

public class LiftMechanism {

	Spark LeftMotor1, LeftMotor2, LeftMotor3, LeftMotor4;
	Spark RightMotor1, RightMotor2, RightMotor3, RightMotor4;

	SpeedControllerGroup Lift;

	public LiftMechanism() {

		LeftMotor1 = new Spark(0);
		LeftMotor2 = new Spark(1);
		LeftMotor3 = new Spark(2);
		LeftMotor4 = new Spark(3);

		RightMotor1 = new Spark(4);
		RightMotor2 = new Spark(5);
		RightMotor3 = new Spark(6);
		RightMotor4 = new Spark(7);

		RightMotor1.setInverted(true);
		RightMotor2.setInverted(true);
		RightMotor3.setInverted(true);
		RightMotor4.setInverted(true);

		Lift = new SpeedControllerGroup(LeftMotor1, LeftMotor2, LeftMotor3, LeftMotor4, RightMotor1, RightMotor2,
				RightMotor3, RightMotor4);

	}

	public void runLift() {

		if ((Robot.stick.getRawButton(4) == true) && (Robot.stick.getRawButton(2) == true)) {
			Lift.set(0);
		} else if (Robot.stick.getRawButton(4) == true) {
			Raise();
		} else if (Robot.stick.getRawButton(2) == true) {
			Lower();
		} else {
			Lift.set(0);
		}

	}

	public void Raise() {

		if (Robot.stick.getRawButton(4) == true) {

			Lift.set(-0.50);

		} else {
			Lift.set(0);
		}

	}

	public void Lower() {

		if (Robot.stick.getRawButton(2) == true) {

			Lift.set(0.3);

		} else {
			Lift.set(0);
		}

	}

	public void Switch() {

		Lift.set(-0.4);

		Timer.delay(1.1);

		Lift.set(0);

		Robot.drive_system.allDrive.set(.2);

		Timer.delay(1);

		Robot.intake_system.autoEject();

		Timer.delay(.5);

	}

	public void Scale() {

		Lift.set(-0.35);

		Timer.delay(3.05);

		Lift.set(0);

	}
}
