package openlicht.pixelpaint;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DrawingElements extends VBox {

	private Canvas canvas;
	private ColorPicker cp;
	private static WritableImage image;
	private int pixelSize = 20;
	private Rectangle primaryPaint, secondaryPaint;

	private boolean drawWithPrimaryColor = true;
	private boolean drawWithSecondaryColor = false;
	
	private static Color startColor;

	public enum STATE {
		DRAW, GET_COLOR, FILL
	}

	private STATE state = STATE.DRAW;

	private List<Button> buttons = new ArrayList<Button>();

	public DrawingElements(Canvas canvas) {
		this.canvas = canvas;
		image = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
		buttons.add(new Button("Pencil"));
		buttons.add(new Button("ColorPicker"));
		buttons.add(new Button("Paint Pucket"));
		cp = new ColorPicker();

		setElementsOnAction(buttons, cp);

		initPaint();

		getChildren().addAll(primaryPaint, secondaryPaint);
	}

	private void setElementsOnAction(List<Button> buttons, ColorPicker cp) {
		buttons.get(0).setOnAction(e -> setState(STATE.DRAW));
		buttons.get(1).setOnAction(e -> setState(STATE.GET_COLOR));
		buttons.get(2).setOnAction(e -> setState(STATE.FILL));

		cp.setOnAction(e -> {
			if (drawWithPrimaryColor)
				primaryPaint.setFill(cp.getValue());
			if (drawWithSecondaryColor)
				secondaryPaint.setFill(cp.getValue());
		});

		getChildren().add(cp);

		for (int i = 0; i < buttons.size(); i++) {
			getChildren().add(buttons.get(i));
		}
	}

	private void initPaint() {
		primaryPaint = new Rectangle(50, 50);
		primaryPaint.setFill(Color.WHITE);
		primaryPaint.setOnMouseClicked(e -> {
			drawWithPrimaryColor = true;
			drawWithSecondaryColor = false;
			primaryPaint.setStroke(Color.BLACK);
			secondaryPaint.setStroke(secondaryPaint.getFill());

		});
		secondaryPaint = new Rectangle(50, 50);
		secondaryPaint.setFill(Color.WHITE);
		secondaryPaint.setOnMouseClicked(e -> {
			drawWithPrimaryColor = false;
			drawWithSecondaryColor = true;
			secondaryPaint.setStroke(Color.BLACK);
			primaryPaint.setStroke(primaryPaint.getFill());

		});
	}

	private void setState(STATE state) {
		this.state = state;
		setActionToMouseEvent();
	}

	private void setActionToMouseEvent() {
		switch (state) {
		case DRAW:
			canvas.setOnMousePressed(e -> draw(e, canvas.getGraphicsContext2D()));
			canvas.setOnMouseDragged(e -> draw(e, canvas.getGraphicsContext2D()));
			break;
		case GET_COLOR:
			canvas.setOnMousePressed(e -> pickColor(e));
			canvas.setOnMouseDragged(e -> pickColor(e));
			break;
		case FILL:
			canvas.setOnMousePressed(e -> fillWithColor(e, canvas.getGraphicsContext2D()));
			canvas.setOnMouseDragged(e -> fillWithColor(e, canvas.getGraphicsContext2D()));
			break;
		default:
			break;

		}
	}
	
	private void fillWithColor(MouseEvent e, GraphicsContext gc){
		System.out.println("FILL FILL FILL FILL! :D");
		
	}
	
	private boolean matchStartColor(int x, int y){
		PixelReader reader = canvas.snapshot(null, image).getPixelReader();
		return startColor.equals(reader.getColor(x,  y));
	}

	private void pickColor(MouseEvent e) {
		canvas.snapshot(null, image);
		cp.setValue(image.getPixelReader().getColor((int) e.getX(), (int) e.getY()));
		if (e.isPrimaryButtonDown())
			primaryPaint.setFill(cp.getValue());
		if (e.isSecondaryButtonDown())
			secondaryPaint.setFill(cp.getValue());
	}

	private void draw(MouseEvent e, GraphicsContext gc) {
		int mouseX = pixelSize * (int) ((Math.floor(e.getX() / (canvas.getWidth() / 64))));
		int mouseY = pixelSize * (int) ((Math.floor(e.getY() / (canvas.getHeight() / 36))));
		if (e.isPrimaryButtonDown() && !e.isSecondaryButtonDown()) {
			gc.setFill(primaryPaint.getFill());
			gc.fillRect(mouseX, mouseY, pixelSize, pixelSize);
		} else if (e.isSecondaryButtonDown() && !e.isPrimaryButtonDown()) {
			gc.setFill(secondaryPaint.getFill());
			gc.fillRect(mouseX, mouseY, pixelSize, pixelSize);
		}
	}
}
