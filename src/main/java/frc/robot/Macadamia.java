/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Encoder;
import frc.robot.Ultrasonic2537;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Macadamia extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private Talon leftTalon, rightTalon;
  private DifferentialDrive drive;
  private final int LEFT_TALON = 0;
  private final int RIGHT_TALON = 1;

  private Ultrasonic2537 frontUltrasonic, rearUltrasonic;
  private final int FRONT_PING = 8;
  private final int FRONT_ECHO = 9;
  private final int REAR_PING = 4;
  private final int REAR_ECHO = 5;
  private final double safeDistance = 15.0;

  private XboxController xbox;

  private Timer timer;

  private Encoder leftEnc, rightEnc;
  private final int L_ENCODER_A = 0;
  private final int L_ENCODER_B = 1;
  private final int R_ENCODER_A = 2;
  private final int R_ENCODER_B = 3;

  private double leftSpeed, rightSpeed; // speed to run motors
  private int stopLeft, stopRight;      // When robot reached its destination

  private final int X_BUTTON = 0;
  private final int Y_BUTTON = 1;
  private final int B_BUTTON = 2;
  private final int A_BUTTON = 3;
  private final int NOBUTTON = -1;
  private int buttonPressed = NOBUTTON;


/*public class MyThread extends Thread {

  public void run(){
    System.out.println("MyThread");
  }
    public void driveS(){
      driveEncoder(2000,2000, 0.47,0.5);
  }
    public void driveRight(){
    driveEncoder(666,-666, 0.5,-0.47);
  }
    public void driveBack(){
    driveEncoder(-2000,-2000, -0.47,-0.5);
  }
    public void driveLeft(){
    driveEncoder(-666,666, -0.47,0.5);
  }
} */

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    //Configure Drive
    leftTalon = new Talon(LEFT_TALON);
    rightTalon = new Talon(RIGHT_TALON);
    drive = new DifferentialDrive(leftTalon, rightTalon);

    //Configure Ultrasonic sensors on front and rear
      frontUltrasonic = new Ultrasonic2537(FRONT_PING, FRONT_ECHO); // ping, echo
      rearUltrasonic = new Ultrasonic2537(REAR_PING, REAR_ECHO); // ping, echo
      rearUltrasonic.setAutomaticMode(true);
      

    //Configure Joystick input
    xbox = new XboxController(0);

    //Configure Camera
    CameraServer.getInstance().startAutomaticCapture();

    //Instantiate timer
    timer = new Timer();

    leftEnc = new Encoder (L_ENCODER_A, L_ENCODER_B);
    rightEnc = new Encoder (R_ENCODER_A, R_ENCODER_B, true);
    leftEnc.reset();
    rightEnc.reset();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    timer.reset();
    timer.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    if (timer.get() < 2.0) {
        drive.curvatureDrive(0.1, 0.0, false);
      }
      else
      {
        drive.curvatureDrive(0.0, 0.0,false);
      }
 
 
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
public void teleopPeriodic() {
  // Use controller joysticks to set drive speed, but
  // safety stop if too close to an obstacle

  if (xbox.getYButtonPressed()) { // drive 2 feet forward
    // reset encoders so counts start at 0
    leftEnc.reset();
    rightEnc.reset();

    // set motor speed to go straight forward
    leftSpeed = 0.45;
    rightSpeed = 0.5;

    // set point we want to stop at
    stopLeft = 2000; stopRight = 2000;
  }
  else if (xbox.getBButtonPressed()) {  // turn 90 degrees to right
    // reset encoders so counts start at 0
    leftEnc.reset();
    rightEnc.reset();

    // set motor speed to go straight forward
    leftSpeed = 0.5;
    rightSpeed = -0.47;

    // set point we want to stop at
    stopLeft = 2000; stopRight = 2000;
  }
  else if (xbox.getAButtonPressed()) { // drive backward 2 feet
    // reset encoders so counts start at 0
    leftEnc.reset();
    rightEnc.reset();

    // set motor speed to go straight forward
    leftSpeed = - 0.5;
    rightSpeed = -0.47;

    // set point we want to stop at
    stopLeft = 2000; stopRight = 2000;
  } else if (xbox.getXButtonPressed()) {
    // reset encoders so counts start at 0
    leftEnc.reset();
    rightEnc.reset();

    // set motor speed to go straight forward
    rightSpeed = 0.5;
    leftSpeed = -0.47;

    // set point we want to stop at
    stopLeft = 2000; stopRight = 2000;
  }
  else if (stopLeft == 0 && stopRight == 0) { // drive only using joystick
    joystickDrive();
  } else {  // drive fixed distance
    driveEncoder(stopLeft, stopRight, leftSpeed, rightSpeed);
  }
}

public void joystickDrive() {
  leftSpeed  = -0.5*xbox.getY(Hand.kLeft);
  rightSpeed = -0.5*xbox.getY(Hand.kRight);

  if (safetyStop(safeDistance, frontUltrasonic) && (leftSpeed > 0.0) && (rightSpeed > 0.0)) {
      drive.stopMotor();
  } else if (safetyStop(safeDistance, rearUltrasonic) && (leftSpeed < 0.0) && (rightSpeed < 0.0)) {
      drive.stopMotor();
  } else {
    // otherwise, set motors according to joysticks
      drive.tankDrive(leftSpeed, rightSpeed);
  }
}


  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }


  // Use  ultrasonic sensor to stop robot
  // if it gets too close to an obstacle 
  public boolean safetyStop(double safeDistance, Ultrasonic2537 sensor) {

    if (sensor == null)  // no ultrasonic sensor working
      return false;

    double distance = sensor.getRangeInches();
    if (distance < safeDistance) 
        return true;
    else
        return false;
}


public void driveS(){
 driveEncoder(2000,2000, 0.47,0.5);
}
public void driveRight(){
  driveEncoder(666,-666, 0.5,-0.47);
}
public void driveBack(){
  driveEncoder(-2000,-2000, -0.47,-0.5);
}
public void driveLeft(){
  driveEncoder(-666,666, -0.47,0.5);
}

void driveEncoder(int leftDistance, int rightDistance, double leftSpeed, double rightSpeed){
  leftEnc.reset();
  rightEnc.reset();
  if ((Math.abs(leftEnc.getRaw()) < Math.abs(leftDistance)) ||
        (Math.abs(rightEnc.getRaw()) < Math.abs(rightDistance))){
          //System.out.println("left" + leftEnc.getRaw() + "right" + rightEnc.getRaw());
          if (Math.abs(leftEnc.getRaw()) >= Math.abs(leftDistance)){
            drive.tankDrive(0.0,rightSpeed * 0.2);
          }
          else if (Math.abs(rightEnc.getRaw()) >= Math.abs(rightDistance)){
            drive.tankDrive(leftSpeed * 0.2,0.0);
          }
          else {
            drive.tankDrive(leftSpeed,rightSpeed);
          } 
        }
  else {
    drive.tankDrive(0.0,0.0);
  }
}

};