package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Logging;

// Must change logging

public class ClimberSubsystem extends SubsystemBase {
   private boolean switchFlipped = false;
   private TalonFX leftClimberMotor;
   private TalonFX rightClimberMotor;

   public ClimberSubsystem(int leftClimberMotorID, int rightClimberMotorID) {

      leftClimberMotor = new TalonFX(leftClimberMotorID);
      rightClimberMotor = new TalonFX(rightClimberMotorID);
   }

   public void climberSwitchTriggered() {
      switchFlipped = true;
   }

   public void engageClimber() {
      leftClimberMotor.set(0.1);
      rightClimberMotor.set(-0.1);
   }

}