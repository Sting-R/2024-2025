package frc.robot.archived_classes;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Utility;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class CompCommands {
    public CompCommands(SubsystemBase subsystem) {
        Utility.printLn("CompCommands Object Successfully Created.");
    }

    public SequentialCommandGroup useCommand(Command... commandsToExecute) {
        Command firstCommand = commandsToExecute[0];
        Command[] executableCommandsArray = new Command[commandsToExecute.length - 1];
        for (int i = 1; i < commandsToExecute.length; i++) {
            executableCommandsArray[i - 1] = commandsToExecute[i];
        }
        return firstCommand.andThen(executableCommandsArray);
    }
}
// This Is The Twenty-First Line