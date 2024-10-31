package ro.brutariabaiasprie.evidentaproductie.MVC.Components;

import javafx.scene.control.Button;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;

public class SceneButton extends Button {
    private ACTION_TYPE actionType;

    public SceneButton(ACTION_TYPE actionType) {
        super();
        this.actionType = actionType;
        getStyleClass().add("filled-button");
    }

    public SceneButton(String s, ACTION_TYPE actionType) {
        super();
        this.textProperty().set(s);
        this.actionType = actionType;
        getStyleClass().add("filled-button");
    }

    public ACTION_TYPE getActionType() {
        return actionType;
    }

    public void setActionType(ACTION_TYPE actionType) {
        this.actionType = actionType;
    }
}
