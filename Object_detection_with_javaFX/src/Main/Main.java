package Main;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

public class Main extends Application
{
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			//loads in GUI from GUI.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI.fxml"));
			Pane root = (Pane) loader.load();
			root.setStyle("-fx-background-color: whitesmoke;");
			Scene scene = new Scene(root, 800, 800);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			FXController controller = loader.getController();
			
			controller.Start();

			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we)
				{
					System.exit(0);
				}
			}));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		// load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		launch(args);
	}
}
