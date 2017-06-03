package openlicht.pixelpaint;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Window extends Application {

	private Canvas canvas;
	private GraphicsContext gc;
	private int width, height;
	public int pixelSize = 20;

	public void start(Stage stage) throws Exception {
		width = 640 * 2;
		height = 360 * 2;
		stage.setTitle("PixelPaint");
		createGUI(width, height, stage);
	}

	private void createGUI(int width, int height, Stage stage) {
		canvas = new Canvas(width, height);
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, width, height);
		DrawingElements drawingElements = new DrawingElements(canvas);
		BorderPane root = new BorderPane();
		root.setCenter(canvas);
		root.setLeft(drawingElements);
		root.setTop(createMenu());
		Scene scene = new Scene(root, 1280, 720);
		stage.setScene(scene);
		stage.show();
	}

	private MenuBar createMenu() {
		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu("File");
		MenuItem newCanvas = new MenuItem("New");
		MenuItem loadFile = new MenuItem("Load...");
		MenuItem saveFile = new MenuItem("Save...");
		newCanvas.setOnAction(e-> newCanvas());
		loadFile.setOnAction(e -> loadPicture());
		saveFile.setOnAction(e -> savePicture());
		menuFile.getItems().addAll(newCanvas, loadFile, saveFile);
		menuBar.getMenus().add(menuFile);

		return menuBar;
	}
	
	private void newCanvas(){
//		gc.setFill(Color.WHITE);
//		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	private void savePicture() {
		FileChooser fc = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new ExtensionFilter("png files (*.png)", "*.png");
		fc.getExtensionFilters().add(extFilter);

		File file = fc.showSaveDialog(null);

		if (file != null) {
			try {
				WritableImage image = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
				canvas.snapshot(null, image);
				RenderedImage rImage = SwingFXUtils.fromFXImage(image, null);
				ImageIO.write(rImage, "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadPicture() {
		FileChooser fc = new FileChooser();

		FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("All Images", "*.*");
		fc.getExtensionFilters().addAll(extFilterPNG);

		File file = fc.showOpenDialog(null);

		try {
			if (file == null)
				return;
			BufferedImage bi = ImageIO.read(file);
			Image image = SwingFXUtils.toFXImage(bi, null);
			gc.drawImage(image, 0, 0);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
