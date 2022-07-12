package pkg;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        final VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(25, 25, 25, 25));
        VBox.setVgrow(vBox, Priority.ALWAYS);

        final Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ffmpeg script generator util");

        final ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(".bat", ".sh"));
        comboBox.setValue(".bat");
        vBox.getChildren().add(new VBox(new Label("File format"), comboBox));

        vBox.getChildren().add(new Label("Press INSERT button to call dialog"));

        final TextField from = new TextField();
        from.setOnKeyReleased(event -> {
            set(event.getCode().getName(), primaryStage, from);
        });
        vBox.getChildren().add(new VBox(new Label("From"), from));

        final TextField to = new TextField();
        to.setOnKeyReleased(event -> {
            set(event.getCode().getName(), primaryStage, to);
        });
        vBox.getChildren().add(new VBox(new Label("To"), to));


        final Button button = new Button("Convert");
        button.setOnMouseClicked(event -> {
            final File source = new File(from.getText());
            final Path destination = Path.of(to.getText());
            if (source.exists() & Files.exists(destination)) {
                try {
                    final File[] listFiles = source.listFiles();
                    if (listFiles != null) {
                        final Path scriptPath = destination.resolve("convert" + comboBox.getValue());
                        try (final BufferedWriter writer = Files.newBufferedWriter(scriptPath)) {
                            for (final File file : listFiles) {
                                final StringBuilder stringBuilder = new StringBuilder("ffmpeg")
                                        .append(" -i \"" + file.getAbsolutePath() + "\"")
                                        .append(" -vf scale=960:-1")
                                        .append(" -map 0")
                                        .append(" \"" + destination.resolve(file.getName()) + "\"");
                                writer.write(stringBuilder.toString());
                                writer.newLine();
                            }
                            new Alert(Alert.AlertType.INFORMATION, "Script complete").show();
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "ERROR").show();
                }
            }
        });
        vBox.getChildren().add(button);

        primaryStage.show();
    }

    private static void set(final String key, final Stage primaryStage, final TextField textField) {
        if (key.equals("Insert")) {
            final File file = new DirectoryChooser().showDialog(primaryStage);
            if (file != null && file.exists()) {
                textField.setText(file.getAbsolutePath());
            }
        }
    }
}
