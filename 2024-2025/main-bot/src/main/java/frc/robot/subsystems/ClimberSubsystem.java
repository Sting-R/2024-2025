package frc.robot.subsystems;

import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ClimberConstants;
import frc.robot.Constants.CoralArmConstants;

// Must change logging

public class ClimberSubsystem extends SubsystemBase {
   private boolean switchFlipped = false;
   private TalonFX climberMotor;
   private DigitalInput climberLimitSwitch;

   public static final double MOTION_MAGIC_ACCELERATION = 80;// 120
   public static final double MOTION_MAGIC_VELOCITY = 100;// 170
   private MotionMagicVoltage setVoltage;

   public ClimberSubsystem() {
      climberMotor = new TalonFX(ClimberConstants.kClimberMotorID);
      climberLimitSwitch = new DigitalInput(ClimberConstants.kClimberLimitSwitchID);

      configClimberMotor();
      setVoltage = new MotionMagicVoltage(0).withSlot(0);
   }

   private void configClimberMotor() {

      TalonFXConfiguration ClimberArmMotorConfig = new TalonFXConfiguration();
      // Coral Arm Going up
      ClimberArmMotorConfig.Slot0.kP = 3; // p pid //4.1
      ClimberArmMotorConfig.Slot0.kD = 0.15;// SmartDashboard.getNumber("d", 0.51); // d pid .5362, then .52
      ClimberArmMotorConfig.Slot0.kV = 0;
      ClimberArmMotorConfig.Slot0.kA = 0;
      ClimberArmMotorConfig.Slot0.kG = 0.45;

      ClimberArmMotorConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
      ClimberArmMotorConfig.MotorOutput.PeakForwardDutyCycle = 0.5;
      ClimberArmMotorConfig.MotorOutput.PeakReverseDutyCycle = -4; // can bump up to 12 or something
      ClimberArmMotorConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

      ClimberArmMotorConfig.CurrentLimits.StatorCurrentLimitEnable = true;
      ClimberArmMotorConfig.CurrentLimits.StatorCurrentLimit = 60;

      MotionMagicConfigs motionMagicConfigs = new MotionMagicConfigs();
      motionMagicConfigs.withMotionMagicAcceleration(MOTION_MAGIC_ACCELERATION)
            .withMotionMagicCruiseVelocity(MOTION_MAGIC_VELOCITY);

      climberMotor.getConfigurator().apply(ClimberArmMotorConfig);
      climberMotor.getConfigurator().apply(motionMagicConfigs);
   }

   public void climberSwitchTriggered() {
      switchFlipped = true;
   }

   double climberPosition = 0;

   public void setToActivePosition() {
      climberMotor.setControl(setVoltage.withPosition(ClimberConstants.kCoralEncoderActivePosition).withSlot(0));
   }

   public void climberForward() {
      climberMotor.set(0.1);
   }

   public void climberBackward() {
      climberMotor.set(-1);
   }

   public void climberStop() {
      climberMotor.set(0);
   }

   public void engageClimber() {
      if (climberLimitSwitch.get()) {
         if (climberPosition == 0) {
            climberPosition = climberMotor.getPosition().getValueAsDouble();
         } else {
            climberMotor.setControl(setVoltage.withPosition(CoralArmConstants.kCoralEncoderIntakePosition).withSlot(0));
         }
      } else {
         climberMotor.set(0.1);
      }
      // climberMotor.set
      // Control(setVoltage.withPosition(CoralArmConstants.kCoralEncoderIntakePosition).withSlot(0));
   }

   public void debugMethod() {
      SmartDashboard.putBoolean("Climber.IsSwitchedFlipped?", switchFlipped);
      SmartDashboard.putData("putData is cool. Have a ClimberMotor", climberMotor);
   }

}