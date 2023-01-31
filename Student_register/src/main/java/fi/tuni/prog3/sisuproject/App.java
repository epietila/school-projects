package fi.tuni.prog3.sisuproject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfoenix.controls.JFXButton;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.TreeMap;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerNextArrowBasicTransition;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Year;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;



/**
 * JavaFX App
 */

public class App extends Application {
    
    private enum Type {
        DEGREE, MODULE, COURSE
    }
    
    private final TreeMap<String, Degree> allDegrees = new TreeMap<>();
    private Student currentStudent;
    private Degree currentDegree;
    private Progress currentProgress;
    
    // For different scenes
    private Scene accordionScene;
    private TreeView<String> tree;
    private Label progressLabel;
    
    /**
     * Reads degree structures from the URL link and returns data as String type
     * @param urlString the link where university degree structures are listed
     * @return the data contained in the urlString as a String type
     * @throws Exception 
     */
    private static String readUrl(String urlString) throws Exception {
        
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read); 
            }

            return buffer.toString();
        } 
    
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    /**
     * Read contents of JSON string into a JSON object.
     * @param id id of the module or course. Added to a link-string depending on
     * the type. If type is Type.DEGREE, id doesn't matter.
     * @param t type of the JSON string that will be read. The link is different
     * for searching degrees, modules and courses.
     * @return JSON object acquired from the Sisu API.
     * @throws Exception 
     */
    private static JsonObject readJson(String id, Type t) throws Exception {
        String json = "";
        switch(t) {
            case DEGREE:
                json = readUrl("https://sis-tuni.funidata.fi/kori/api/module-"
                    + "search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-"
                    + "university-root-id&moduleType=DegreeProgramme&limit=1000");
                break;
            case MODULE:
                json = readUrl("https://sis-tuni.funidata.fi/kori/api/modules/" 
                + id);
                break;    
        
            case COURSE:
                json = readUrl("https://sis-tuni.funidata.fi/kori/api/course-"
                        + "units/by-group-id?groupId="
                        + id + "&universityId=tuni-university-root-id");
                return JsonParser.parseString(json).getAsJsonArray().get(0)
                        .getAsJsonObject();
        }
                
        JsonObject jParser = JsonParser.parseString(json).getAsJsonObject();
        return jParser;
    }
    
    /**
     * Print name of a course in the tree view.
     * @param id id string of the course in Sisu API.
     * @param rootModule The tree item that is the parent of the course.
     * @throws Exception 
     */
    private void printCourseName(String id, CheckBoxTreeItem<String> rootModule) 
            throws Exception{
        JsonObject obj;
        
        try {
            obj = readJson(id, Type.COURSE);
    
            CheckBoxTreeItem<String> course;
            if (obj.getAsJsonObject("name").get("fi") != null){
                course = new CheckBoxTreeItem<>(obj.getAsJsonObject("name")
                        .get("fi").getAsString());
            } else {
                course = new CheckBoxTreeItem<>(obj.getAsJsonObject("name")
                        .get("en").getAsString());
            }
            rootModule.getChildren().add(course);
            course.selectedProperty().addListener((cl) -> {
                if (course.isSelected()) {
                    currentProgress.addCompletedCourse(obj);
                }
                else if (!course.isSelected()) {
                    currentProgress.removeCompletedCourse(obj);
                }
            });
            
        } catch (FileNotFoundException | NullPointerException e) {
            // Sisu API shows items under a module but doesn't contain data
            // for it.
            CheckBoxTreeItem<String> course  = new CheckBoxTreeItem<>(
                    "Sisu API näyttää tässä kurssin ilman lisätietoja");
            rootModule.getChildren().add(course);
        }
    }
    
    /**
     * Find submodules for a degree or a module.
     * @param id Module id in Sisu API
     * @param rootModule Parent module
     * @param root True if handling the very root of the tree view.
     * @throws Exception 
     */
    private void getSubModules(String id, CheckBoxTreeItem<String> rootModule, 
            boolean root) throws Exception {
                
        JsonObject obj;
        try {
            obj = readJson(id, Type.MODULE);
            CheckBoxTreeItem<String> module;
            if(!root){
                // Add module name to the tree view
                if (obj.getAsJsonObject("name").get("fi") != null){
                    module  = new CheckBoxTreeItem<>(obj.getAsJsonObject("name")
                            .get("fi").getAsString());
                } else {
                    module  = new CheckBoxTreeItem<>(obj.getAsJsonObject("name")
                            .get("en").getAsString());
                }
                rootModule.getChildren().add(module);
            } else {
                module = rootModule;
            }
            
            
            // Select correct hierarchy for finding submodules in Sisu API
            JsonArray rules;
            if (obj.getAsJsonObject("rule").getAsJsonArray("rules") == null){
                rules = obj.getAsJsonObject("rule").getAsJsonObject("rule")
                        .getAsJsonArray("rules");     
                if (rules.get(0).getAsJsonObject().get("type").getAsString()
                        .equals("CompositeRule")){
                    rules = rules.get(0).getAsJsonObject()
                            .getAsJsonArray("rules");
                }
            } else {
                rules = obj.getAsJsonObject("rule").getAsJsonArray("rules");
                if (rules.get(0).getAsJsonObject().get("type").getAsString()
                        .equals("CompositeRule")){
                    rules = rules.get(0).getAsJsonObject()
                            .getAsJsonArray("rules");
                }
            }
            // Find modules by recursion
            for (var el : rules) {
                JsonObject modObj = el.getAsJsonObject();
                String type = modObj.get("type").getAsString();

                if (type.contains("CourseUnitRule")) {
                    if(modObj.get("courseUnitGroupId") != null){
                        printCourseName(modObj.get("courseUnitGroupId")
                                .getAsString(), module);
                    }          
                } else {
                    if(modObj.get("moduleGroupId") == null){
                        if (modObj.get("anyModuleGroupId") != null) {
                            getSubModules(modObj.get("anyModuleGroupId")
                                    .getAsString(), module, false);
                        }
                    } else {
                        getSubModules(modObj.get("moduleGroupId")
                                .getAsString(), module, false);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // Sisu API shows items under a module but doesn't contain data
            // for it.
            CheckBoxTreeItem<String> module = new CheckBoxTreeItem<>(
                    "Sisu API näyttää tässä modulin ilman lisätietoja");
            rootModule.getChildren().add(module);
        } catch (IndexOutOfBoundsException e){
            
        }
    }
   
    /**
     * Read all available degrees into Degree objects and save to allDegrees.
     * @param obj JSON object containing all the degree information in Sisu API.
     */
    public void readToDegree(JsonObject obj){
        JsonArray degrees = obj.getAsJsonArray("searchResults"); 
        
        for (JsonElement degree : degrees) {
            JsonObject degreeObj = degree.getAsJsonObject();
            String name = degreeObj.getAsJsonPrimitive("name").getAsString();
            String degId = degreeObj.getAsJsonPrimitive("id").getAsString();
            
            JsonElement min = degreeObj.getAsJsonObject("credits").get("min");
            JsonElement max = degreeObj.getAsJsonObject("credits").get("max");
            
            String minC;
            String maxC;
            
            if (max.isJsonNull()) {
                minC = min.getAsString();
                maxC = null;
            }
            else if (min.isJsonNull()) {
                maxC = max.getAsString();
                minC = null;
            }
            else {
                minC = min.getAsString();
                maxC = max.getAsString();
            }
            
            Degree newDegree = new Degree(name, minC, maxC, degId);
            this.allDegrees.put(name, newDegree);
        }
    }
    
    /**
     * Stores student data and completed courses in JSON format
     * @throws IOException 
     */
    private void writeJsonFile() throws IOException {
        
        if (currentStudent != null) {
            JsonArray root = new JsonArray();
            String filename = currentStudent.getName() + ".json";
        
            JsonObject studentInfo = new JsonObject();
            
            studentInfo.addProperty("Opiskelijan nimi", currentStudent
                    .getName());
            studentInfo.addProperty("Opiskelijanumero", currentStudent
                    .getStudentNumber());
            studentInfo.addProperty("Aloitusvuosi", currentStudent
                    .getStartYear());
            studentInfo.addProperty("Arvioitu valmistumisvuosi", currentStudent
                    .getEndYear());
        
            root.add(studentInfo);
            
            if (currentProgress != null) {
                JsonArray courses = new JsonArray();
 
                currentProgress.getCompletedCourses().entrySet().forEach(
                        course -> {
                    JsonObject courseObj = new JsonObject();
                    courseObj.addProperty("Kurssin nimi", course.getKey());
                    courseObj.addProperty("Opintopisteet", course.getValue());
                    courses.add(courseObj);
                });

                JsonObject progressObj = new JsonObject();
                progressObj.addProperty("Suoritetut opintopisteet", 
                        currentProgress.getProgress().getKey());
                progressObj.addProperty("Vaaditut opintopisteet", 
                        currentProgress.getProgress().getValue());

                root.add(courses);
                root.add(progressObj);
                
                try (var fileWriter = new FileWriter(filename, Charset
                        .forName("UTF-8"))) {
                    progressObj.addProperty("Suoritetut opintopisteet",
                            currentProgress.getProgress().getKey());
                    progressObj.addProperty("Vaaditut opintopisteet", 
                            currentProgress.getProgress().getValue());
                }
            }
            
            try (var fileWriter = new FileWriter(filename, Charset
                .forName("UTF-8"))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(root, fileWriter);
            }
        }   
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Sisu");
        
        // Scene for degree choosing
        BorderPane root = new BorderPane();
        Scene degreeScene = new Scene(root, 640, 500);
        JsonObject data = readJson("", Type.DEGREE);
        readToDegree(data);
        Label welcomeLabel = new Label("Tervetuloa! Valitse tästä tutkinto.");
        welcomeLabel.setFont(Font.font("Arial", 18));
        welcomeLabel.setTextFill(Color.MEDIUMORCHID);
        
        // Set up the starting page for student information input
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(20);
        grid.setBackground(new Background(new BackgroundFill(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.BEIGE),
                new Stop(1, Color.BISQUE)), 
                CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(grid, 640, 500);
        
        Label infoLabel = new Label("Anna seuraavat tiedot: ");
        infoLabel.setFont(Font.font("Arial", 18));
        infoLabel.setTextFill(Color.MEDIUMORCHID);
        grid.add(infoLabel, 0, 0, 2, 2);
        
        VBox labels = new VBox(20);
        VBox textFields = new VBox(10);
        VBox errorLabels = new VBox(20);
        
        grid.add(labels, 0, 2);
        grid.add(textFields, 1, 2);
        grid.add(errorLabels, 3, 2);
        
        Label nameLabel = new Label("Nimi: ");
        nameLabel.setFont(Font.font("Arial", 15));
        nameLabel.setTextFill(Color.MEDIUMORCHID);
        labels.getChildren().add(nameLabel);
        TextField nameField = new TextField();
        textFields.getChildren().add(nameField);
        Label nameError = new Label("");
        nameError.setFont(Font.font("Arial", 13));
        errorLabels.getChildren().add(nameError);
        
        Label numberLabel = new Label("Opiskelijanumero: ");
        numberLabel.setFont(Font.font("Arial", 15));
        numberLabel.setTextFill(Color.MEDIUMORCHID);
        labels.getChildren().add(numberLabel);
        TextField numberField = new TextField();
        textFields.getChildren().add(numberField);
        Label numberError = new Label("");
        numberError.setFont(Font.font("Arial", 13));
        errorLabels.getChildren().add(numberError);
        
        Label startYearLabel = new Label("Opintojen aloitusvuosi: ");
        startYearLabel.setFont(Font.font("Arial", 15));
        startYearLabel.setTextFill(Color.MEDIUMORCHID);
        labels.getChildren().add(startYearLabel);
        TextField startYearField = new TextField();
        textFields.getChildren().add(startYearField);
        Label startYearError = new Label("");
        startYearError.setFont(Font.font("Arial", 13));
        errorLabels.getChildren().add(startYearError);
        
        Label endYearLabel = new Label("Valmistumisen tavoitevuosi: ");
        endYearLabel.setFont(Font.font("Arial", 15));
        endYearLabel.setTextFill(Color.MEDIUMORCHID);
        labels.getChildren().add(endYearLabel);
        TextField endYearField = new TextField();
        textFields.getChildren().add(endYearField);
        Label endYearError = new Label("");
        endYearError.setFont(Font.font("Arial", 13));
        errorLabels.getChildren().add(endYearError);
        
        Button readyBtn = new Button("Valmis");
        readyBtn.setFont(Font.font("Arial", 15));
        readyBtn.setBackground(new Background(new BackgroundFill(Color.PLUM, 
                CornerRadii.EMPTY, Insets.EMPTY)));
        grid.add(readyBtn, 1, 3);
        
        readyBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                // If all the information given to the program are in correct 
                // form, continue  to choose degree, otherwise show error labels
                if (nameField.getText().trim().isEmpty()) {
                    nameError.setText(" Anna nimesi!");
                    nameError.setTextFill(Color.RED);
                }
                else if (numberField.getText().trim().isEmpty()) {
                    numberError.setText(" Anna opiskelijanumerosi!");
                    numberError.setTextFill(Color.RED);
                }
                else if (startYearField.getText().trim().isEmpty()) {
                    startYearError.setText(" Anna opintojesi aloitusvuosi!");
                    startYearError.setTextFill(Color.RED);
                }
                else if (endYearField.getText().trim().isEmpty()) {
                    endYearError.setText(" Anna arvioitu valmistumisvuotesi!");
                    endYearError.setTextFill(Color.RED);
                }
                else {
                    nameError.setText("");
                    numberError.setText("");
                    startYearError.setText("");
                    endYearError.setText("");
                    String name = nameField.getText();
                    String studentNumber = numberField.getText();
                    try {
                        Integer.parseInt(startYearField.getText());
                    }
                    catch (NumberFormatException e) {
                        startYearError.setText(" Anna vuosi numeroina!");
                        startYearError.setTextFill(Color.RED);
                    }
                    try {
                        Integer.parseInt(endYearField.getText());
                    }
                    catch (NumberFormatException e) {
                        endYearError.setText(" Anna vuosi numeroina!");
                        endYearError.setTextFill(Color.RED);
                    }
                    
                    int startYear = Integer.parseInt(startYearField.getText());
                    int endYear = Integer.parseInt(endYearField.getText());
                    
                    if (startYear < 1960 || startYear > Year.now().getValue()) {
                        startYearError.setText(" Aloitusvuosi on oltava vähintään"
                                + " vuosi 1960 \nja korkeintaan kuluva vuosi");
                        startYearError.setTextFill(Color.RED);
                    }
                    else if (endYear <= startYear || endYear > startYear + 7) {
                        endYearError.setText(" Arvioitu valmistumisvuosi on\n "
                                + "oltava suurempi kuin aloitusvuosi,\n mutta "
                                + "viimeistään 7 vuoden\n päässä "
                                + "aloitusvuodesta");
                        endYearError.setTextFill(Color.RED);
                    }
                    else {
                        stage.setScene(degreeScene);
                        currentStudent = new Student(name, studentNumber,
                                startYear, endYear);
                    }
                } 
            } 
        });
        
        // Side menu for changing the degree or student information or to exit
        JFXHamburger menu = new JFXHamburger();
        HamburgerNextArrowBasicTransition transition = 
                new HamburgerNextArrowBasicTransition(menu);
        transition.setRate(transition.getRate() * -1);
        
        menu.setAlignment(Pos.CENTER_LEFT);
        menu.setPadding(new Insets(5));
        menu.setStyle("-fx-background-color: #DDA0DD;");
        
        JFXButton degreeBtn = new JFXButton("Vaihda tutkinto");
        degreeBtn.setFont(Font.font("Arial", 15));
        degreeBtn.setId("degreeBtn");
        degreeBtn.setVisible(false);
        degreeBtn.setMinWidth(70);
        
        JFXButton goBackBtn = new JFXButton("Alkuun");
        goBackBtn.setFont(Font.font("Arial", 15));
        goBackBtn.setId("goBackBtn");
        goBackBtn.setVisible(false);
        goBackBtn.setMinWidth(40);
        
        JFXButton exitBtn = new JFXButton("Lopeta");
        exitBtn.setFont(Font.font("Arial", 15));
        exitBtn.setId("exitBtn");
        exitBtn.setVisible(false);
        exitBtn.setMinWidth(40);
        exitBtn.setOnMouseClicked(event -> {
            try {
                writeJsonFile();
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
            stage.close();        
        });
                
        JFXButton[] buttons = {degreeBtn, goBackBtn, exitBtn,};
        
        degreeBtn.setOnMouseClicked(event -> {
            // Delete progress tracking and return to choosing degree
            tree.setRoot(null);
            transition.setRate(-1);
            transition.play();
            for (var b : buttons) {
                b.setVisible(false);
            }
            stage.setScene(degreeScene);   
        });
        
        goBackBtn.setOnMouseClicked(event -> {
            // Return to student information view
            transition.setRate(-1);
            transition.play();
            
            for (JFXButton jfxButton : buttons) {
                jfxButton.setVisible(false);
            }
            stage.setScene(scene);
        });
        
        menu.setOnMouseClicked(event -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();
            
            if (transition.getRate() == -1) {
                for (JFXButton jfxButton : buttons) {
                    jfxButton.setVisible(false);
                }
            } else {
                for (JFXButton jfxButton : buttons) {
                    if (jfxButton.getId().equals("degreeBtn") && 
                            stage.getScene()!=accordionScene){
                        jfxButton.setVisible(false);
                    } else {
                        jfxButton.setVisible(true);
                    }
                    
                    jfxButton.setContentDisplay(ContentDisplay.LEFT);
                    jfxButton.setMaxWidth(Double.MAX_VALUE);
                }
            }
        });
        
        // Set the hamburger menu visible in the GUI
        ScrollPane hamPane = new ScrollPane();
        VBox vBox = new VBox();
        hamPane.setBackground(new Background(new BackgroundFill(Color.PLUM, 
                CornerRadii.EMPTY, Insets.EMPTY)));
        hamPane.setFitToWidth(true);
        hamPane.setContent(vBox);
        hamPane.setStyle("-fx-background: rgb(255,255,255);\n "
                + "-fx-background-color: rgb(255,255,255)");
        
        vBox.getStyleClass().add("content_scene_left");
        vBox.getChildren().add(menu);
        vBox.getChildren().addAll(buttons);
        vBox.setFillWidth(true);
        vBox.prefHeightProperty().bind(stage.heightProperty().multiply(1.0));
        vBox.setBackground(new Background(new BackgroundFill(Color.PLUM, 
                CornerRadii.EMPTY, Insets.EMPTY)));        
        
        // Adjust the view for degree choosing
        VBox vbox2 = new VBox(10);
        ScrollPane degreeScroller = new ScrollPane();
        degreeScroller.setContent(vbox2);
        vbox2.getChildren().addAll(welcomeLabel);
        vbox2.setAlignment(Pos.CENTER);
        root.setLeft(hamPane);
        root.setCenter(degreeScroller);
        
        // Add degrees on below another as buttons
        allDegrees.entrySet().stream().map(entry -> {
            String degreeName = entry.getKey();
            Button degreeButton = new Button(degreeName);
            degreeButton.setFont(Font.font("Arial", 15));
            degreeButton.setPrefWidth(500);
            degreeButton.setStyle("-fx-background-color: #DDA0DD");
            degreeButton.setId(degreeName);
            degreeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    // Change scene to show modules and courses of the chosen 
                    // degree
                    String degreeId = entry.getValue().getDegreeId();
                    currentDegree = entry.getValue();
                    currentProgress = new Progress(currentDegree);
                    try {                 
                        CheckBoxTreeItem<String> rootDegree = 
                                new CheckBoxTreeItem<>(degreeName);
                        getSubModules(degreeId, rootDegree, true);
                        
                        tree = new TreeView<> (rootDegree);
                        tree.setBackground(new Background(new BackgroundFill(
                                new LinearGradient(0, 0, 1, 1, true,
                                        CycleMethod.NO_CYCLE, 
                                        new Stop(0, Color.BEIGE),
                                new Stop(1, Color.BISQUE)), 
                                CornerRadii.EMPTY, Insets.EMPTY)));
                        tree.setCellFactory(CheckBoxTreeCell
                                .<String>forTreeView());
                        
                        BorderPane treePane = new BorderPane();
                        treePane.setBackground(new Background(
                                new BackgroundFill(Color.PLUM, 
                            CornerRadii.EMPTY, Insets.EMPTY)));
                        
                        // Create a view for the progress
                        progressLabel = new Label();
                        Pair prog = currentProgress.getProgress();
                        progressLabel.setText("Tutkintoa suorittettu\n" + 
                                prog.getKey() + "/" + prog.getValue() + " op.");
                        progressLabel.setFont(Font.font("Arial", 15));
                        progressLabel.setPadding(new Insets(5));
                        
                        Button showCoursesBtn = 
                                new Button("Näytä suoritetut kurssit");
                        showCoursesBtn.setFont(Font.font("Arial", 15));
                        Label courseLabel = new Label();
                        courseLabel.setFont(Font.font("Arial", 15));
                        showCoursesBtn.setOnMouseClicked(event -> {
                            Set<String> courseSet = 
                                    currentProgress.getCompletedCourses()
                                            .keySet();
                            
                            String s = "";
                            for (var ele : courseSet) {
                                s = s + ele + "\n";
                            }
                            courseLabel.setText(s);
                            
                            Pair prog2 = currentProgress.getProgress();
                            progressLabel.setText("Tutkintoa suorittettu\n" + 
                                    prog2.getKey() + "/" + prog2.getValue() 
                                    + " op.");
                
                            showCoursesBtn.setText("Päivitä suoritetut kurssit");
                            showCoursesBtn.setFont(Font.font("Arial", 15));
                        });
                        
                        VBox vb = new VBox();
                        vb.getChildren().addAll(progressLabel, showCoursesBtn, 
                                courseLabel);
                        vb.prefHeightProperty().bind(stage.heightProperty()
                                .multiply(1.0));
                        vb.setBackground(new Background(new BackgroundFill(
                                Color.PLUM, CornerRadii.EMPTY, Insets.EMPTY)));
                        
                        ScrollPane progressPane = new ScrollPane();
                        progressPane.setContent(vb);
                        
                        
                        treePane.setRight(progressPane);
                        treePane.setLeft(hamPane);
                        treePane.setCenter(tree);
                        
                        accordionScene = new Scene(treePane);
                        
                    } catch (Exception ex) {
                        Logger.getLogger(App.class.getName()).log(Level.SEVERE, 
                                null, ex);
                    }
                    
                    stage.setScene(accordionScene);
                }
                
            });
            return degreeButton;            
        }).forEachOrdered(degreeButton -> {
            vbox2.getChildren().add(degreeButton);
        });

        stage.setScene(scene);
        stage.setOnCloseRequest((event) -> { Platform.exit();});
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                try {
                    writeJsonFile();
                } catch (IOException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, 
                            null, ex);
                }
            }
       
       });
        
    }

    public static void main(String[] args) {
        launch();
    }
}