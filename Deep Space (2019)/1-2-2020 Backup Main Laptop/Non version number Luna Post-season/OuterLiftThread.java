/////////////////////////////////////////////////////////////////////
//  File:
/////////////////////////////////////////////////////////////////////
//
//  Purpose:
//
//  Programmer:
//
//  Environment:
//
//  Inception Date:
//
//  Revisions:
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

package frc.robot;

class OuterLift implements Runnable  {
    String name;
    int operation=0;
    Thread t;
    Runtime r = Runtime.getRuntime();
    private double speed = 0.4;
    private double deadband=10.0;
    private Delay delay;

    //  These are all guesses at this point
    //  These should be heights inches from the reference
    //  (startng point).  Some positions will be negative
    //  numbers.
    //  Declaring these as private allows use of the same names
    //  as in the inner lift thread.
    private final double PLATE_INTAKE=0.0;
    private final double BALL_INTAKE=0.0;
    //private final double PLATE_MOUNT_CS=0.0;  //  assumed same for all
    private final double PLATE_MOUNT_R1=-0.0;  //  assumed same for all
    private final double PLATE_MOUNT_R2=12.0;  //  assumed same for all
    private final double PLATE_MOUNT_R3=24.0;  //  assumed same for all

    private final double BALL_SHOOT_CARGO=0.0;
    private final double BALL_SHOOT_ROCK1=0.0;
    private final double BALL_SHOOT_ROCK2=12.0;
    //private final double BALL_SHOOT_ROCK3=24.0;

    private final double ENC_COUNTS_PER_INCH=722.95;
    private final double LOOP_DELAY=25.0;  //  delay in milliseconds
    private final double MIN_ENC_READ=0.0;  //  at reference
    private final double MAX_ENC_READ=17352.0;  //  assumes max travel of 24 inches
    private final int LOOP_EXIT=100;      //  Allows ?? seconds to complete the movement.

    //  Constructor
    OuterLift(String threadname)  {
        name=threadname;
        t=new Thread(this,name);
        System.out.println("New thread: " + t);
        delay=new Delay();
        t.start();  //  Start the thread        
    }


    //  Entry point for the thread.  This is where any desired
    //  action needs to be implemented.  It will run in "parallel"
    //  or "time share" with other active threads.  The prototype
    //  of this function is fixed.  It cannot be overidden with
    //  a version that passes in variables.  This makes it necessary
    //  to create static variables within Robot.java that can be
    //  seen within this function.
    public void run()  {

        double error=0;

    
        switch(Robot.outer_lift_op)  {
            case Robot.START_POS:  
                error=move2Position(0.0);
                break;
            case Robot.LOAD_PLATE:
                error=move2Position(PLATE_INTAKE);
                break;
            case Robot.LOAD_BALL:
                error=move2Position(BALL_INTAKE);
                break;
            case Robot.PLACE_PLATE_L1:
                error=move2Position(PLATE_MOUNT_R1);
                break;
            case Robot.PLACE_PLATE_L2:
                error=move2Position(PLATE_MOUNT_R2);
                break;
            case Robot.PLACE_PLATE_L3:
                error=move2Position(PLATE_MOUNT_R3);
                break;
            case Robot.SHOOT_BALL_CARGO:
                error=move2Position(BALL_SHOOT_CARGO);
                break;
            case Robot.SHOOT_BALL_RL1:
                error=move2Position(BALL_SHOOT_ROCK1);
                break;
            case Robot.SHOOT_BALL_RL2:
                error=move2Position(BALL_SHOOT_ROCK2);
                break;
        }
        
        System.out.println(name + "Exiting " + " Positioning Error = " + error);
        r.gc();  //  force garbage collection (freeing of memory resources)
        Robot.outer_lift_thread_active=false;

    }

    /////////////////////////////////////////////////////////////////
    //  Function:  private double move2Position(double positin) 
    /////////////////////////////////////////////////////////////////
    //
    //  Purpose:  Raises/lowers the outer lift to the specified position.
    //
    //  Arguments:Accepts a double representing the position in inches
    //            frpm the reference location (in this case the bottom)
    //
    //  Returns:  A double representing the difference in encoder 
    //            counts from the target and the achieved.
    //
    //  Remarks:
    //
     //  private member function.  Position is referenced to starting
    //   position determined at the begining of the game via RobotInit().
    //
    //   03/08/19:  Added stops at both ends.  If we go past the maximum
    //   allowed angle, the motor is stopped.  If we go past the starting
    //   point the motor is stopped.
    //
    //   Unknowns:  If we kill the thread and dump the memory do we
    //   have to re-reference to our zero?  In other words, does the
    //   encoder reset itself when the thread is re-created?  We can
    //   set the encoder to the last value but we do have some drift
    //   that may mess us up.
    //
    //   03/08/2019:  This problem is corrected by declaring the
    //   motor and encoder in Robot.java as static.  The encoder does not
    //   need to be reset when the thread is destroyed.  This is a
    //   method we will want to employ for the lift threads also.
    //
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////
    private double move2Position(double position)      {
        boolean debug=true;
        int count=0;  //  safety exit counter
        double enc_counts=0;
        double error=0.0;
        double delta=0.0;
        double current_position=0.0;
        double target_position=0.0;
 
        //  Get the current position.  
        //  Assumed:
        //  1.  If "garbage collection" has taken place we have lost
        //      all reference to the last time we were in this thread.
        //  2.  We determine the current position and then determine which way
        //      to drive the motor to the desired angle.
        current_position=Robot.left_motor.getSelectedSensorPosition(1);  //  This will be in encoder counts
        enc_counts=current_position;
        //  Target position with our present mechanical configuration will be
        //  a negative encoder value relative to our zero which is the intake
        //  in the full down starting position.
        target_position=-(position*ENC_COUNTS_PER_INCH);
     
        //  Determine the sign of the movement.  Here it is
        //  assumed that (counterclockwise) motion (lifting) of the
        //  intake will result in an increase in angle.  The
        //  variable delta merely determines sign, i.e., are we above
        //  where we want to be or below?  A negative value
        //  of delta indicates that we are lower than our target value,
        //  a positive value indicates we are above where we want to be.
        delta=target_position-current_position;
        if (debug == true) {
            System.out.println("target position = " + position);
            System.out.println("enc_counts: current position = " + enc_counts);
            System.out.println("target_position = " + target_position);
            System.out.println("delta = " + delta);
        }
    
        //  A pair of while loops with an escape after 2.5 seconds
        //  A deadband of '10' provides approx. +/- 1 degree acceptable
        //  error.  Depending on the sign of delta we rotate the motor
        //  forward or reverse.

        //  delta<0 implies that the lift is higher than the target.
        //  lowering the lift will result in lower values of the encoder.
        if(delta<0.0)  {
            Robot.outer_lift.set(-speed);  //  rotate to larger (negative) values
            while(enc_counts>(target_position+deadband) )  {
                enc_counts=Robot.left_motor.getSelectedSensorPosition(1);
                if (debug == true) {
                    System.out.println("enc_counts = " + enc_counts);
                }
                //  Don't allow movement past the max angle (90 degrees from reference)
                if(enc_counts<MIN_ENC_READ)  {  //  min encoder read should be zero for this lift
                    Robot.outer_lift.set(0);
                    if (debug == true) {
                        System.out.println("enc_counts = " + enc_counts + " Exceeded movement limit of = " + MIN_ENC_READ);
                    }
                    break;
                 }

                delay.delay_milliseconds(LOOP_DELAY);
                count++;
                if (count > LOOP_EXIT) {
                    if (debug == true) {
                        System.out.println("escape counts = " + count + " Timed Out");
                    }
                    break;
                }
            }
        //  delta>0 implies that we are lower than the target and
        //  we want to raise the lift.  We rotate the motor in
        //  a clockwise direction which results in increasing encoder
        //  values.
        } else if(delta>0.0)  {
            while(enc_counts<(target_position-deadband))  {
                enc_counts=Robot.left_motor.getSelectedSensorPosition(1);
                if (debug == true) {
                    System.out.println("enc_counts = " + enc_counts);
                }
                //  Don't allow movement past the reference (0 degrees)
                if(enc_counts>MAX_ENC_READ)  {
                    Robot.outer_lift.set(0);
                    if (debug == true) {
                        System.out.println("enc_counts = " + enc_counts + " Exceeded movement limit of = " + MAX_ENC_READ);
                    }
                    break;
                }
                Robot.outer_lift.set(speed);
                delay.delay_milliseconds(LOOP_DELAY);
                count++;
                if (count > LOOP_EXIT) {
                    if (debug == true) {
                        System.out.println("escape counts = " + count + " Timed Out");
                    }
                    break;
                }
            }
            Robot.outer_lift.set(0);
        }

        //  Take a final read on the encoder position and 
        //  assign it to the static variable declared in
        //  Robot.java.  
        Robot.outer_lift_position=Robot.left_motor.getSelectedSensorPosition(1);
        error=Robot.outer_lift_position-target_position;
        return(error);
    }
}
