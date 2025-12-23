package frc.components;

import com.kauailabs.navx.frc.AHRS;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.util.PIDConstants;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.CommonData;
import frc.robot.Settings;
import frc.robot.Utility;

// NOTE: All things commented out are not used. (At least not currently)

/**
 *
 */
public class SwerveDrive implements IComponent {

  /**
   * Overall Equation:
   *
   * Motor roations per second:
   * Motor rotations per minute / 60
   *
   * Wheel rotations per second:
   * Motor rotations per second / gear ratio
   *
   * Wheel radius (in ft):
   * Wheel diameter (in ft) / 2
   * or
   * Wheel radius (in inches) / 12
   *
   * Wheel circumference (in ft):
   * Wheel radius (in ft) * 2 * pi
   *
   * Speed (in ft/s):
   * Wheel rotations per second * wheel circumference (in ft)
   *
   * Total Equation:
   * (Motor rotations per minute / 60) / gear ratio * wheel circumference
   * (in ft)
   */

  /**
   * The maximum rotation of the robot.
   */
  private final static double MAX_ROTATION =
      (SwerveWheel.MAX_SPEED * 2 * Math.PI) / 2.7389195456 / 2;

  // The 2.7389195456 value still needs to be documented

  /**
   * The offset of the yaw.
   */
  double yawOffset = 0;

  /**
   * The constructor for the SwerveDrive class.
   * @param settings  The settings for the swerve drive.
   */
  public SwerveDrive(final Settings.SwerveDrive settings) {
    swerveWheels = new SwerveWheel[] {new SwerveWheel(settings.frontLeft),
                                      new SwerveWheel(settings.frontRight),
                                      new SwerveWheel(settings.backLeft),
                                      new SwerveWheel(settings.backRight)};

    kinematics = new SwerveDriveKinematics(
        swerveWheels[0].getSwervePos(), swerveWheels[1].getSwervePos(),
        swerveWheels[2].getSwervePos(), swerveWheels[3].getSwervePos());

    var path = PathPlannerPath.fromPathFile("newpath");
    traj = path.getTrajectory(new ChassisSpeeds(0, 0, 0), new Rotation2d(0));

    odometry = new SwerveDrivePoseEstimator(
        kinematics, Rotation2d.fromDegrees(360.0 - ahrs.getYaw()),
        new SwerveModulePosition[] {
            swerveWheels[0].getOdometryPosition(),
            swerveWheels[1].getOdometryPosition(),
            swerveWheels[2].getOdometryPosition(),
            swerveWheels[3].getOdometryPosition(),
        },
        new Pose2d(0, 0, new Rotation2d(0)));

    // limelightPoses[0] = new LimelightPose("limelight-back");
    // limelightPoses[1] = new LimelightPose("limelight-right");
    // limelightPoses[2] = new LimelightPose("limelight-left");

    controller = new PPHolonomicDriveController(
        new PIDConstants(0.5, 0, 0), new PIDConstants(0.05, 0, 0),
        SwerveWheel.MAX_SPEED, 0.42720018726587655839358241631199);
  }

  SwerveWheel[] swerveWheels;

  double leftOrRight;
  double forwardOrBack; // This is the forward or back speed

  PathPlannerTrajectory traj;
  PPHolonomicDriveController controller;

  private AHRS ahrs = new AHRS(SPI.Port.kMXP);

  Timer timer;

  private SwerveDriveKinematics kinematics;
  private SwerveDrivePoseEstimator odometry;

  // LimelightPose[] limelightPoses = new LimelightPose[3];

  Pose2d getCurrentPose() { return odometry.getEstimatedPosition(); }

  public void update() {

    // Put the wheels in their "X" position
    // TODO: Add the common data value and finish implementing the code
    // if (CommonData.getDefense) {
    // }

    // Calabrate The NavX
    if (CommonData.getCalibrate()) {
      CommonData.setCalibrate(false);
      // Reset the NavX.
      ahrs.reset();
      // Reset the yaw offset after calibration.
      yawOffset = ahrs.getYaw();
    } else {
      odometry.update(Rotation2d.fromDegrees(360.0 - ahrs.getYaw()),
                      new SwerveModulePosition[] {
                          swerveWheels[0].getOdometryPosition(),
                          swerveWheels[1].getOdometryPosition(),
                          swerveWheels[2].getOdometryPosition(),
                          swerveWheels[3].getOdometryPosition(),
                      });
    }

    SmartDashboard.putNumber("Yaw offset", yawOffset);

    // Optional<LimelightPose> maybeClosest =
    //     Stream.of(limelightPoses)
    //         .filter(x -> x.validPose())
    //         .min((left, right) -> {
    //           final var whereWeAre =
    //               odometry.getEstimatedPosition().getTranslation();
    //           final var leftDistance =
    //               left.getPose().getTranslation().getDistance(whereWeAre);
    //           final var rightDistance =
    //               right.getPose().getTranslation().getDistance(whereWeAre);
    //           return leftDistance < rightDistance ? -1
    //           : leftDistance > rightDistance      ? 1
    //                                               : 0;
    //         });

    // if (maybeClosest.isPresent()) {
    //   odometry.addVisionMeasurement(maybeClosest.get().getPose(),
    //                                 Timer.getFPGATimestamp());
    // }

    if (CommonData.getFollowPath()) {
      if (timer == null) {
        timer = new Timer();
        timer.start();
      }

      if (timer.get() >= traj.getTotalTimeSeconds()) {
        // CommonData.setFollowPath(false);
        // return;
      }

      var pose = getCurrentPose();
      var goal = traj.sample(timer.get());

      StringBuilder diagnosticString = new StringBuilder();

      diagnosticString.append("\n\nCurrent pose");
      diagnosticString.append("\nX: " + pose.getX());
      diagnosticString.append("\nY: " + pose.getY());
      diagnosticString.append("\nCurrent goal");
      diagnosticString.append("\nX: " + goal.positionMeters.getX());
      diagnosticString.append("\nY: " + goal.positionMeters.getY());

      var speeds =
          controller.calculateRobotRelativeSpeeds(getCurrentPose(), goal);

      diagnosticString.append("\nCalculated Speeds");
      diagnosticString.append("\nX/s: " + speeds.vxMetersPerSecond);
      diagnosticString.append("\nY/s: " + speeds.vyMetersPerSecond);
      diagnosticString.append("\nR/s: " +
                              (speeds.omegaRadiansPerSecond / (2 * Math.PI)));

      var states = kinematics.toSwerveModuleStates(speeds);

      diagnosticString.append("\nSwerve Wheel States");

      for (int i = 0; i < 4; ++i) {
        diagnosticString.append("\n[" + i + "] M/s" +
                                states[i].speedMetersPerSecond);
        diagnosticString.append("\n[" + i + "] Ang" +
                                states[i].angle.getRotations());

        swerveWheels[i].set(states[i]);
      }

      Utility.printLn(diagnosticString.toString());
    } else {
      timer = null;

      final var desiredTranslationSpeedX =
          CommonData.getForwardSpeed() *
          SwerveWheel.MAX_SPEED; // This code uses '+x' as toward opposing
                                 // alliance wall
      final var desiredTranslationSpeedY =
          -CommonData.getSideSpeed() *
          SwerveWheel.MAX_SPEED; // This code uses '+y' as toward driver's right
      final var desiredRotationSpeed =
          -CommonData.getDesiredTurn() * MAX_ROTATION;
      var moduleStates =
          kinematics.toSwerveModuleStates(ChassisSpeeds.fromFieldRelativeSpeeds(
              desiredTranslationSpeedX, desiredTranslationSpeedY,
              desiredRotationSpeed, Rotation2d.fromDegrees(-ahrs.getYaw())));

      for (int i = 0; i < 4; i++) {
        swerveWheels[i].set(moduleStates[i]);
      }
    }
  } // Man that's a lot of curly braces - Justin
} // Well here's some more for ya {{}}{{{}{{{{}}}}}} - Henry
// Do you need some more? Here you go! {{{{{{{{{{{{}}}}}}}}}}}} - Ben
// :o
// >:p
