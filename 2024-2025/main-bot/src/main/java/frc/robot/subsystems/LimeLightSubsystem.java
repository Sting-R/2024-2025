package frc.robot.subsystems;

import static edu.wpi.first.units.Units.RotationsPerSecond;

import org.opencv.core.Mat;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.PoseEstimator;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CoralArmConstants;
import frc.robot.Constants.DriveTrainConstants;
import frc.robot.Constants.LimelightConstants;
import frc.robot.LimelightHelpers.RawFiducial;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.CompCommands;

public class LimeLightSubsystem extends SubsystemBase {

  /**
   * tx - Horizontal Offset ty - Vertical Offset ta - Area of target tv - Target
   * Visible
   */
  int FLIndex = 0;
  int FRIndex = 1;
  int BLIndex = 2;
  int BRIndex = 3;

  private String limelightName;

  private final SwerveDrivePoseEstimator m_poseEstimator;

  public LimeLightSubsystem(CommandSwerveDrivetrain swerveDriveTrain, String LimelightName) {

    m_poseEstimator = new SwerveDrivePoseEstimator(swerveDriveTrain.getKinematics(),
        swerveDriveTrain.getPigeon2().getRotation2d(),
        new SwerveModulePosition[] { swerveDriveTrain.getModule(0).getPosition(true),
            swerveDriveTrain.getModule(1).getPosition(true), swerveDriveTrain.getModule(2).getPosition(true),
            swerveDriveTrain.getModule(3).getPosition(true) },
        new Pose2d(), VecBuilder.fill(0.05, 0.05, Units.degreesToRadians(5)),
        VecBuilder.fill(0.5, 0.5, Units.degreesToRadians(30)));

    limelightName = LimelightName;
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
      LimelightHelpers.SetRobotOrientation(limelightName,
          m_poseEstimator.getEstimatedPosition().getRotation().getDegrees(), 0, 0, 0, 0, 0);

      // Same thing as above
      LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(limelightName);
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
    double kP = 35.75;

    // tx ranges from (-hfov/2) to (hfov/2) in degrees. If your target is on the
    // rightmost edge of your limelight 3 feed, tx should return roughly 31 degrees.
    double targetingAngularVelocity = LimelightHelpers.getCameraPose3d_TargetSpace(limelightName).getRotation().getY()
        * kP;

    // System.out.println(LimelightHelpers.getCameraPose3d_TargetSpace(limelightName).getRotation().getY());

    // targetingAngularVelocity *= DriveTrainConstants.MaxAngularRate;

    // convert to radians per second for our drive method
    double maxAngularRate = RotationsPerSecond.of(0.10).in(RadiansPerSecond);
    if (targetingAngularVelocity > maxAngularRate) {
      targetingAngularVelocity = maxAngularRate;
    } else if (targetingAngularVelocity < -maxAngularRate) {
      targetingAngularVelocity = -maxAngularRate;
    }

    // // invert since tx is positive when the target is to the right of the
    // crosshair
    // targetingAngularVelocity *= -1.0;

    System.out.println("limelight_aim_proportional " + targetingAngularVelocity);
    // System.out.println("lap " + targetingAngularVelocity);
    SmartDashboard.putNumber("Limelight_aim_porportional", targetingAngularVelocity);
    return targetingAngularVelocity;
  }

  /**
   * simple proportional ranging control with Limelight's "ty" value this works
   * best if your Limelight's mount height and target mount height are different.
   * if your limelight and target are mounted at the same or similar heights, use
   * "ta" (area) for target ranging rather than "ty"
   * 
   * Theoretically this should work for aiming intake
   * 
   */
  public double limelight_range_proportional() {
    double kP = .1;
    double targetingForwardSpeed = LimelightHelpers.getTY(limelightName) * kP;
    targetingForwardSpeed *= DriveTrainConstants.MaxSpeed;
    targetingForwardSpeed *= -1.0;
    // SmartDashboard.putNumber("Range_porportional", targetingForwardSpeed);
    return targetingForwardSpeed;
  }

  public double getDistanceToTarget() {
    double x = LimelightHelpers.getCameraPose3d_TargetSpace(limelightName).getX();
    double y = LimelightHelpers.getCameraPose3d_TargetSpace(limelightName).getZ();
    return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
  }

  /**
   * This method is used to get the area of the target from the limelight It will
   * be used to determine whether the distance from the target is too far or too
   * close
   */
  public double limelight_intake_forward_speed() {
    double kP = 0.1;
    double distanceFromTarget = getDistanceToTarget();
    double desiredForwardSpeed = (LimelightConstants.desiredIntakeDistance - distanceFromTarget) * kP;
    SmartDashboard.putNumber(limelightName, desiredForwardSpeed);
    return desiredForwardSpeed;
  }

  public double limelight_outtake_forward_speed(CoralArmLevels reefLvl) {
    double kP = 1.5;
    double distanceFromTarget = getDistanceToTarget();
    SmartDashboard.putNumber("Distance from apriltag", distanceFromTarget);
    double desiredForwardSpeed = 0;
    switch (reefLvl) {
    case lvl1:
      desiredForwardSpeed = (LimelightConstants.desiredOuttakeDistanceLvl1 - distanceFromTarget) * kP;
      break;
    case lvl2:
      desiredForwardSpeed = (LimelightConstants.desiredOuttakeDistanceLvl2 - distanceFromTarget) * kP;
      break;
    case lvl3:
      desiredForwardSpeed = (LimelightConstants.desiredOuttakeDistanceLvl3 - distanceFromTarget) * kP;
      break;
    case lvl4:
      desiredForwardSpeed = (LimelightConstants.desiredOuttakeDistanceLvl4 - distanceFromTarget) * kP;
      break;
    }
    SmartDashboard.putNumber("Desired forward speed", desiredForwardSpeed);
    return desiredForwardSpeed;
  }

  public double limelight_outtake_side_speed(CoralArmLevels coralArmLevel, Boolean isLeftReef) {
    double kP = 1.25;
    double yValueFromTarget = LimelightHelpers.getTY(limelightName);
    SmartDashboard.putNumber("y-offset from apriltag", yValueFromTarget);
    double desiredSideSpeed = 0;
    // Checks if it is going for the lvl 4 on the left side
    if (coralArmLevel == CoralArmLevels.lvl4 && (isLeftReef)) {
      desiredSideSpeed = (LimelightConstants.desiredLeftLLOutakeYOffsetLvl4 - yValueFromTarget) * kP;
    }
    // Checks if it is going for lvl 4 on the right side
    else if (coralArmLevel == CoralArmLevels.lvl4 && !isLeftReef) {
      desiredSideSpeed = (LimelightConstants.desiredRightLLOutakeYOffsetLvl4 - yValueFromTarget) * kP;
    }
    // Checks if it is going for lvl 1, 2, or 3 on the left side
    else if (((coralArmLevel == CoralArmLevels.lvl1) || (coralArmLevel == CoralArmLevels.lvl2)
        || (coralArmLevel == CoralArmLevels.lvl3)) && (isLeftReef)) {
      desiredSideSpeed = (LimelightConstants.desiredLeftLLOutakeYOffsetLvl123 - yValueFromTarget) * kP;
    }
    // Checks if it is going for lvl 1, 2, or 3 on the right side
    else if (((coralArmLevel == CoralArmLevels.lvl1) || (coralArmLevel == CoralArmLevels.lvl2)
        || (coralArmLevel == CoralArmLevels.lvl3)) && (!isLeftReef)) {
      desiredSideSpeed = (LimelightConstants.desiredRightLLOutakeYOffsetLvl123 - yValueFromTarget) * kP;
    }
    SmartDashboard.putNumber("Desired Side Speed", desiredSideSpeed);
    return desiredSideSpeed;
  }

  public boolean isRobotInDesiredReefPosition(CoralArmLevels reefLvl, boolean isLeftReef) {
    double distanceFromTarget = getDistanceToTarget();
    double xAngleFromTarget = LimelightHelpers.getTX(limelightName);
    double yOffsetFromTarget = LimelightHelpers.getTY(limelightName);
    double bufferValue = 0.2;
    if (isLeftReef) {
      switch (reefLvl) {
      case lvl1:
        return (Math.abs(LimelightConstants.desiredOuttakeDistanceLvl1 - distanceFromTarget) < bufferValue)
            && (Math.abs(LimelightConstants.desiredLeftLLOutakeYOffsetLvl123 - yOffsetFromTarget) < bufferValue);
      // (Math.abs(LimelightConstants.desiredLeftReefOffset - xAngleFromTarget) <
      // bufferValue);
      case lvl2:
        return (Math.abs(LimelightConstants.desiredOuttakeDistanceLvl2 - distanceFromTarget) < bufferValue)
            && (Math.abs(LimelightConstants.desiredLeftLLOutakeYOffsetLvl123 - yOffsetFromTarget) < bufferValue);
      // (Math.abs(LimelightConstants.desiredLeftReefOffset - xAngleFromTarget) <
      // bufferValue);
      case lvl3:
        return Math.abs(LimelightConstants.desiredOuttakeDistanceLvl3 - distanceFromTarget) < bufferValue
            && (Math.abs(LimelightConstants.desiredLeftLLOutakeYOffsetLvl123 - yOffsetFromTarget) < bufferValue);
      // Math.abs(LimelightConstants.desiredLeftReefOffset - xAngleFromTarget) <
      // bufferValue;
      case lvl4:
        return Math.abs(LimelightConstants.desiredOuttakeDistanceLvl4 - distanceFromTarget) < bufferValue
            && (Math.abs(LimelightConstants.desiredLeftLLOutakeYOffsetLvl4 - yOffsetFromTarget) < bufferValue);
      // Math.abs(LimelightConstants.desiredLeftReefOffset - xAngleFromTarget) <
      // bufferValue;
      default:
        return false;
      }
    } else {
      switch (reefLvl) {
      case lvl1:
        return (Math.abs(LimelightConstants.desiredOuttakeDistanceLvl1 - distanceFromTarget) < bufferValue)
            && (Math.abs(LimelightConstants.desiredRightLLOutakeYOffsetLvl123 - yOffsetFromTarget) < bufferValue);
      // (Math.abs(LimelightConstants.desiredLeftReefOffset - xAngleFromTarget) <
      // bufferValue);
      case lvl2:
        return (Math.abs(LimelightConstants.desiredOuttakeDistanceLvl2 - distanceFromTarget) < bufferValue)
            && (Math.abs(LimelightConstants.desiredRightLLOutakeYOffsetLvl123 - yOffsetFromTarget) < bufferValue);
      // (Math.abs(LimelightConstants.desiredLeftReefOffset - xAngleFromTarget) <
      // bufferValue);
      case lvl3:
        return Math.abs(LimelightConstants.desiredOuttakeDistanceLvl3 - distanceFromTarget) < bufferValue
            && (Math.abs(LimelightConstants.desiredRightLLOutakeYOffsetLvl123 - yOffsetFromTarget) < bufferValue);
      // Math.abs(LimelightConstants.desiredLeftReefOffset - xAngleFromTarget) <
      // bufferValue;
      case lvl4:
        return Math.abs(LimelightConstants.desiredOuttakeDistanceLvl4 - distanceFromTarget) < bufferValue
            && (Math.abs(LimelightConstants.desiredRightLLOutakeYOffsetLvl4 - yOffsetFromTarget) < bufferValue);
      // Math.abs(LimelightConstants.desiredLeftReefOffset - xAngleFromTarget) <
      // bufferValue;
      default:
        return false;
      }
    }

  }

  public boolean isRobotInDesiredIntakePosition() {
    double distanceFromTarget = getDistanceToTarget();
    return Math.abs(LimelightConstants.desiredIntakeDistance - distanceFromTarget) < 0.1;
  }

  public double getAprilTagInfo(String dataToReturn) {
    RawFiducial[] fiducials = LimelightHelpers.getRawFiducials(limelightName);
    double id = 0;
    double txnc = 0;
    double tync = 0;
    double ta = 0;
    double distToCamera = 0;
    double distToRobot = 0;
    double ambiguity = 0;

    // This is just using the last item in the list. If that is desired,
    // consider doing that more directly rather than looping
    for (RawFiducial thing : fiducials) {
      id = thing.id;
      txnc = thing.txnc;
      tync = thing.tync;
      ta = thing.ta;
      distToCamera = thing.distToCamera;
      distToRobot = thing.distToRobot;
      ambiguity = thing.ambiguity;

    }
    if (dataToReturn.equals("id")) {
      return id;
    } else if (dataToReturn.equals("tx")) {
      return txnc;
    } else if (dataToReturn.equals("ty")) {
      return tync;
    } else if (dataToReturn.equals("ta")) {
      return ta;
    } else if (dataToReturn.equals("distCamera")) {
      return distToCamera;
    } else if (dataToReturn.equals("distRobot")) {
      return distToRobot;
    } else if (dataToReturn.equals("amb")) {
      return ambiguity;
    } else {
      return 0;
    }
  }

  public void debuggingMethod() {
    SmartDashboard.putNumber("y-offset from apriltag", LimelightHelpers.getTY(limelightName));
    SmartDashboard.putNumber("Distance from apriltag", getDistanceToTarget());
  }

}
// This is line 281