package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Logging;
import frc.robot.Constants.ClimberConstants;

// Must change logging

public class ClimberSubsystem extends SubsystemBase {
   private boolean switchFlipped = false;
   private TalonFX leftClimberMotor;
   private TalonFX rightClimberMotor;

   public ClimberSubsystem() {
      leftClimberMotor = new TalonFX(ClimberConstants.kLeftClimberMotorID);
      rightClimberMotor = new TalonFX(ClimberConstants.kRightClimberMotorID);
   }

   public void climberSwitchTriggered() {
      switchFlipped = true;
   }

   public void engageClimber() {
      leftClimberMotor.set(0.1);
      rightClimberMotor.set(-0.1);
   }

}