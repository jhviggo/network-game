package bot;

import game.Main;
import game.Player;
import javafx.application.Platform;

import java.util.ArrayList;

public class Bot extends Thread {
    private int boardXSize;
    private int boardYSize;

    private int[] neighbourY = {-1, 0, 1, 0};
    private int[] neighbourX = {0, 1, 0, -1};

    private Node[][] nodes;
    private ArrayList<Node> openNodes;
    private ArrayList<Node> closedNodes;

    private ArrayList<Node> path;

    private Player enemy;
    private Player bot;

    private Main game;

    private boolean running;

    /**
     * Constructor to create the gameboard of nodes
     *
     * @param gameBoard the string array representing the board
     */
    public Bot(String[] gameBoard, Player bot, Player enemy, Main game) {
        this.bot = bot;
        this.enemy = enemy;
        this.game = game;
        running = true;
        path = new ArrayList<>();
        openNodes = new ArrayList<>();
        closedNodes = new ArrayList<>();
        nodes = new Node[20][20];
        transformMap(gameBoard);
    }

    public void stopRunning() {
        System.out.println("Stopping bot...");
        running = false;
    }

    /**
     * Main thread method
     */
    @Override
    public void run() {
        while (running) {
            clearNodes();
            findPath(
                    bot.getXpos(),
                    bot.getYpos(),
                    enemy.getXpos(),
                    enemy.getYpos());

            if (path.isEmpty()) {
                return;
            }

            Node nextPosition = path.get(0);

            if (nextPosition.x > bot.getXpos()) {
                Platform.runLater(() -> game.playerMoved(bot, nextPosition.x - bot.getXpos(), nextPosition.y - bot.getYpos(), "bot_right"));
            } else if (nextPosition.x < bot.getXpos()) {
                Platform.runLater(() -> game.playerMoved(bot, nextPosition.x - bot.getXpos(), nextPosition.y - bot.getYpos(), "bot_left"));
            } else if (nextPosition.y > bot.getYpos()) {
                Platform.runLater(() -> game.playerMoved(bot, nextPosition.x - bot.getXpos(), nextPosition.y - bot.getYpos(), "bot_down"));
            } else {
                Platform.runLater(() -> game.playerMoved(bot, nextPosition.x - bot.getXpos(), nextPosition.y - bot.getYpos(), "bot_up"));
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Clears all lists of nodes to recalculate optimal path
     */
    private void clearNodes() {
        path = new ArrayList<>();
        openNodes = new ArrayList<>();
        closedNodes = new ArrayList<>();
    }

    /**
     * Finds the shortest path between the bot and the target
     * using the A-star (A*) algorithm
     *
     * @param startX x-coordinate of the bot
     * @param startY y-coordinate of the bot
     * @param targetX x-coordinate of the target
     * @param targetY y-coordinate of the target
     */
    private void findPath(int startX, int startY, int targetX, int targetY) {
        Node startNode = nodes[startY][startX];
        Node targetNode = nodes[targetY][targetX];

        openNodes.add(startNode);

        while (openNodes.size() > 0) {
            Node currentNode = openNodes.get(0);

            for (int i = 0; i < openNodes.size(); i++) {
                if (openNodes.get(i).fCost() <= currentNode.fCost() && openNodes.get(i).hCost < currentNode.hCost) {
                    currentNode = openNodes.get(i);
                }
            }

            openNodes.remove(currentNode);
            closedNodes.add(currentNode);

            if (currentNode == targetNode) {
                retracePath(startNode, targetNode);
                return;
            }

            for (Node neighbour : getNeighbours(currentNode)) {
                if(!neighbour.walkable || closedNodes.contains(neighbour)) {
                    continue;
                }

                int newMovementCostToNeighbour = currentNode.gCost + getDistance(currentNode, neighbour);

                if (newMovementCostToNeighbour < neighbour.gCost || !openNodes.contains(neighbour)) {
                    neighbour.gCost = newMovementCostToNeighbour;
                    neighbour.hCost = getDistance(neighbour, targetNode);
                    neighbour.parent = currentNode;

                    if (!openNodes.contains(neighbour)) {
                        openNodes.add(neighbour);
                    }

                }

            }

        }
    }

    /**
     * Traverses the node parents to back-trace the shortest path
     *
     * @param startNode the position of the bot
     * @param endNode the target node
     */
    private void retracePath(Node startNode, Node endNode) {
        System.out.println("Hunting (" + startNode.x + "," + startNode.y + ") -> (" + endNode.x + "," + endNode.y + ")");

        ArrayList<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != startNode) {
            path.add(0, currentNode);
            currentNode = currentNode.parent;
        }

        this.path = path;
    }

    /**
     * Calculates the distance from the current node to the target node
     *
     * @param currentNode current node to evaluate
     * @param destinationNode target node we are trying to reach
     * @return the coordinate distance between the nodes
     */
    private int getDistance(Node currentNode, Node destinationNode) {
        return Math.abs((destinationNode.x - currentNode.x) + (destinationNode.y - currentNode.y));
    }

    /**
     * Transforms the array of strings that define the board into a multi-dimensional array of nodes
     *
     * @param board multi-dimensional array of nodes
     */
    private void transformMap(String[] board) {
        if (board == null || board.length == 0 || board[0] == null || board[0].length() == 0) {
            throw new IllegalArgumentException("Undefined game board");
        }

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[0].length(); x++) {
                if (board[y].charAt(x) == 'w') {
                    nodes[y][x] = new Node(x, y, false);
                } else {
                    nodes[y][x] = new Node(x, y, true);
                }
            }
        }

        boardXSize = board.length;
        boardYSize = board[0].length();
    }

    /**
     * Finds all neighbours to the current node. Neighbours are defined as non-diagonal touching fields
     *
     * @param node the current node to evaluate
     * @return zero to four nodes that touch the current node
     */
    private ArrayList<Node> getNeighbours(Node node) {
        ArrayList<Node> neighbours = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            if (node.x + neighbourX[i] >= 0
                    && node.x + neighbourX[i] < boardXSize
                    && node.y + neighbourY[i] >= 0
                    && node.y + neighbourY[i] < boardYSize) {
                neighbours.add(nodes[node.y + neighbourY[i]][ node.x + neighbourX[i]]);
            }

        }
        return neighbours;
    }

    /**
     * Node class to contain all fields on te board
     */
    private class Node {
        Node parent;
        boolean walkable;
        int x;
        int y;

        int gCost;
        int hCost;

        public Node(int x, int y, boolean walkable) {
            this.x = x;
            this.y = y;
            this.walkable = walkable;
        }

        public int fCost() {
            return gCost + hCost;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ") -> ";
        }
    }
}
