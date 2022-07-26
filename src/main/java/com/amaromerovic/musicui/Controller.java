package com.amaromerovic.musicui;


import com.amaromerovic.musicui.model.Album;
import com.amaromerovic.musicui.model.Artist;
import com.amaromerovic.musicui.model.Datasource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Optional;

public class Controller {
    @FXML
    private Button listArtistsButton;
     @FXML
    private Button listAlbumsForArtistButton;
     @FXML
    private Button updateArtistButton;
     @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private TableView artistTable;

    public void listArtists() {
        Task<ObservableList<Artist>> task = new GetAllArtistsTask();
        artistTable.itemsProperty().bind(task.valueProperty());
        progressBar.progressProperty().bind(task.progressProperty());

        progressBar.setVisible(true);
        listArtistsButton.setDisable(true);
        listAlbumsForArtistButton.setDisable(true);
        updateArtistButton.setDisable(true);

        task.setOnSucceeded(event -> {
            progressBar.setVisible(false);
            listArtistsButton.setDisable(true);
            listAlbumsForArtistButton.setDisable(false);
            updateArtistButton.setDisable(false);
        });
        task.setOnFailed(event -> {
            progressBar.setVisible(false);
            listArtistsButton.setDisable(false);
            listAlbumsForArtistButton.setDisable(false);
            updateArtistButton.setDisable(false);
        });

        new Thread(task).start();
    }

    public void listAlbumsForArtist() {
        Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();
        if (artist == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("No artist selected!");
            alert.showAndWait();
            return;
        }

        Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() {
                return FXCollections.observableArrayList(Datasource.getInstance().queryAlbumForArtistId(artist.getId()));
            }
        };

        artistTable.itemsProperty().bind(task.valueProperty());
        progressBar.progressProperty().bind(task.progressProperty());

        progressBar.setVisible(true);
        listArtistsButton.setDisable(true);
        listAlbumsForArtistButton.setDisable(true);
        updateArtistButton.setDisable(true);

        task.setOnSucceeded(event -> {
            progressBar.setVisible(false);
            listArtistsButton.setDisable(false);
            listAlbumsForArtistButton.setDisable(true);
            updateArtistButton.setDisable(true);
        });
        task.setOnFailed(event -> {
            progressBar.setVisible(false);
            listArtistsButton.setDisable(false);
            listAlbumsForArtistButton.setDisable(true);
            updateArtistButton.setDisable(true);
        });

        new Thread(task).start();
    }

    public void updateArtist() {

        Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();
        if (artist == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("No artist selected!");
            alert.showAndWait();
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Update Artist");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("dialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            return;
        }
        DialogController  controller = fxmlLoader.getController();
        controller.setCurrentName(artist);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if ((result.isPresent()) && (result.get() == ButtonType.OK)){
            if (controller.processResults(artist)){
                artistTable.refresh();
            }
        }
    }

}

class GetAllArtistsTask extends Task {
    @Override
    public ObservableList<Artist> call() {
        return FXCollections.observableArrayList(Datasource.getInstance().queryArtists(Datasource.ORDER_BY_ASC));
    }
}

