/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Panel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SU24_CSD201_IA1802_GROUP4
 */
public class Sudoku extends javax.swing.JFrame {

    public static final int NUM_ROW = 9;
    public static final int NUM_COL = 9;
    private static final int SIZE = 9;
    private static final int EMPTY = 0;
    public static final int SIZE_CARD = 64;
    public static final int _EASY = 42;
    public static final int _MEDIUM = 52;
    public static final int _HARD = 62;
    public static final int _BONUS = 100;
    public final int _ONECELL = 25;
    public final int _ONEROWCOL = 50;
    public final int _ONEBABA = 75;

    Thread timer;
    int timeCount;
    Card[][] map;
    Graphics2D g;
    int mau;
    int countWrong = 0;

    private int selectedNumber = -1;
    private int selectedRow = -1;
    public int[][] board;
    public boolean[][] fixedNum;
    public boolean[][] highlight;
    public int[][] solveBoard;
    boolean isEasy = true, isMedium = false, isHard = false, isBtRanking = false, isTheFirstTime = false, isSolve = true;
    ArrayList<Player> listPlayer;
    Player user = null;
    public int score = 0;

    public void numberButtonClicked(int number) {
        selectedNumber = number;
    }

    public int getSelectedNumber() {
        return selectedNumber;
    }

    public void createBoard() {
        board = new int[SIZE][SIZE];
        solveBoard = new int[SIZE][SIZE];
        fixedNum = new boolean[SIZE][SIZE];
        highlight = new boolean[SIZE][SIZE];
        listPlayer = new ArrayList<>();
    }

    public int[][] getBoard() {
        return board;
    }

    public boolean isValid(int row, int col, int num) {
        // Kiểm tra hàng
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num) {
                return false;
            }
        }

        // Kiểm tra cột
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == num) {
                return false;
            }
        }

        // Kiểm tra ô 3x3
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    // SOLVE ===================================================================
    public boolean solve() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (solveBoard[row][col] == 0) {
                    for (int num = 1; num <= SIZE; num++) {
                        if (isValid(row, col, num)) {
                            solveBoard[row][col] = num;
                            if (solve()) {
                                return true;
                            }
                            solveBoard[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }
    // SOLVE ===================================================================

    private boolean fillBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == EMPTY) {
                    Random rand = new Random();
                    int[] numbers = rand.ints(1, 10).distinct().limit(9).toArray();

                    for (int num : numbers) {
                        if (isValid(row, col, num)) {
                            board[row][col] = num;
                            if (fillBoard()) {
                                return true;
                            }
                            board[row][col] = EMPTY;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public void generateSudoku(int level) {
        fillBoard();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                solveBoard[i][j] = board[i][j];
            }
        }
        removeElements(level);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != 0) {
                    fixedNum[i][j] = true;
                }
            }
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("");
        }
    }

    private void removeElements(int level) {
        Random rand = new Random();
        int removed = 0;
        while (removed < level) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            if (board[row][col] != EMPTY) {
                board[row][col] = EMPTY;
                removed++;
            }
        }
    }

    public void printBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private String int2time(int time) {
        return String.format("%02d:%02d:%02d", time / 3600, (time / 60) % 60, time % 60);
    }

    public void runTimer() {
        timer = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        ++timeCount;
                        lbTime.setText(int2time(timeCount));
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(Sudoku.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        };
        timer.start();
    }

    public void reset() {
        countWrong = 0;
        lbMistake.setText("0/5");
        score = 0;
        lbScore.setText("0");
        timeCount = 0; // Đặt lại biến đếm thời gian về 0
        lbTime.setText(int2time(timeCount)); // Cập nhật giao diện hiển thị thời gian
        bt1.setEnabled(true);
        bt2.setEnabled(true);
        bt3.setEnabled(true);
        bt4.setEnabled(true);
        bt5.setEnabled(true);
        bt6.setEnabled(true);
        bt7.setEnabled(true);
        bt8.setEnabled(true);
        bt9.setEnabled(true);
        btSolve.setEnabled(false);
    }

    public void isWrong() {
        ++countWrong;
        lbMistake.setText(countWrong + "/5");
    }

    public void updateScore(int score) {
        this.score += score;
        lbScore.setText(this.score + "");
    }

    public int checkId(String id) {
        for (int i = 0; i < listPlayer.size(); i++) {
            if (listPlayer.get(i).getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1;
    }

    public void genarateBoard() {
        try (Scanner sc = new Scanner(new File("information.txt"))) {
            int n = Integer.parseInt(sc.nextLine());
            for (int i = 0; i < n; i++) {
                String[] str = sc.nextLine().split("#");
                String id = str[0];
                String name = str[1];
                int score = Integer.parseInt(str[2]);
                listPlayer.add(new Player(id, name, score));
            }
            for (Player s : listPlayer) {
                System.out.println("Id: " + s.getId() + " Name: " + s.getName() + " Score: " + s.getScore());
            }
        } catch (Exception e) {
            System.out.println("#File is not exist!");
        }
        if (!isTheFirstTime) {
            int check = JOptionPane.showConfirmDialog(this, "Is this the first time, right?");
            System.out.println(check);
            if (check == 0) {
                String id;
                while (true) {
                    id = JOptionPane.showInputDialog("Hello, please enter id to save your information!");
                    if (checkId(id) == -1) {
                        break;
                    } else {
                        JOptionPane.showMessageDialog(this, "The id is exist in list!");
                    }
                }
                String name = JOptionPane.showInputDialog("Hello, please enter your name");
                int score = 0;
                user = new Player(id, name, score);
                listPlayer.add(user);
            } else if (check == 1) {
                String id;
                while (true) {
                    id = JOptionPane.showInputDialog("Enter your ID: ");
                    int index = checkId(id);
                    if (index != -1) {
                        user = listPlayer.get(index);
                        break;
                    } else {
                        JOptionPane.showMessageDialog(this, "Wrong ID!");
                    }
                }
            }
            for (Player u : listPlayer) {
                System.out.println(u.getId() + "#" + u.getName() + "#" + u.getScore());
            }
        }

        map = new Card[NUM_ROW][NUM_COL];

        pnlBoard.setLayout(new GridLayout(NUM_ROW, NUM_COL));
        pnlBoard.removeAll();
        pnlBoard.revalidate();
        pnlBoard.repaint();

        if (isEasy) {
            generateSudoku(_EASY);
            isEasy = true;
            btEasy.setEnabled(false);
            btMedium.setEnabled(true);
            btHard.setEnabled(true);
        } else if (isMedium) {
            generateSudoku(_MEDIUM);
        } else if (isHard) {
            generateSudoku(_HARD);
        }

        for (int i = 0; i < NUM_ROW; i++) {
            for (int j = 0; j < NUM_COL; j++) {
                mau = board[i][j];
                map[i][j] = new Card(this, i, j, mau);
                pnlBoard.add(map[i][j]);
            }
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.printf("%5d", solveBoard[i][j]);
            }
            System.out.println("");
        }
        isTheFirstTime = true;
        System.out.println(user.getId() + "#" + user.getName() + "#" + user.getScore());
    }

    public void highlight(int row, int col) {
        pnlBoard.setLayout(new GridLayout(NUM_ROW, NUM_COL));
        pnlBoard.removeAll();
        pnlBoard.revalidate();
        pnlBoard.repaint();

        highlight[row][col] = true;

        for (int i = 0; i < NUM_ROW; i++) {
            for (int j = 0; j < NUM_COL; j++) {
                if (i == row) {
                    if (j == col) {
                        continue;
                    }
                    highlight[i][j] = true;
                } else if (j == col) {
                    if (i == row) {
                        continue;
                    }
                    highlight[i][j] = true;
                }
            }
        }
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (i == row && j == col) {
                    continue;
                }
                highlight[i][j] = true;

            }
        }
        for (int i = 0; i < NUM_ROW; i++) {
            for (int j = 0; j < NUM_COL; j++) {
                if ((i == row && j == col) || (board[i][j] == board[row][col] && board[i][j] != 0)) {
                    mau = board[i][j] + 20;
                } else {
                    if (highlight[i][j]) {
                        mau = board[i][j] + 10;
                    } else {
                        mau = board[i][j];
                    }
                }

                map[i][j] = new Card(this, i, j, mau);
                pnlBoard.add(map[i][j]);
            }
        }
        for (int i = 0; i < NUM_ROW; i++) {
            for (int j = 0; j < NUM_COL; j++) {

                highlight[i][j] = false;
            }
        }
    }

    public boolean checkRow(int row) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean checkCol(int col) {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean checkBaba(int row, int col) {
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == 0) {
                    return false;
                }

            }
        }
        return true;
    }

    public Sudoku() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        createBoard();
        genarateBoard();
        runTimer();
    }

    public Card[][] getMap() {
        return map;
    }

    public void setMap(Card[][] map) {
        this.map = map;
    }

    public boolean[][] fixedNum() {
        return fixedNum;
    }

    public boolean isWin() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (!fixedNum[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
public void updateInformation() {
    if (isWin()) {
        user.setScore(user.getScore() + this.score);
        int option = JOptionPane.showOptionDialog(this, "Congratulations! You are the champion!\nDo you want to continue playing?", "Winner!",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Next", "No"}, "Next");
        if (option == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(this, "Your total score: " + user.getScore());
            updateTable();
            bt1.setEnabled(false);
            bt2.setEnabled(false);
            bt3.setEnabled(false);
            bt4.setEnabled(false);
            bt5.setEnabled(false);
            bt6.setEnabled(false);
            bt7.setEnabled(false);
            bt8.setEnabled(false);
            bt9.setEnabled(false);
            timer.stop();
        } else {
            createBoard();
            genarateBoard();
            reset();
            runTimer();
        }
    }
    if (isLose()) {
        JOptionPane.showMessageDialog(this, "Oh noo! You are loser!");
        bt1.setEnabled(false);
        bt2.setEnabled(false);
        bt3.setEnabled(false);
        bt4.setEnabled(false);
        bt5.setEnabled(false);
        bt6.setEnabled(false);
        bt7.setEnabled(false);
        bt8.setEnabled(false);
        bt9.setEnabled(false);
        timer.stop();
    }
}
    public void showSolve() {
        pnlBoard.setLayout(new GridLayout(NUM_ROW, NUM_COL));
        pnlBoard.removeAll();
        pnlBoard.revalidate();
        pnlBoard.repaint();

        if (isSolve) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == 0) {
                        mau = solveBoard[i][j] + 30;
                    } else {
                        mau = solveBoard[i][j];
                    }
                    map[i][j] = new Card(this, i, j, mau);
                    pnlBoard.add(map[i][j]);
                }
            }
        } else {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    mau = board[i][j];
                    map[i][j] = new Card(this, i, j, mau);
                    pnlBoard.add(map[i][j]);
                }
            }
        }

    }

public void updateTable() {
    listPlayer.sort((o2, o1) -> Integer.compare(o1.getScore(), o2.getScore()));
    String text = String.format("%-5s %-20s %-10s\n", "Rank", "Name", "Score");
    text += "-----------------------------------\n";
    int dem = 1;
    for (Player p : listPlayer) {
        text += String.format(" %-5d%-20s %-10s\n", dem++, p.getName(), p.getScore());
        text += "-----------------------------------\n";
    }
    lbRanking.setText(text);
}

    public boolean isLose() {
        if (countWrong == 4) {
            btSolve.setEnabled(true);
            return true;
        }
        return false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jMenu1 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        pnlBoard = new javax.swing.JPanel();
        pnlRanking = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lbRanking = new javax.swing.JTextArea();
        pnlMenu = new javax.swing.JPanel();
        btNewGame = new javax.swing.JButton();
        bt2 = new javax.swing.JButton();
        bt1 = new javax.swing.JButton();
        bt3 = new javax.swing.JButton();
        bt6 = new javax.swing.JButton();
        bt5 = new javax.swing.JButton();
        bt4 = new javax.swing.JButton();
        bt8 = new javax.swing.JButton();
        bt7 = new javax.swing.JButton();
        bt9 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lbMistake = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lbScore = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lbTime = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btEasy = new javax.swing.JButton();
        btMedium = new javax.swing.JButton();
        btHard = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btSolve = new javax.swing.JButton();
        btRanking = new javax.swing.JButton();
        btRanking1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jMenu1.setText("jMenu1");

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sudoku");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setLocation(new java.awt.Point(15, 35));

        pnlBoard.setBackground(new java.awt.Color(255, 255, 255));
        pnlBoard.setForeground(new java.awt.Color(153, 204, 255));
        pnlBoard.setPreferredSize(new java.awt.Dimension(576, 576));

        pnlRanking.setBackground(new java.awt.Color(204, 204, 255));

        jLabel8.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 36)); // NOI18N
        jLabel8.setText("Total Score");

        lbRanking.setEditable(false);
        lbRanking.setColumns(20);
        lbRanking.setFont(new java.awt.Font("Monospaced", 0, 24)); // NOI18N
        lbRanking.setRows(5);
        jScrollPane1.setViewportView(lbRanking);

        javax.swing.GroupLayout pnlRankingLayout = new javax.swing.GroupLayout(pnlRanking);
        pnlRanking.setLayout(pnlRankingLayout);
        pnlRankingLayout.setHorizontalGroup(
            pnlRankingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRankingLayout.createSequentialGroup()
                .addGap(226, 226, 226)
                .addComponent(jLabel8)
                .addContainerGap(208, Short.MAX_VALUE))
            .addGroup(pnlRankingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        pnlRankingLayout.setVerticalGroup(
            pnlRankingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRankingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlBoardLayout = new javax.swing.GroupLayout(pnlBoard);
        pnlBoard.setLayout(pnlBoardLayout);
        pnlBoardLayout.setHorizontalGroup(
            pnlBoardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlRanking, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlBoardLayout.setVerticalGroup(
            pnlBoardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBoardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlRanking, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlRanking.getAccessibleContext().setAccessibleParent(this);

        pnlMenu.setBackground(new java.awt.Color(255, 255, 255));
        pnlMenu.setPreferredSize(new java.awt.Dimension(364, 576));

        btNewGame.setBackground(new java.awt.Color(102, 153, 255));
        btNewGame.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btNewGame.setForeground(new java.awt.Color(255, 255, 255));
        btNewGame.setText("New Game");
        btNewGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNewGameActionPerformed(evt);
            }
        });

        bt2.setBackground(new java.awt.Color(255, 255, 255));
        bt2.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 48)); // NOI18N
        bt2.setForeground(new java.awt.Color(153, 153, 153));
        bt2.setText("2");
        bt2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        bt2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bt2.setSelected(true);
        bt2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt2ActionPerformed(evt);
            }
        });

        bt1.setBackground(new java.awt.Color(255, 255, 255));
        bt1.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 48)); // NOI18N
        bt1.setForeground(new java.awt.Color(153, 153, 153));
        bt1.setText("1");
        bt1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 13), new java.awt.Color(255, 255, 255))); // NOI18N
        bt1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bt1.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        bt1.setSelected(true);
        bt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt1ActionPerformed(evt);
            }
        });

        bt3.setBackground(new java.awt.Color(255, 255, 255));
        bt3.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 48)); // NOI18N
        bt3.setForeground(new java.awt.Color(153, 153, 153));
        bt3.setText("3");
        bt3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        bt3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bt3.setSelected(true);
        bt3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt3ActionPerformed(evt);
            }
        });

        bt6.setBackground(new java.awt.Color(255, 255, 255));
        bt6.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 48)); // NOI18N
        bt6.setForeground(new java.awt.Color(153, 153, 153));
        bt6.setText("6");
        bt6.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        bt6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bt6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt6ActionPerformed(evt);
            }
        });

        bt5.setBackground(new java.awt.Color(255, 255, 255));
        bt5.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 48)); // NOI18N
        bt5.setForeground(new java.awt.Color(153, 153, 153));
        bt5.setText("5");
        bt5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        bt5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bt5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt5ActionPerformed(evt);
            }
        });

        bt4.setBackground(new java.awt.Color(255, 255, 255));
        bt4.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 48)); // NOI18N
        bt4.setForeground(new java.awt.Color(153, 153, 153));
        bt4.setText("4");
        bt4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        bt4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bt4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt4ActionPerformed(evt);
            }
        });

        bt8.setBackground(new java.awt.Color(255, 255, 255));
        bt8.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 48)); // NOI18N
        bt8.setForeground(new java.awt.Color(153, 153, 153));
        bt8.setText("8");
        bt8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        bt8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bt8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt8ActionPerformed(evt);
            }
        });

        bt7.setBackground(new java.awt.Color(255, 255, 255));
        bt7.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 48)); // NOI18N
        bt7.setForeground(new java.awt.Color(153, 153, 153));
        bt7.setText("7");
        bt7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        bt7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bt7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt7ActionPerformed(evt);
            }
        });

        bt9.setBackground(new java.awt.Color(255, 255, 255));
        bt9.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 48)); // NOI18N
        bt9.setForeground(new java.awt.Color(153, 153, 153));
        bt9.setText("9");
        bt9.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        bt9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bt9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt9ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 153, 153));
        jLabel2.setText("Mistake");

        lbMistake.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbMistake.setText("0/5");

        jLabel3.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setText("Score");

        lbScore.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbScore.setText("0");

        jLabel4.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 153, 153));
        jLabel4.setText("Time");

        lbTime.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbTime.setText("00:00:00");

        jLabel1.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        jLabel1.setText("Difficulty:");

        btEasy.setBackground(new java.awt.Color(204, 255, 204));
        btEasy.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        btEasy.setForeground(new java.awt.Color(153, 153, 153));
        btEasy.setText("Easy");
        btEasy.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btEasy.setFocusCycleRoot(true);
        btEasy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEasyActionPerformed(evt);
            }
        });

        btMedium.setBackground(new java.awt.Color(255, 255, 153));
        btMedium.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        btMedium.setForeground(new java.awt.Color(153, 153, 153));
        btMedium.setText("Medium");
        btMedium.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btMediumActionPerformed(evt);
            }
        });

        btHard.setBackground(new java.awt.Color(255, 204, 204));
        btHard.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        btHard.setForeground(new java.awt.Color(153, 153, 153));
        btHard.setText("Hard");
        btHard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btHardActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(153, 153, 153));
        jLabel5.setText("Solve");

        jLabel6.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 153, 153));
        jLabel6.setText("Ranking");

        btSolve.setBackground(new java.awt.Color(255, 255, 255));
        btSolve.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        btSolve.setForeground(new java.awt.Color(153, 153, 153));
        btSolve.setText("Solve");
        btSolve.setEnabled(false);
        btSolve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSolveActionPerformed(evt);
            }
        });

        btRanking.setBackground(new java.awt.Color(255, 255, 255));
        btRanking.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        btRanking.setForeground(new java.awt.Color(153, 153, 153));
        btRanking.setText("Rank");
        btRanking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRankingActionPerformed(evt);
            }
        });

        btRanking1.setBackground(new java.awt.Color(255, 255, 255));
        btRanking1.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        btRanking1.setForeground(new java.awt.Color(153, 153, 153));
        btRanking1.setText("Exit");
        btRanking1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRanking1ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(153, 153, 153));
        jLabel7.setText("Exit");

        javax.swing.GroupLayout pnlMenuLayout = new javax.swing.GroupLayout(pnlMenu);
        pnlMenu.setLayout(pnlMenuLayout);
        pnlMenuLayout.setHorizontalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMenuLayout.createSequentialGroup()
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlMenuLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlMenuLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(7, 7, 7)
                                .addComponent(lbMistake)
                                .addGap(63, 63, 63)
                                .addComponent(jLabel5))
                            .addGroup(pnlMenuLayout.createSequentialGroup()
                                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlMenuLayout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(24, 24, 24)
                                        .addComponent(lbScore))
                                    .addGroup(pnlMenuLayout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(30, 30, 30)
                                        .addComponent(lbTime)))
                                .addGap(25, 25, 25)
                                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btRanking, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btRanking1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btSolve, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnlMenuLayout.createSequentialGroup()
                        .addGap(0, 22, Short.MAX_VALUE)
                        .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(pnlMenuLayout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pnlMenuLayout.createSequentialGroup()
                                            .addComponent(bt4, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(bt5, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(bt6, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(btNewGame, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlMenuLayout.createSequentialGroup()
                                                .addComponent(bt7, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(bt8, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(bt9, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlMenuLayout.createSequentialGroup()
                                    .addComponent(bt1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(bt2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(bt3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlMenuLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btEasy)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btMedium)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btHard, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(22, 22, 22))
        );
        pnlMenuLayout.setVerticalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMenuLayout.createSequentialGroup()
                        .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlMenuLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jLabel2))
                            .addGroup(pnlMenuLayout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(lbMistake)))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btSolve)
                        .addComponent(jLabel5)))
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMenuLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel3))
                    .addGroup(pnlMenuLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(lbScore))
                    .addGroup(pnlMenuLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(btRanking))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMenuLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel4))
                    .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRanking1)
                        .addComponent(jLabel7)
                        .addComponent(lbTime)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btEasy)
                    .addComponent(jLabel1)
                    .addComponent(btMedium)
                    .addComponent(btHard))
                .addGap(11, 11, 11)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bt2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bt4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt5, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt6, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bt7, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt8, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt9, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btNewGame, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlBoard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlBoard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btHardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btHardActionPerformed
        // TODO add your handling code here:
        isHard = true;
        btHard.setEnabled(false);
        isEasy = false;
        btEasy.setEnabled(true);
        isMedium = false;
        btMedium.setEnabled(true);
    }//GEN-LAST:event_btHardActionPerformed

    private void btMediumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btMediumActionPerformed
        // TODO add your handling code here:
        isMedium = true;
        btMedium.setEnabled(false);
        isEasy = false;
        btEasy.setEnabled(true);
        isHard = false;
        btHard.setEnabled(true);
    }//GEN-LAST:event_btMediumActionPerformed

    private void btEasyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEasyActionPerformed
        // TODO add your handling code here:
        isEasy = true;
        btEasy.setEnabled(false);
        isMedium = false;
        btMedium.setEnabled(true);
        isHard = false;
        btHard.setEnabled(true);
    }//GEN-LAST:event_btEasyActionPerformed

    private void bt9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt9ActionPerformed
        // TODO add your handling code here:
        mau = 9;
        numberButtonClicked(mau);
        System.out.println("9 is selected");
    }//GEN-LAST:event_bt9ActionPerformed

    private void bt7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt7ActionPerformed
        // TODO add your handling code here:
        mau = 7;
        numberButtonClicked(mau);
        System.out.println("7 is selected");
    }//GEN-LAST:event_bt7ActionPerformed

    private void bt8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt8ActionPerformed
        // TODO add your handling code here:
        mau = 8;
        numberButtonClicked(mau);
        System.out.println("8 is selected");
    }//GEN-LAST:event_bt8ActionPerformed

    private void bt4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt4ActionPerformed
        // TODO add your handling code here:
        mau = 4;
        numberButtonClicked(mau);
        System.out.println("4 is selected");
    }//GEN-LAST:event_bt4ActionPerformed

    private void bt5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt5ActionPerformed
        // TODO add your handling code here:
        mau = 5;
        numberButtonClicked(mau);
        System.out.println("5 is selected");
    }//GEN-LAST:event_bt5ActionPerformed

    private void bt6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt6ActionPerformed
        // TODO add your handling code here:
        mau = 6;
        numberButtonClicked(mau);
        System.out.println("6 is selected");
    }//GEN-LAST:event_bt6ActionPerformed

    private void bt3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt3ActionPerformed
        // TODO add your handling code here:
        mau = 3;
        numberButtonClicked(mau);
        System.out.println("3 is selected");
    }//GEN-LAST:event_bt3ActionPerformed

    private void bt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt1ActionPerformed
        // TODO add your handling code here:
        mau = 1;
        numberButtonClicked(mau);
        System.out.println("1 is selected");
    }//GEN-LAST:event_bt1ActionPerformed

    private void bt2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt2ActionPerformed
        // TODO add your handling code here:
        mau = 2;
        numberButtonClicked(mau);
        System.out.println("2 is selected");
    }//GEN-LAST:event_bt2ActionPerformed

    private void btNewGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNewGameActionPerformed
        // TODO add your handling code here:
    createBoard();
    genarateBoard();
    reset();
    runTimer(); 
    }//GEN-LAST:event_btNewGameActionPerformed

    private void btRankingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRankingActionPerformed
        // TODO add your handling code here:
        this.add(pnlRanking);
        // pnlRanking.add(lbRanking);

        pnlRanking.setLocation(15, 30);

        if (isBtRanking == false) {
            isBtRanking = true;

            updateTable();
            pnlRanking.setVisible(true);
            //lbRanking.setVisible(true);
            pnlBoard.setVisible(false);
            btNewGame.setEnabled(false);
        } else {
            isBtRanking = false;
            pnlRanking.setVisible(false);
            //lbRanking.setVisible(false);
            pnlBoard.setVisible(true);
            btNewGame.setEnabled(true);
        }


    }//GEN-LAST:event_btRankingActionPerformed

    private void btRanking1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRanking1ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btRanking1ActionPerformed

    private void btSolveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSolveActionPerformed
        // TODO add your handling code here:
        showSolve();
        if (isSolve) {
            isSolve = false;
        } else {
            isSolve = true;
        }
    }//GEN-LAST:event_btSolveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Sudoku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Sudoku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Sudoku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Sudoku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Sudoku().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt1;
    private javax.swing.JButton bt2;
    private javax.swing.JButton bt3;
    private javax.swing.JButton bt4;
    private javax.swing.JButton bt5;
    private javax.swing.JButton bt6;
    private javax.swing.JButton bt7;
    private javax.swing.JButton bt8;
    private javax.swing.JButton bt9;
    private javax.swing.JButton btEasy;
    private javax.swing.JButton btHard;
    private javax.swing.JButton btMedium;
    private javax.swing.JButton btNewGame;
    private javax.swing.JButton btRanking;
    private javax.swing.JButton btRanking1;
    private javax.swing.JButton btSolve;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbMistake;
    private javax.swing.JTextArea lbRanking;
    private javax.swing.JLabel lbScore;
    private javax.swing.JLabel lbTime;
    private javax.swing.JPanel pnlBoard;
    private javax.swing.JPanel pnlMenu;
    private javax.swing.JPanel pnlRanking;
    // End of variables declaration//GEN-END:variables
}
