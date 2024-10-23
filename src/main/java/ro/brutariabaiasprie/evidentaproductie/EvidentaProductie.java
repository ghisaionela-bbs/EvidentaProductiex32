package ro.brutariabaiasprie.evidentaproductie;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ro.brutariabaiasprie.evidentaproductie.Controllers.IController;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

public class EvidentaProductie extends Application {
    Stage stage;
    IController controller;

//    @Override
//    public void start(Stage stage) throws IOException, InterruptedException {
//        this.stage = stage;
//        FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("database-connection.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        DatabaseConnectionController databaseConnectionController = fxmlLoader.getController();
//        databaseConnectionController.setController(stage);
//        this.controller = databaseConnectionController;
//        stage.setTitle("Evidenta productie Brutaria Baia Sprie");
//        String css = Objects.requireNonNull(this.getClass().getResource("stylesheet.css")).toExternalForm();
//        scene.getStylesheets().add(css);
//        stage.setScene(scene);
//        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent t) {
//                try {
//                    if(databaseConnectionController.connection != null) {
//                        databaseConnectionController.connection.close();
//                    }
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//                Platform.exit();
//                System.exit(0);
//            }
//        });
//        stage.setMaximized(true);
//        stage.show();
//    }

    public void start(Stage stage) throws Exception {
        ConfigApp.check_config();
        SceneFactory sceneFactory = new SceneFactory(stage);
    }

    public static void main(String[] args) {
        launch();
    }
//
//    @Override
//    public void start(Stage stage) throws Exception {
//        Scene scene = new Scene(new Controller(6L).getView());
//        scene.getStylesheets().add(getClass().getResource("stylesheet2.css").toExternalForm());
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    public class Model {
//
//        private final PresentationModel presentationModel = new PresentationModel();
//        private String domainObject = "Nothing Yet";
//
//
//        public Model() {
//            presentationModel.setTheResult(domainObject);
//        }
//
//        void doSomethingComplicated(BiConsumer<Long, Long> progressUpdater) {
//            for (long idx = 0; idx < presentationModel.getCycleCount(); idx++) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                progressUpdater.accept(idx, presentationModel.getCycleCount());
//            }
//            domainObject = "Something was found";
//        }
//
//        void integrateComplicatedResults() {
//            presentationModel.setTheResult(domainObject);
//        }
//
//        public PresentationModel getPresentationModel() {
//            return presentationModel;
//        }
//
//
//        public static class PresentationModel {
//            private final DoubleProperty progress = new SimpleDoubleProperty(0.0);
//            private final ObjectProperty<Long> cycleCount = new SimpleObjectProperty<>(5L);
//            private final StringProperty theResult = new SimpleStringProperty("");
//
//            public double getProgress() {
//                return progress.get();
//            }
//
//            public DoubleProperty progressProperty() {
//                return progress;
//            }
//
//            public void setProgress(double progress) {
//                this.progress.set(progress);
//            }
//
//            public long getCycleCount() {
//                return cycleCount.get();
//            }
//
//            public ObjectProperty<Long> cycleCountProperty() {
//                return cycleCount;
//            }
//
//            public void setCycleCount(long cycleCount) {
//                this.cycleCount.set(cycleCount);
//            }
//
//            public String getTheResult() {
//                return theResult.get();
//            }
//
//            public StringProperty theResultProperty() {
//                return theResult;
//            }
//
//            public void setTheResult(String theResult) {
//                this.theResult.set(theResult);
//            }
//        }
//    }
//
//    public class Controller {
//
//        private final View view;
//        private Model model = new Model();
//
//        public Controller(long initialCycleCount) {
//            model.getPresentationModel().setCycleCount(initialCycleCount);
//            view = new View(model.getPresentationModel(), this::startSomethingBig);
//        }
//
//        private void startSomethingBig(Runnable postRunGUIAction) {
//            Task<Void> bigTask = new Task<Void>() {
//                @Override
//                protected Void call() {
//                    model.doSomethingComplicated(this::updateProgress);
//                    return null;
//                }
//            };
//            bigTask.setOnSucceeded(evt -> {
//                model.getPresentationModel().progressProperty().unbind();
//                model.integrateComplicatedResults();
//                postRunGUIAction.run();
//            });
//            Thread bigTaskThread = new Thread(bigTask);
//            model.getPresentationModel().progressProperty().bind(bigTask.progressProperty());
//            bigTaskThread.start();
//        }
//
//
//        public View getView() {
//            return view;
//        }
//    }
//
//    public class View extends VBox {
//
//        private final Model.PresentationModel viewModel;
//        private final BooleanProperty showProgress = new SimpleBooleanProperty(false);
//        private final Consumer<Runnable> actionHandler;
//
//        public View(Model.PresentationModel viewModel, Consumer<Runnable> actionHandler) {
//            this.viewModel = viewModel;
//            this.actionHandler = actionHandler;
//            initializeLayout();
//        }
//
//        private void initializeLayout() {
//            getChildren().addAll(createTopBox(), createButton());
//            setPadding(new Insets(20));
//        }
//
//        private Node createButton() {
//            Button button = new Button("Start");
//            button.setOnAction(evt -> {
//                showProgress.set(true);
//                button.setDisable(true);
//                actionHandler.accept(() -> {
//                    showProgress.set(false);
//                    button.setDisable(false);
//                });
//            });
//            return button;
//        }
//
//        private Region createTopBox() {
//            StackPane results = new StackPane();
//            results.getChildren().addAll(createDataBox(), createProgressIndicator());
//            return results;
//        }
//
//        private Node createDataBox() {
//            Label prompt = new Label("Number of Cycles:");
//            prompt.getStyleClass().add("label-text");
//            HBox inputBox = new HBox(6, prompt, createTextField());
//            inputBox.setAlignment(Pos.CENTER);
//            VBox results = new VBox(20, inputBox, createDataLabel());
//            results.setAlignment(Pos.CENTER);
//            results.visibleProperty().bind(showProgress.not());
//            return results;
//        }
//
//        @NotNull
//        private Label createDataLabel() {
//            Label dataLabel = new Label();
//            dataLabel.textProperty().bind(viewModel.theResultProperty());
//            dataLabel.getStyleClass().add("data-text");
//            return dataLabel;
//        }
//
//        private Node createTextField() {
//            TextField textField = new TextField();
//            TextFormatter<Long> textFormatter = new TextFormatter<>(new LongStringConverter());
//            textField.setTextFormatter(textFormatter);
//            textField.setMaxWidth(120.0);
//            textFormatter.valueProperty().bindBidirectional(viewModel.cycleCountProperty());
//            return textField;
//        }
//
//        private Node createProgressIndicator() {
//            ProgressIndicator progressIndicator = new ProgressIndicator();
//            progressIndicator.progressProperty().bind(viewModel.progressProperty());
//            progressIndicator.setMinSize(200, 200);
//            progressIndicator.visibleProperty().bind(showProgress);
//            progressIndicator.visibleProperty().addListener(observable -> {
//                if (progressIndicator.isVisible()) {
//                    Transition transition = new Transition() {
//                        {
//                            setCycleDuration(Duration.millis(2000));
//                        }
//                        @Override
//                        protected void interpolate(double v) {
//                            progressIndicator.setOpacity(v);
//                        }
//                    };
//                    transition.play();
//                }
//            });
//            return progressIndicator;
//        }
//    }
}