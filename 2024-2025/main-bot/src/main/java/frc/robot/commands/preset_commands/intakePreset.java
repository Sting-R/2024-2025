package frc.robot.commands.preset_commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.CoralArmSubsystem;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;

public class intakePreset extends Command {
    ElevatorSubsystem m_Elevator;
    CoralArmSubsystem m_CoralArm;
    CoralArmLevels coralLevel;
    ElevatorPresets elevatorLevel;
    CommandXboxController driveController;
    CommandXboxController operatorController;
    Timer outtakeTimer;
    boolean timerStarted = false;
    boolean isAuto;

    public intakePreset(ElevatorSubsystem elevator, CoralArmSubsystem coralArm, CommandXboxController driveController,
            CommandXboxController operatorController, boolean isAuto) {
        addRequirements(elevator, coralArm);
        m_Elevator = elevator;
        m_CoralArm = coralArm;
        this.driveController = driveController;
        this.operatorController = operatorController;
        coralLevel = CoralArmLevels.intake;
        elevatorLevel = ElevatorPresets.intake;
        this.isAuto = isAuto;
    }

    public void execute() {
        m_Elevator.elevatorMoveToPresetMM(elevatorLevel);
        m_CoralArm.intake(operatorController);
    }

    @Override
    public boolean isFinished() {
        if (isAuto) {
            return m_CoralArm.coralGrabbed();
        } else {
            return false;
        }

        // return m_CoralArm.coralGrabbed() && outtakeTimer.hasElapsed(3);
    }

    public void end(boolean interrupted) {
        m_Elevator.elevatorMoveToPresetMM(elevatorLevel);
        m_CoralArm.intake();
        // m_Elevator.stop();
    }

}
