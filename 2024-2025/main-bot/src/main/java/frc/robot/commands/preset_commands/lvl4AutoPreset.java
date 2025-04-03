package frc.robot.commands.preset_commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.CoralArmConstants;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CoralArmSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;

public class lvl4AutoPreset extends Command {
    private ElevatorSubsystem m_Elevator;
    private CoralArmSubsystem m_CoralArm;
    private CoralArmLevels desiredCoralLevel;
    private ElevatorPresets desiredElevatorLevel;
    private CommandXboxController operatorController;
    private boolean eject = false;
    private Timer outtakeTimer;
    private boolean timerStarted = false;
    private double lvl = 0;

    public lvl4AutoPreset(ElevatorSubsystem elevator, CoralArmSubsystem coralArm,
            CommandXboxController operatorController, boolean eject) {
        addRequirements(elevator, coralArm);
        // System.out.println("DOES THIS FRIGGING COMMAND WORK????");
        m_Elevator = elevator;
        m_CoralArm = coralArm;
        desiredCoralLevel = CoralArmLevels.lvl3; // Because lvl 3 and lvl 4 are very close, helps with checking position
        desiredElevatorLevel = ElevatorPresets.Level4;
        this.operatorController = operatorController;
        this.eject = eject;
    }

    public void initialize() {
        // System.out.println("Command Started");

    }

    public void execute() {

        m_Elevator.elevatorMoveToPresetMM(desiredElevatorLevel);
        // Basically checks if it is past the level 3 position, and if so, then raises
        // coral arm
        if (m_Elevator.getElevatorPosition() > ElevatorConstants.ElevatorPreset.level4EncoderValue - 4) {
            if (eject) {
                m_CoralArm.outtake(CoralArmConstants.kCoralEncoderOuttakelvl4Position - 2, eject);
                if (!m_CoralArm.coralGrabbed() && outtakeTimer == null) {
                    outtakeTimer = new Timer();
                    // System.out.println("Outtake Timer Started");
                    outtakeTimer.start();
                }
            } else {
                m_CoralArm.outtake(desiredCoralLevel, false);
            }
        }

    }

    @Override
    public boolean isFinished() {
        if (eject) {
            if (outtakeTimer == null) {
                return false;
            } else {
                // SmartDashboard.putNumber("Command Timer", outtakeTimer.get());
                return outtakeTimer.hasElapsed(1.5) && !m_CoralArm.coralGrabbed();
            }
        } else {
            return isInDesiredState(desiredElevatorLevel, desiredCoralLevel);
        }

    }

    public void end(boolean interrupted) {
        m_CoralArm.outtake(desiredCoralLevel, false);
        // System.out.println("Move to preset level finished: " + coralLevel);

        outtakeTimer = null;
        // m_Elevator.stop();
    }

    public boolean isInDesiredState(ElevatorPresets elevatorPreset, CoralArmLevels coralArmLevel) {
        return m_Elevator.isElevatorAtDesiredState(elevatorPreset).getAsBoolean()
                && m_CoralArm.isCoralArmAtDesiredState(coralArmLevel).getAsBoolean();

    }

}
