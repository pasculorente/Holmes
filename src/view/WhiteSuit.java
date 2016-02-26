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

package view;

import core.WTask;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WhiteSuit extends Application {

    private final static List<WTask> RUNNING_TASKS = new ArrayList<>();
    private static final Properties properties = new Properties();
    private static Stage primaryStage;
    private static MainView mainView;

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void executeTask(WTask task) {
        mainView.executeTask(task);
        task.completedProperty().addListener((observable, previous, completed) -> {
            if (completed) RUNNING_TASKS.remove(task);
        });
        RUNNING_TASKS.add(task);
    }

    public static Properties getProperties() {
        return properties;
    }

    @Override
    public void start(Stage primaryStage) {
        WhiteSuit.primaryStage = primaryStage;
        loadProperties();
        mainView = new MainView();
        final Scene scene = new Scene(mainView);
        mainView.setPrefSize(800, 600);
        scene.getStylesheets().add("css/default.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("ExomeSuite");
        primaryStage.setOnCloseRequest(this::endRunningTasks);
        primaryStage.show();
    }

    private void endRunningTasks(WindowEvent event) {
        if (!RUNNING_TASKS.isEmpty()) {
            if (userWantsToExitAnyway()) RUNNING_TASKS.forEach(WTask::cancel);
            else event.consume();
        }
    }

    private boolean userWantsToExitAnyway() {
        final ExitDialog exitDialog = new ExitDialog("There are still running tasks. Do you want to exit anyway?", "Yes, exit", "No, continue");
        exitDialog.show(primaryStage);
        return exitDialog.isExit();
    }

    @Override
    public void stop() throws Exception {
        RUNNING_TASKS.forEach(WTask::cancel);
    }

    private void loadProperties() {
        try {
            if (new File("whitesuit.properties").exists())
                properties.load(new FileInputStream("whitesuit.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    properties.store(new FileOutputStream("whitesuit.properties"), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
