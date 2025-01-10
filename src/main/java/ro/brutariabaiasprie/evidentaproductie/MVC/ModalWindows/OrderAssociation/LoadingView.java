package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class LoadingView extends Parent implements Builder<Region> {
    private final String message;

    public LoadingView() {
        this.message = "Va rugam asteptati...";
    }

    public LoadingView(String message) {
        this.message = message;
    }

    @Override
    public Region build() {
        HBox root = new HBox();
        Label label = new Label(message);
        FontIcon loading = new FontIcon("mdi2l-loading");
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        final KeyValue kv = new KeyValue(loading.rotateProperty(),360);
        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();

        root.getChildren().addAll(label, loading);
        root.getStyleClass().add("modal-window");
        return root;
    }
}
