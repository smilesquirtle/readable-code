package cleancode.minesweeper.asis;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class MinesweeperGame {

    public static final int BOARD_ROW_SIZE = 8;
    public static final int BOARD_COL_SIZE = 10;
    public static final String OPEN_CELL_SIGN = "■";
    public static final Scanner SCANNER = new Scanner(System.in);
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
        initializeGame();

        while (true) {
            try {
                showBoard();

                if (doesUserWinTheGame()) {
                    System.out.println("지뢰를 모두 찾았습니다. GAME CLEAR!");
                    break;
                }
                if (doseUserLoseTheGame()) {
                    System.out.println("지뢰를 밟았습니다. GAME OVER!");
                    break;
                }

                String cellInput = getCellInput();
                String userActionInput = getUserActionInput();
                actOnCell(cellInput, userActionInput);
            } catch (AppException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("프로그램에 문제가 생겼습니다.");
            }
        }
    }

    private static void actOnCell(String cellInput, String userActionInput) {
        int selectedRow = getSelectedRow(cellInput);
        int selectedColumn = getSelectedColumn(cellInput);

        if (doesUserWantToPlantFlag(userActionInput)) {
            BOARD[selectedRow][selectedColumn] = FLAG_SIGN;
            checkIfGameIsOver();
            return;
        }

        if (doeseUserWantToOpenCell(userActionInput)) {
            if (isLandMineCell(selectedRow, selectedColumn)) {
                BOARD[selectedRow][selectedColumn] = LAND_MINE_SIGN;
                changeGameStatusToLose();
                return;
            }

            open(selectedRow, selectedColumn);
            checkIfGameIsOver();
            return;
        }

        throw new AppException("잘못된 번호를 선택하셨습니다.");
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

    private static String getUserActionInput() {
        System.out.println("선택한 셀에 대한 행위를 선택하세요. (1: 오픈, 2: 깃발 꽂기)");
        return SCANNER.nextLine();
    }

    private static String getCellInput() {
        System.out.println("선택할 좌표를 입력하세요. (예: a1)");
        return SCANNER.nextLine();
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
        return Arrays.stream(BOARD)
                .flatMap(Arrays::stream)
                .noneMatch(CLOSED_CELL_SIGN::equals);
    }

    private static int convertRowFrom(char cellInputRow) {
        int row = Character.getNumericValue(cellInputRow) - 1;
        if (row >= BOARD_ROW_SIZE) {
            throw new AppException("잘못된 입력입니다.");
        }

        return row;
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
                throw new AppException("잘못된 입력입니다.");
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
            int row = new Random().nextInt(BOARD_ROW_SIZE);
            int col = new Random().nextInt(BOARD_COL_SIZE);
            LAND_MINES[row][col] = true;
        }

        for (int row = 0; row < BOARD_ROW_SIZE; row++) {
            for (int column = 0; column < BOARD_COL_SIZE; column++) {
                if (isLandMineCell(row, column)) {
                    NEARBY_LAND_MINE_COUNTS[row][column] = 0;
                    continue;
                }

                int count = countNearbyLandMines(row, column);
                NEARBY_LAND_MINE_COUNTS[row][column] = count;
            }
        }
    }

    private static int countNearbyLandMines(int row, int column) {
        int count = 0;
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
        return count;
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
