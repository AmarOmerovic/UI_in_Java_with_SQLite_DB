package com.amaromerovic.musicui;

import com.amaromerovic.musicui.model.Artist;
import com.amaromerovic.musicui.model.Datasource;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class DialogController {
    @FXML
    private TextField currentField;
    @FXML
    private TextField newField;

    public boolean processResults(Artist artist) {
        String name = newField.getText().trim();
        if (name.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Text field cannot be empty!");
            alert.showAndWait();
            return false;
        }
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                return Datasource.getInstance().updateArtistName(artist.getId(), name);
            }
        };

        task.setOnSucceeded(e -> {
            if (task.valueProperty().get()) {
                artist.setName(name);
            }
        });

        new Thread(task).start();
        return true;
    }

    public void setCurrentName (Artist artist) {
        String currentName = artist.getName();
        currentField.setText(currentName);
    }
}
