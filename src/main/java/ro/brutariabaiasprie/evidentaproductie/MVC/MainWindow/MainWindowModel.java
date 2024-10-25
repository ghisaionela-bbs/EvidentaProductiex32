package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;

public class MainWindowModel {
    private User CONNECTED_USER;
    private final IntegerProperty openedTab = new SimpleIntegerProperty(0);

    public MainWindowModel() {
        User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
        if(user != null) {
            this.CONNECTED_USER = user;
        }
    }

    public User getCONNECTED_USER() {
        return CONNECTED_USER;
    }

    public void setCONNECTED_USER(User CONNECTED_USER) {
        this.CONNECTED_USER = CONNECTED_USER;
    }

    public int getOpenedTab() {
        return openedTab.get();
    }

    public IntegerProperty openedTabProperty() {
        return openedTab;
    }

    public void setOpenedTab(int openedTab) {
        this.openedTab.set(openedTab);
    }
}
