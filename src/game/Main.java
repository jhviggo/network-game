package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

public class Main extends Application {

	public static final int size = 20;
	public static final int scene_height = size * 20 + 100;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right,hero_left,hero_up,hero_down;

	public static Player me;
	public static List<Player> players = new ArrayList<Player>();


	private String myName = "HC";

	private GameClient gameClient = new GameClient(this);

	private Label[][] fields;
	private TextArea scoreList;

	private  String[] board = {    // 20x20
			"wwwwwwwwwwwwwwwwwwww",
			"w        ww        w",
			"w w  w  www w  w  ww",
			"w w  w   ww w  w  ww",
			"w  w               w",
			"w w w w w w w  w  ww",
			"w w     www w  w  ww",
			"w w     w w w  w  ww",
			"w   w w  w  w  w   w",
			"w     w  w  w  w   w",
			"w ww ww        w  ww",
			"w  w w    w    w  ww",
			"w        ww w  w  ww",
			"w         w w  w  ww",
			"w        w     w  ww",
			"w  w              ww",
			"w  w www  w w  ww ww",
			"w w      ww w     ww",
			"w   w   ww  w      w",
			"wwwwwwwwwwwwwwwwwwww"
	};


	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();

			GridPane boardGrid = new GridPane();

			image_wall  = new Image(getClass().getResourceAsStream("Image/wall4.png"),size,size,false,false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"),size,size,false,false);

			hero_right  = new Image(getClass().getResourceAsStream("Image/heroRight.png"),size,size,false,false);
			hero_left   = new Image(getClass().getResourceAsStream("Image/heroLeft.png"),size,size,false,false);
			hero_up     = new Image(getClass().getResourceAsStream("Image/heroUp.png"),size,size,false,false);
			hero_down   = new Image(getClass().getResourceAsStream("Image/heroDown.png"),size,size,false,false);

			fields = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default: throw new Exception("Illegal field value: "+board[j].charAt(i) );
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);

			// client/server calls
			gameClient.start();

			// grid setup
			grid.add(mazeLabel,  0, 0);
			grid.add(scoreLabel, 1, 0);
			grid.add(boardGrid,  0, 1);
			grid.add(scoreList,  1, 1);

			Scene scene = new Scene(grid,scene_width,scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP:    playerMoved(0,-1,"up");    break;
				case DOWN:  playerMoved(0,+1,"down");  break;
				case LEFT:  playerMoved(-1,0,"left");  break;
				case RIGHT: playerMoved(+1,0,"right"); break;
				default: break;
				}
			});

            // Setting up standard players

			Player harry = new Player("Harry",14,15,"up");
			players.add(harry);
			fields[14][15].setGraphic(new ImageView(hero_up));


			scoreList.setText(getScoreList());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void addEnemyPlayer(final String name, final int x, final int y) {
		if (players.stream().noneMatch(player -> player.name.equalsIgnoreCase(name))) {
			System.out.println("Adding player: " + name);
			Player p = new Player(name, x, y, "down");
			players.add(p);
			Platform.runLater(() -> fields[x][y].setGraphic(new ImageView(hero_up)));

			if (p.name.equalsIgnoreCase(myName)) {
				me = p;
			}
		}
	}

	public void playerMoved(int delta_x, int delta_y, String direction) {
		me.direction = direction;
		int x = me.getXpos(),y = me.getYpos();

		if (board[y+delta_y].charAt(x+delta_x)=='w') {
			me.addPoints(-1);
		}
		else {
			Player p = getPlayerAt(x+delta_x,y+delta_y);
			if (p!=null) {
              me.addPoints(10);
              p.addPoints(-10);
			} else {
				me.addPoints(1);

				//fields[x][y].setGraphic(new ImageView(image_floor));
				x+=delta_x;
				y+=delta_y;

				//if (direction.equals("right")) {
				//	fields[x][y].setGraphic(new ImageView(hero_right));
				//};
				//if (direction.equals("left")) {
				//	fields[x][y].setGraphic(new ImageView(hero_left));
				//};
				//if (direction.equals("up")) {
				//	fields[x][y].setGraphic(new ImageView(hero_up));
				//};
				//if (direction.equals("down")) {
				//	fields[x][y].setGraphic(new ImageView(hero_down));
				//};

				me.setXpos(x);
				me.setYpos(y);
			}
		}
		gameClient.send("MOVE " + myName + " " + x + " " + y + " " + direction);
		scoreList.setText(getScoreList());
	}

	public String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p+"\r\n");
		}
		return b.toString();
	}

	public Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}

	public List<Player> getPlayers() {
		return new ArrayList<>(players);
	}

	public void remotePlayerMoved(Player player) {
		int x = player.getXpos();
		int y = player.getYpos();
		String direction = player.getDirection();
		if (direction.equals("right")) {
		    if (board[y].charAt(x-1) != 'w') {
				Platform.runLater(() -> fields[x - 1][y].setGraphic(new ImageView(image_floor)));
			}
			Platform.runLater(() -> fields[x][y].setGraphic(new ImageView(hero_right)));
		} else if (direction.equals("left")) {
			if (board[y].charAt(x+1) != 'w') {
				Platform.runLater(() -> fields[x+1][y].setGraphic(new ImageView(image_floor)));
			}
			Platform.runLater(() -> fields[x][y].setGraphic(new ImageView(hero_left)));
		} else if (direction.equals("up")) {
			if (board[y+1].charAt(x) != 'w') {
				Platform.runLater(() -> fields[x][y+1].setGraphic(new ImageView(image_floor)));
			}
			Platform.runLater(() -> fields[x][y].setGraphic(new ImageView(hero_up)));
		} else if (direction.equals("down")) {
			if (board[y-1].charAt(x) != 'w') {
				Platform.runLater(() -> fields[x][y-1].setGraphic(new ImageView(image_floor)));
			}
			Platform.runLater(() -> fields[x][y].setGraphic(new ImageView(hero_down)));
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

