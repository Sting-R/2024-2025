package frc.robot.commands.preset_commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CoralArmSubsystem;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;

public class knockAlgaeOff extends Command {
    ElevatorSubsystem m_Elevator;
    CoralArmSubsystem m_CoralArm;
    CoralArmLevels coralLevel;
    ElevatorPresets elevatorLevel;
    Timer outtakeTimer;

    public knockAlgaeOff(ElevatorSubsystem elevator, CoralArmSubsystem coralArm, boolean isLowerAlgae) {
        addRequirements(elevator, coralArm);
        m_Elevator = elevator;
        m_CoralArm = coralArm;
        coralLevel = CoralArmLevels.defaultState;
        if (isLowerAlgae) {
            elevatorLevel = ElevatorPresets.kickLowerAlgaeOff;
        } else {
            elevatorLevel = ElevatorPresets.kickUpperAlgaeOff;
        }
        // System.out.println("Kick Algae Off Preset reached");
    }

    public void execute() {

        m_Elevator.elevatorMoveToPresetMM(elevatorLevel);
        if (m_Elevator.isElevatorAtDesiredState(elevatorLevel).getAsBoolean()) {
            coralLevel = CoralArmLevels.lvl4;
        }
        m_CoralArm.outtake(coralLevel, false);
    }

    @Override
    public boolean isFinished() {
        return m_CoralArm.isCoralArmAtDesiredState(coralLevel).getAsBoolean()
                && m_Elevator.isElevatorAtDesiredState(elevatorLevel).getAsBoolean();
    }

    // public void end(boolean interrupted) {

    // // m_Elevator.stop();
    // }

}
