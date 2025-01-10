package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows;

import javafx.concurrent.Task;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;

public abstract class ModalWindow {
    protected abstract void onWindowAction(ACTION_TYPE actionType);

    /***
     * Runs a task on a different thread for database operations
     * @param runnable the runnable that will be run by the task
     */
    protected void runDatabaseTask(Runnable runnable) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
}
