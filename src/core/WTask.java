/*
 Copyright (c) UICHUIMI 02/2016

 This file is part of WhiteSuit.

 WhiteSuit is free software: you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 WhiteSuit is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with Foobar.
 If not, see <http://www.gnu.org/licenses/>.
 */

package core;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Date created 10/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public abstract class WTask implements Runnable {

    /*
     * Possible state transitions:
     * NEW -> COMPLETING -> NORMAL
     * NEW -> COMPLETING -> EXCEPTIONAL
     * NEW -> CANCELLED
     * NEW -> INTERRUPTING -> INTERRUPTED
     */

    private final Property<String> title = new SimpleObjectProperty<>();
    private final Property<Boolean> newProperty = new SimpleObjectProperty<>(true);
    private final Property<Boolean> cancelled = new SimpleObjectProperty<>(false);
    private final Property<Boolean> completed = new SimpleObjectProperty<>(false);
    private PrintStream printStream = System.out;
    private Process process;

    @Override
    public void run() {
        newProperty.setValue(false);
        start();
        completed.setValue(true);
    }

    public abstract void start();

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    public String getTitle() {
        return title.getValue();
    }

    protected void setTitle(String title) {
        this.title.setValue(title);
    }

    public Property<String> titleProperty() {
        return title;
    }

    protected void println(String line) {
        printStream.println(line);
    }

    /**
     * Executes a bash task, calling system console. Calling <code>cancel()</code> will stop the running task.
     *
     * @param args List of args. No need to use spaces as separators.
     * @return the return value of the process
     */
    protected int execute(String... args) {
        return execute(Arrays.asList(args));
    }

    /**
     * Any object will be parsed using String::valueOf.
     *
     * @param args List of args. Everything will be converted to String using String.valueOf
     * @return return value of execution
     */
    protected int execute(Object... args) {
        return execute(Arrays.stream(args).map(String::valueOf).collect(Collectors.toList()));
    }

    /**
     * Executes a bash task, calling system console. Calling <code>cancel()</code> will stop the running task.
     *
     * @param args List of args. No need to use spaces as separators.
     * @return the return value of the process
     */
    protected int execute(List<String> args) {
        if (cancelled.getValue()) return -1;
        final ProcessBuilder builder = new ProcessBuilder(args);
        System.out.println(builder.command());
        builder.redirectErrorStream(true);
        try {
            process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(this::println);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void cancel() {
        if (process != null) process.destroy();
        cancelled.setValue(true);
        completed.setValue(true);
    }

    public Property<Boolean> cancelledProperty() {
        return cancelled;
    }

    public Boolean isCancelled() {
        return cancelled.getValue();
    }

    /**
     * Whether this Task has been completed or not. A task is completed by finishing start method, or by cancelling it.
     * You must check cancelledProperty to know what happened.
     *
     * @return
     */
    public Property<Boolean> completedProperty() {
        return completed;
    }

    /**
     * Tells whether this Task has been completed. A task is completed by finishing start method, or by cancelling it.
     * You must check cancelledProperty to know what happened.
     *
     * @return true only if <code>start()</code> method has completed
     */
    public Boolean isTerminated() {
        return completed.getValue();
    }
}
