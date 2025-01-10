package ro.brutariabaiasprie.evidentaproductie.MVC.Components;

import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;

public class ColoredProgressBar extends ProgressBar {
    public ColoredProgressBar(double progress) {
        super(progress);
        if(progress > 1) {
            getStyleClass().add("progress-bar-overflow");
        } else if (progress == 1) {
            getStyleClass().add("progress-bar-exact");
        }
    }
}
