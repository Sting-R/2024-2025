package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.PoseEstimator;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveTrainConstants;
import frc.robot.LimelightHelpers;
// import edu.first.LimelightHelpers;
// import edu.first.wpilibj.

// bunch of random imports 

import edu.wpi.first.math.kinematics.SwerveDriveKinematics;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class LimeLightSubsystem extends SubsystemBase {

  /**
   * tx - Horizontal Offset ty - Vertical Offset ta - Area of target tv - Target
   * Visible
   */

  int FLIndex = 0;
  int FRIndex = 1;
  int BLIndex = 2;
  int BRIndex = 3;

  private final SwerveDrivePoseEstimator m_poseEstimator;

  public LimeLightSubsystem(CommandSwerveDrivetrain swerveDriveTrain) {

    m_poseEstimator = new SwerveDrivePoseEstimator(swerveDriveTrain.getKinematics(),
        swerveDriveTrain.getPigeon2().getRotation2d(),
        new SwerveModulePosition[] { swerveDriveTrain.getModule(0).getPosition(true),
            swerveDriveTrain.getModule(1).getPosition(true), swerveDriveTrain.getModule(2).getPosition(true),
            swerveDriveTrain.getModule(3).getPosition(true) },
        new Pose2d(), VecBuilder.fill(0.05, 0.05, Units.degreesToRadians(5)),
        VecBuilder.fill(0.5, 0.5, Units.degreesToRadians(30)));
  }

  public void updateOdometry(CommandSwerveDrivetrain swerveDriveTrain) {

    m_poseEstimator.update(swerveDriveTrain.getPigeon2().getRotation2d(),
        new SwerveModulePosition[] { swerveDriveTrain.getModule(0).getPosition(true),
            swerveDriveTrain.getModule(1).getPosition(true), swerveDriveTrain.getModule(2).getPosition(true),
            swerveDriveTrain.getModule(3).getPosition(true) });

    // Is this boolean ever changing at runtime? If not, it should be a final
    // variable
    boolean useMegaTag2 = true; // set to false to use MegaTag1
    boolean doRejectUpdate = false;
    if (useMegaTag2 == false) {
      // Use this line to know whether we are part of an alliance (we might not be if
      // we are not in a match presently)
      // DriverStation.getAlliance().isEmpty()

      // Use this line to know whether we are on the blue alliance
      // DriverStation.getAlliance().get().equals(Alliance.Blue);

      // This should change depending on the alliance we are on
      LimelightHelpers.PoseEstimate mt1 = LimelightHelpers.getBotPoseEstimate_wpiBlue("limelight");

      if (mt1.tagCount == 1 && mt1.rawFiducials.length == 1) {
        if (mt1.rawFiducials[0].ambiguity > .7) {
          doRejectUpdate = true;
        }
        if (mt1.rawFiducials[0].distToCamera > 3) {
          doRejectUpdate = true;
        }
      }
      if (mt1.tagCount == 0) {
        doRejectUpdate = true;
      }

      if (!doRejectUpdate) {
        m_poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.5, .5, 9999999));
        m_poseEstimator.addVisionMeasurement(mt1.pose, mt1.timestampSeconds);

        swerveDriveTrain.addVisionMeasurement(mt1.pose, mt1.timestampSeconds, VecBuilder.fill(.5, .5, 9999999));
      }
    } else if (useMegaTag2 == true) {
      LimelightHelpers.SetRobotOrientation("limelight",
          m_poseEstimator.getEstimatedPosition().getRotation().getDegrees(), 0, 0, 0, 0, 0);

      // Same thing as above
      LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("limelight");
      // NOTE! The .getAngularVelocityZWorld() update is different from orignal code.
      // The method .getRate() is depreicated and will be removed soom, so we have
      // switched to this method. However, it is CCW+ instead of CW+, and may not
      // return the same value
      if (Math.abs(swerveDriveTrain.getPigeon2().getAngularVelocityZWorld().getValueAsDouble()) > 720)
      // if our angular velocity is greater than 720 degrees per second, ignore vision
      // updates
      {
        doRejectUpdate = true;
      }
      if (mt2.tagCount == 0) {
        doRejectUpdate = true;
      }
      if (!doRejectUpdate) {
        m_poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.7, .7, 9999999));
        m_poseEstimator.addVisionMeasurement(mt2.pose, mt2.timestampSeconds);

        swerveDriveTrain.addVisionMeasurement(mt2.pose, mt2.timestampSeconds, VecBuilder.fill(.7, .7, 9999999));
      }
    }
  }

  public PoseEstimator getPoseEstimator() {
    return m_poseEstimator;
  }

  /**
   * simple proportional turning control with Limelight. "proportional control" is
   * a control algorithm in which the output is proportional to the error. in this
   * case, we are going to return an angular velocity that is proportional to the
   * "tx" value from the Limelight.
   */
  public double limelight_aim_proportional() {
    // kP (constant of proportionality)
    // this is a hand-tuned number that determines the aggressiveness of our
    // proportional control loop
    // if it is too high, the robot will oscillate around.
    // if it is too low, the robot will never reach its target
    // if the robot never turns in the correct direction, kP should be inverted.
    double kP = .035;

    // tx ranges from (-hfov/2) to (hfov/2) in degrees. If your target is on the
    // rightmost edge of your limelight 3 feed, tx should return roughly 31 degrees.
    double targetingAngularVelocity = LimelightHelpers.getTX("DriveCamera") * kP;

    // convert to radians per second for our drive method
    targetingAngularVelocity *= DriveTrainConstants.MaxAngularRate;

    // invert since tx is positive when the target is to the right of the crosshair
    targetingAngularVelocity *= -1.0;

    return targetingAngularVelocity;
  }

  /**
   * simple proportional ranging control with Limelight's "ty" value this works
   * best if your Limelight's mount height and target mount height are different.
   * if your limelight and target are mounted at the same or similar heights, use
   * "ta" (area) for target ranging rather than "ty"
   */
  public double limelight_range_proportional() {
    double kP = .1;
    double targetingForwardSpeed = LimelightHelpers.getTY("limelight") * kP;
    targetingForwardSpeed *= DriveTrainConstants.MaxSpeed;
    targetingForwardSpeed *= -1.0;
    return targetingForwardSpeed;
  }
}
