// package frc.robot.generated;

// import static edu.wpi.first.units.Units.MetersPerSecond;

// import com.pathplanner.lib.config.ModuleConfig;
// import com.pathplanner.lib.config.RobotConfig;
// import com.pathplanner.lib.controllers.PPHolonomicDriveController;
// import com.pathplanner.lib.controllers.PathFollowingController;
// import data.Length;
// import edu.wpi.first.math.controller.PIDController;
// import edu.wpi.first.math.controller.ProfiledPIDController;
// import edu.wpi.first.math.system.plant.DCMotor;
// import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
// import edu.wpi.first.math.util.Units;
// import pid.ScreamPIDConstants;
// import util.PPUtil;

// public final class DrivetrainConstants {
//         public static final double MAX_SPEED = TunerConstants.SPEED_12V_MPS.in(MetersPerSecond);
//         public static final double MAX_ANGULAR_SPEED_RADS = 8.0;
//         public static final double MAX_AZIMUTH_VEL_RADS = Units.rotationsToRadians(10);

//         public static final Length AT_POSE_DIST_THRESHOLD = Length.fromInches(2.0);

//         public static final int NUM_MODULES = 4;

//         public static final ScreamPIDConstants HEADING_CORRECTION_CONSTANTS = new ScreamPIDConstants(8.0, 0.0, 0.0);

//         public static final ProfiledPIDController HEADING_CONTROLLER_PROFILED = new ProfiledPIDController(7.0, 0, 0,
//                         new Constraints(7.0, Units.degreesToRadians(720.0 * 5)));

//         static {
//                 HEADING_CONTROLLER_PROFILED.enableContinuousInput(-Math.PI, Math.PI);
//         }

//         public static final ProfiledPIDController DRIVE_ALIGNMENT_CONTROLLER = new ProfiledPIDController(13.0, 0.0, 0.0,
//                         new Constraints(3.5, 4.0));

//         public static final PIDController HEADING_CONTROLLER = HEADING_CORRECTION_CONSTANTS.getPIDController(-Math.PI,
//                         Math.PI);

//         public static final ScreamPIDConstants PATH_TRANSLATION_CONSTANTS = new ScreamPIDConstants(10.0, 0.0, 0.0);
//         public static final ScreamPIDConstants PATH_ROTATION_CONSTANTS = new ScreamPIDConstants(7.0, 0.0, 0.0);

//         public static final ModuleConfig MODULE_CONFIG = new ModuleConfig(Units.inchesToMeters(2), MAX_SPEED, 1.4,
//                         DCMotor.getKrakenX60(1), 85, 1);

//         public static final RobotConfig ROBOT_CONFIG = new RobotConfig(Units.lbsToKilograms(150), 6.883, MODULE_CONFIG,
//                         TunerConstants.TRACK_WIDTH.getMeters());

//         public static final PathFollowingController PATH_FOLLOWING_CONTROLLER = new PPHolonomicDriveController(
//                         PPUtil.screamPIDConstantsToPPConstants(PATH_TRANSLATION_CONSTANTS),
//                         PPUtil.screamPIDConstantsToPPConstants(PATH_ROTATION_CONSTANTS));
// }
