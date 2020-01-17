/////////////////////////////////////////////////////////////////////
//  File:  IntakeTiltThread.java
/////////////////////////////////////////////////////////////////////
//
//  Purpose:
//
//  Development History:
//
//      Inception:  03/04/2019
//
//  Remarks/Concerns:
//
//      It is intended to create threads multiple times during the
//      running of the robot.  Not sure how memory managment, 
//      specifically, Java "garbage collection" will handle this.
//      Created "global" variables in Robot.java to remember where
//      the tilt is from the last time, the idea being that on
//      exit from the thread, all locally declared class variables
//      will no longer exist.
//
//      Use is made of the Java Runtime class for memory management.
//      Each time this thread is created, on exit, the memory is
//      returned to the system.  It is unknown at this point in time
//      when the process of "garbage collection" is performed.  Use
//      of Runtime.gc() forces memory to be released on exit from the
//      thread.
//
//     03/08/2019:  It turns out that upward movement of the intake
//     results in negative values (counterclockwise rotation of the 
//     NEO motor) for the encoder.  To keep this straight we need to
//     use absolute movements relative to the initial zero reference
//     and base the quantity of movement on current position and
//     target encoder value.
//
//     03/08/2019:  Declared the tilt moter and encoder in Robot.java 
//     as static. This will allow the motor and encoder to "remember" 
//     when the thread is destroyed.  The encoder will always be 
//     representative of the true position.
//
//     03/13/2019:  Corrected sign of motor speed in move2Position(...).
//     Tilt motor is energized before entering the while() loop - inside
//     the while() loop the encoder is read and the escape counter is
//     incremented.  Eliminated the initialization of the tilt encoder
//     reading in this thread - moved to robotInit() per Brady's 
//     suggestion.
//     Discovered that the hard stop "MIN_ENC_READ" was specified as
//     a positive number - this would immediately kill movement as
//     soon as we tried to move from the reference value.
//
//     03/14/2019:  Added flag "tilt_thread_active" to prevent starting
//     another tilt thread while one is active.  Also prevents use of
//     tilt joystick when thread is active.
//
//
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

package frc.robot;

class IntakeTilt implements Runnable {
    String name;
    int operation = 0;
    Thread t;
    Runtime r = Runtime.getRuntime();
    private double speed = 0.4;
    private double deadband = 1.0;
    private Delay delay;

    final double PLATE_INTAKE = 60.0;
    final double BALL_INTAKE = 20.0;
    final double PLATE_MOUNT = 60.0;
    final double PLATE_MOUNT_L3 = 60.0;

    final double BALL_SHOOT_CARGO = 85.0;
    final double BALL_SHOOT_ROCK1 = 75.0;
    final double BALL_SHOOT_ROCK2 = 75.0;

    //  SparkMax encoder yields a '1.0' per revolutio
    //  With a gear reduction of 100:1 implies 0.278
    //  degrees per revolution of the output sprocket.
    //  The initial reference point (0 degrees) was the
    //  starting point at the beginning.  I don't know
    //  how consistent this could be.
    final double ENC_COUNTS_PER_DEGREE = 0.278;  
    final double LOOP_DELAY = 25.0; // delay in milliseconds
    final double MIN_ENC_READ = -40.0;
    final double MAX_ENC_READ = 0.0;
    final int LOOP_EXIT = 100; // Allows 2.5 seconds to complete the movement.

    // Constructor
    IntakeTilt(String threadname) {
        name = threadname;
        t = new Thread(this, name);
        System.out.println("New thread: " + t);
        delay = new Delay();
        t.start(); // Start the thread
    }

    // Entry point for the thread. This is where any desired
    // action needs to be implemented. It will run in "parallel"
    // or "time share" with other active threads. The prototype
    // of this function is fixed. It cannot be overidden with
    // a version that passes in variables. This makes it necessary
    // to create static variables within Robot.java that can be
    // seen within this function.
    public void run() {

        double error = 0;

        // Per Brady's recommendation we set the reference
        // value for the tilt encoder in RobotInit().

        switch (Robot.tilt_op) {
        case Robot.START_POS:
            error = move2Angle(0.0);
            break;
        case Robot.LOAD_PLATE:
            error = move2Angle(PLATE_INTAKE);
            break;
        case Robot.LOAD_BALL:
            error = move2Angle(BALL_INTAKE);
            break;
        case Robot.PLACE_PLATE_L1:
            error = move2Angle(PLATE_MOUNT);
            break;
        case Robot.PLACE_PLATE_L2:
            error = move2Angle(PLATE_MOUNT);
            break;
        case Robot.PLACE_PLATE_L3:
            error = move2Angle(PLATE_MOUNT_L3);
            break;
        case Robot.SHOOT_BALL_CARGO:
            error = move2Angle(BALL_SHOOT_CARGO);
            break;
        case Robot.SHOOT_BALL_RL1:
            error = move2Angle(BALL_SHOOT_ROCK1);
            break;
        case Robot.SHOOT_BALL_RL2:
            error = move2Angle(BALL_SHOOT_ROCK2);
            break;
        }

        System.out.println(name + "Exiting " + " Positioning Error = " + error);
        r.gc(); // force garbage collection (freeing of memory resources)
        Robot.tilt_thread_active = false;
    }

    /////////////////////////////////////////////////////////////////
    // Function: private double move2Angle(double angle)
    /////////////////////////////////////////////////////////////////
    //
    // Purpose: Rotates the intake to the specified angle.
    //
    // Arguments:Accepts a double representing the angle in degrees.
    //
    // Returns: A double representing the difference in encoder
    // counts from the target and the achieved.
    //
    // Remarks:
    //
    // private member function. Angle is referenced to starting
    // position determined at the begining of the game via RobotInit().
    //
    // 03/08/19: Added stops at both ends. If we go past the maximum
    // allowed angle, the motor is stopped. If we go past the starting
    // point the motor is stopped.
    //
    // Unknowns: If we kill the thread and dump the memory do we
    // have to re-reference to our zero? In other words, does the
    // encoder reset itself when the thread is re-created? We can
    // set the encoder to the last value but we do have some drift
    // that may mess us up.
    //
    // 03/08/2019: This problem is corrected by declaring the
    // motor and encoder in Robot.java as static. The encoder does not
    // need to be reset when the thread is destroyed. This is a
    // method we will want to employ for the lift threads also.
    //
    // 03/13/2019:
    // 1. Eliminated initialization of encoder postion. Now
    // done in RobotInit().
    // 2. Moved motor drive outside the while() loop. Will we
    // have a watchdog issue?
    // 3. Corrected direction of motor movement - hopefully
    // this will allow this thread to function properly.
    // 4. Added "debug" flag and lots of debugging outputs
    // so we can figure this out.
    //
    //
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////
    private double move2Angle(double angle) {
        boolean debug = true;
        int count = 0; // safety exit counter
        double enc_counts = 0;
        double error = 0.0;
        double delta = 0.0;
        double current_position = 0.0;
        double target_position = 0.0;

        // Get the current position.
        // Assumed:
        // 1. If "garbage collection" has taken place we have lost
        // all reference to the last time we were in this thread.
        // 2. We determine the current position and then determine which way
        // to drive the motor to the desired angle.

        // The current position should always be less than or equal to zero.
        current_position = Robot.tilt_enc.getPosition(); // This will be in encoder counts
        enc_counts = current_position;
        // Target position with our present mechanical configuration will be
        // a negative encoder value relative to our zero which is the intake
        // in the full down starting position.
        target_position = -(angle * ENC_COUNTS_PER_DEGREE);

        // Determine the sign of the movement. Here it is
        // assumed that (counterclockwise) motion (lifting) of the
        // intake will result in an increase in angle. The
        // variable delta merely determines sign, i.e., are we above
        // where we want to be or below? A negative value
        // of delta indicates that we are lower than our target value,
        // a positive value indicates we are above where we want to be.
        delta = target_position - current_position;

        if (debug == true) {
            System.out.println("target angle = " + angle);
            System.out.println("enc_counts: current position = " + enc_counts);
            System.out.println("target_position = " + target_position);
            System.out.println("delta = " + delta);
        }

        // A pair of while loops with an escape after 2.5 seconds
        // A deadband of '10' provides approx. +/- 1 degree acceptable
        // error. Depending on the sign of delta we rotate the motor
        // forward or reverse.

        // delta<0 implies that the lift tilt is lower than desired.
        // raising the tilt will result in lower values of the encoder
        // or larger (in magnitude) negative values. In this scenario
        // the motor needs to rotate clockwise moving the encoder readings
        // to more negative values. At some point the encoder counts will
        // be less than the target value.
        if (delta < 0.0) {
            // Start the motor - 3/13/19 moved outside the while() loop
            Robot.tilt_motor.set(-speed); // rotate to smaller (more negative) values
            while (enc_counts > (target_position + deadband)) {
                enc_counts = Robot.tilt_enc.getPosition();
                if (debug == true) {
                    System.out.println("enc_counts = " + enc_counts);
                }

                // Don't allow movement past the max angle (90 degrees from reference)
                if (enc_counts < MIN_ENC_READ) {
                    Robot.tilt_motor.set(0);
                    if (debug == true) {
                        System.out.println(
                                "enc_counts = " + enc_counts + " Exceeded movement limit of = " + MIN_ENC_READ);
                    }
                    break;
                }
                // This delay will affect only this thread and not the main thread.
                delay.delay_milliseconds(LOOP_DELAY);

                // Increment the escape counter
                count++;
                if (count > LOOP_EXIT) {
                    if (debug == true) {
                        System.out.println("escape counts = " + count + " Timed Out");
                    }
                    break;
                }
            } // End of while() loop for delta<0.0
            Robot.tilt_motor.set(0);

            // delta>0 implies that we are at a greater tilt angle and
            // we want to lower the intake. We rotate the motor in
            // a clockwise direction which results in increasing encoder
            // values - smaller magnitude negative numbers.
        } else if (delta > 0.0) {
            // Start the motor - 3/13/19 moved outside the while() loop
            Robot.tilt_motor.set(speed); // rotate to larger (less negative) values
            while (enc_counts < (target_position - deadband)) {
                enc_counts = Robot.tilt_enc.getPosition();
                if (debug == true) {
                    System.out.println("enc_counts = " + enc_counts);
                }
                // Don't allow movement past the reference (0 degrees)
                if (enc_counts > MAX_ENC_READ) {
                    Robot.tilt_motor.set(0);
                    if (debug == true) {
                        System.out.println("enc_counts = " + enc_counts
                                + " Exceeded movement boundary past reference = " + MAX_ENC_READ);
                    }
                    break;
                }
                // We want to move towards more positive (less negative) values
                // Robot.tilt_motor.set(-speed);
                delay.delay_milliseconds(LOOP_DELAY);
                count++;
                if (count > LOOP_EXIT) {
                    if (debug == true) {
                        System.out.println("escape counts = " + count + " Timed Out");
                    }
                    break;
                }
            }
            Robot.tilt_motor.set(0);
        }

        // Take a final read on the encoder position and
        // assign it to the static variable declared in
        // Robot.java.
        Robot.tilt_position = Robot.tilt_enc.getPosition();
        error = Robot.tilt_position - target_position;
        return (error);
    }
}
