package cleancode.minesweeper.asis;

import java.util.Random;
import java.util.Scanner;

public class MinesweeperGame {

    public static final int BOARD_ROW_SIZE = 8;
    public static final int BOARD_COL_SIZE = 10;
    public static final String OPEN_CELL_SIGN = "■";
    private static final String[][] BOARD = new String[BOARD_ROW_SIZE][BOARD_COL_SIZE];
    private static final Integer[][] NEARBY_LAND_MINE_COUNTS = new Integer[BOARD_ROW_SIZE][BOARD_COL_SIZE];
    private static final boolean[][] LAND_MINES = new boolean[BOARD_ROW_SIZE][BOARD_COL_SIZE];
    public static final int LAND_MINE_COUNT = 10;
    public static final String FLAG_SIGN = "⚑";
    public static final String LAND_MINE_SIGN = "☼";
    public static final String CLOSED_CELL_SIGN = "□";
    private static int gameStatus = 0; // 0: 게임 중, 1: 승리, -1: 패배

    public static void main(String[] args) {
        showGameStartComments();
        
        Scanner scanner = new Scanner(System.in);
        initializeGame();
        while (true) {
            showBoard();

            if (doesUserWinTheGame()) {
                System.out.println("지뢰를 모두 찾았습니다. GAME CLEAR!");
                break;
            }
            if (doseUserLoseTheGame()) {
                System.out.println("지뢰를 밟았습니다. GAME OVER!");
                break;
            }

            String cellInput = getCellInput(scanner);
            String userActionInput = getUserActionInput(scanner);
            int selectedRow = getSelectedRow(cellInput);
            int selectedColumn = getSelectedColumn(cellInput);
            if (doesUserWantToPlantFlag(userActionInput)) {
                BOARD[selectedRow][selectedColumn] = FLAG_SIGN;
                checkIfGameIsOver();
            } else if (doeseUserWantToOpenCell(userActionInput)) {
                if (isLandMineCell(selectedRow, selectedColumn)) {
                    BOARD[selectedRow][selectedColumn] = LAND_MINE_SIGN;
                    changeGameStatusToLose();
                    continue;
                } else {
                    open(selectedRow, selectedColumn);
                }
                checkIfGameIsOver();
            } else {
                System.out.println("잘못된 번호를 선택하셨습니다.");
            }
        }
    }

    private static void changeGameStatusToLose() {
        gameStatus = -1;
    }

    private static boolean isLandMineCell(int selectedRow, int selectedColumn) {
        return LAND_MINES[selectedRow][selectedColumn];
    }

    private static boolean doeseUserWantToOpenCell(String userActionInput) {
        return userActionInput.equals("1");
    }

    private static boolean doesUserWantToPlantFlag(String userActionInput) {
        return userActionInput.equals("2");
    }

    private static int getSelectedRow(String cellInput) {
        char cellInputRow = cellInput.charAt(1);
        return convertRowFrom(cellInputRow);
    }

    private static int getSelectedColumn(String cellInput) {
        char cellInputColumn = cellInput.charAt(0);
        return convertColFrom(cellInputColumn);
    }

    private static String getUserActionInput(Scanner scanner) {
        System.out.println("선택한 셀에 대한 행위를 선택하세요. (1: 오픈, 2: 깃발 꽂기)");
        return scanner.nextLine();
    }

    private static String getCellInput(Scanner scanner) {
        System.out.println("선택할 좌표를 입력하세요. (예: a1)");
        return scanner.nextLine();
    }

    private static boolean doseUserLoseTheGame() {
        return gameStatus == -1;
    }

    private static boolean doesUserWinTheGame() {
        return gameStatus == 1;
    }

    private static void checkIfGameIsOver() {
        if (isAllCellOpen()) {
            changeGameStatusToWin();
        }
    }

    private static void changeGameStatusToWin() {
        gameStatus = 1;
    }

    private static boolean isAllCellOpen() {
        boolean isAllOpen = true;
        for (int row = 0; row < BOARD_ROW_SIZE; row++) {
            for (int column = 0; column < BOARD_COL_SIZE; column++) {
                if (BOARD[row][column].equals(CLOSED_CELL_SIGN)) {
                    isAllOpen = false;
                }
            }
        }
        return isAllOpen;
    }

    private static int convertRowFrom(char cellInputRow) {
        return Character.getNumericValue(cellInputRow) - 1;
    }

    private static int convertColFrom(char cellInputColumn) {
        switch (cellInputColumn) {
            case 'a':
                return 0;
            case 'b':
                return 1;
            case 'c':
                return 2;
            case 'd':
                return 3;
            case 'e':
                return 4;
            case 'f':
                return 5;
            case 'g':
                return 6;
            case 'h':
                return 7;
            case 'i':
                return 8;
            case 'j':
                return 9;
            default:
                return -1;
        }
    }

    private static void showBoard() {
        System.out.println("   a b c d e f g h i j");
        for (int row = 0; row < BOARD_ROW_SIZE; row++) {
            System.out.printf("%d  ", row + 1);
            for (int column = 0; column < BOARD_COL_SIZE; column++) {
                System.out.print(BOARD[row][column] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void initializeGame() {
        for (int row = 0; row < BOARD_ROW_SIZE; row++) {
            for (int column = 0; column < BOARD_COL_SIZE; column++) {
                BOARD[row][column] = CLOSED_CELL_SIGN;
            }
        }
        for (int i = 0; i < LAND_MINE_COUNT; i++) {
            int col = new Random().nextInt(LAND_MINE_COUNT);
            int row = new Random().nextInt(LAND_MINE_COUNT);
            LAND_MINES[row][col] = true;
        }
        for (int row = 0; row < BOARD_ROW_SIZE; row++) {
            for (int column = 0; column < BOARD_COL_SIZE; column++) {
                int count = 0;
                if (!isLandMineCell(row, column)) {
                    if (row - 1 >= 0 && column - 1 >= 0 && isLandMineCell(row - 1, column - 1)) {
                        count++;
                    }
                    if (row - 1 >= 0 && isLandMineCell(row - 1, column)) {
                        count++;
                    }
                    if (row - 1 >= 0 && column + 1 < BOARD_COL_SIZE && isLandMineCell(row - 1, column + 1)) {
                        count++;
                    }
                    if (column - 1 >= 0 && isLandMineCell(row, column - 1)) {
                        count++;
                    }
                    if (column + 1 < BOARD_COL_SIZE && isLandMineCell(row, column + 1)) {
                        count++;
                    }
                    if (row + 1 < BOARD_ROW_SIZE && column - 1 >= 0 && isLandMineCell(row + 1, column - 1)) {
                        count++;
                    }
                    if (row + 1 < BOARD_ROW_SIZE && isLandMineCell(row + 1, column)) {
                        count++;
                    }
                    if (row + 1 < BOARD_ROW_SIZE && column + 1 < BOARD_COL_SIZE && isLandMineCell(row + 1, column + 1)) {
                        count++;
                    }
                    NEARBY_LAND_MINE_COUNTS[row][column] = count;
                    continue;
                }
                NEARBY_LAND_MINE_COUNTS[row][column] = 0;
            }
        }
    }

    private static void showGameStartComments() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("지뢰찾기 게임 시작!");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    private static void open(int row, int col) {
        if (row < 0 || row >= BOARD_ROW_SIZE || col < 0 || col >= BOARD_COL_SIZE) {
            return;
        }
        if (!BOARD[row][col].equals(CLOSED_CELL_SIGN)) {
            return;
        }
        if (isLandMineCell(row, col)) {
            return;
        }
        if (NEARBY_LAND_MINE_COUNTS[row][col] != 0) {
            BOARD[row][col] = String.valueOf(NEARBY_LAND_MINE_COUNTS[row][col]);
            return;
        } else {
            BOARD[row][col] = OPEN_CELL_SIGN;
        }
        open(row - 1, col - 1);
        open(row - 1, col);
        open(row - 1, col + 1);
        open(row, col - 1);
        open(row, col + 1);
        open(row + 1, col - 1);
        open(row + 1, col);
        open(row + 1, col + 1);
    }

}
