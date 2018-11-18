package org.greenblitz.motion.utils.commandChain;

import edu.wpi.first.wpilibj.command.Command;

import java.util.Optional;
import java.util.Vector;

public class CommandChain extends Command {

    public CommandChain() {
        addCommand(new Command() {
            protected boolean isFinished() {
                return true;
            }
        });
    }

    private final Vector<ParallelCommand> m_commands = new Vector<ParallelCommand>();
    private int m_currentCommand = 0;
    private boolean m_hasRan = false;

    protected final void addCommand(Command toRun) {
        m_commands.add(new ParallelCommand(toRun));
    }

    /**
     * Adds a command to run after the last entered group of parallel commands.
     */
    protected final void addSequential(Command toRun) {
        addCommand(toRun);
    }

    protected final void addSequential(Command toRun, Command after) {
        for (ParallelCommand parallelCommand : m_commands) {
            if (parallelCommand.contains(after)) {
                try {
                    m_commands.get(m_commands.indexOf(parallelCommand) + 1).addParallel(toRun);
                } catch (Exception e) {
                    m_commands.add(new ParallelCommand(toRun));
                }
                return;
            }
        }
        throw new IllegalArgumentException("The Command " + after.getName() +
                " is not a part of this command chain. please enter it beforehand.");
    }

    /**
     * Adds a command to run with the last entered group of parallel commands.
     */
    protected final void addParallel(Command toRun) {
        addParallel(toRun, m_commands.lastElement().getParallelCommands().get(0));
    }

    protected final void addParallel(Command toRun, Command with) {
        //Filters only elements which contain with -> takes the first one of them
        //(if there are any of them hence the name Optional <=> We don't know if it exists
        Optional<ParallelCommand> found = m_commands.stream().filter(x -> x.contains(with)).findAny();
        //If it exists
        if (found.isPresent()) {
            //get the actual value and do shit
            found.get().addParallel(toRun);
            return;
        }
        throw new IllegalArgumentException("The Command " + with.getName() +
                " is not a part of this command chain. please enter it beforehand.");
    }

    @Override
    protected final void initialize() {
        System.out.println("Running command chain: " + getName());
        if (!m_hasRan) {
            m_hasRan = true;
            onFirstRun();
        }
        m_currentCommand = 0;
        m_commands.get(m_currentCommand).runCommands();
    }

    protected void onFirstRun() {
    }

    @Override
    protected final void execute() {
        ParallelCommand currentCommands = m_commands.get(m_currentCommand);
        if (currentCommands.isCompleted()) {
            m_currentCommand++;
            if (!isFinished())
                m_commands.get(m_currentCommand).runCommands();
        }
    }

    public void end() {
        System.out.println(getClass().getCanonicalName() + " Has Finished!");
    }

    @Override
    protected final boolean isFinished() {
        return m_currentCommand == m_commands.size();
    }
}