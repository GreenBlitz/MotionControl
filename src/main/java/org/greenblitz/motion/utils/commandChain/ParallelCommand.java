package org.greenblitz.motion.utils.commandChain;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.Vector;

public class ParallelCommand {
	private Vector<Command> m_commands = new Vector<Command>();
	
	public ParallelCommand(Command command) {
		addParallel(command);
	}
	
	public void addParallel(Command command) {
		m_commands.add(command);
	}

	public Vector<Command> getParallelCommands() {
		return m_commands;
	}
	
	public boolean contains(Command command) {
		return m_commands.contains(command);
	}
	
	public void runCommands() {
		m_commands.forEach(x ->
			Scheduler.getInstance().add(x));
	}
	
	public boolean isCompleted() {
		return !m_commands.stream().filter(x ->
			x.isRunning()).findAny().isPresent();
	}
	
	public boolean doesRequire(Subsystem subsystem) {
		return m_commands.stream().filter(x ->
			x.doesRequire(subsystem)).findAny().isPresent();
	}
}