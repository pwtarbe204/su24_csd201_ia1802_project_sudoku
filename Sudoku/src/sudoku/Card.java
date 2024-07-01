/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author SU24_CSD201_IA1802_GROUP4
 */
public class Card extends JLabel {

    int row, col, value, num;
    public MouseListener mouseClicked;
    public Sudoku parent;
    boolean[][] fixedNum;

    public Card(Sudoku parent, int row, int col, int value) {
        this.parent = parent;
        this.row = row;
        this.col = col;
        this.value = value;
        this.fixedNum = parent.fixedNum();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        this.mouseClicked = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardClicked();
            }
        };
        this.addMouseListener(mouseClicked);
        updateFace();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK.darker());
        g2.setStroke(new BasicStroke(5));

        // Vẽ đường viền ngoài cùng cho lưới Sudoku
        if (row == 0) {
            g2.drawLine(0, 0, getWidth(), 0); // Đỉnh trên cùng
        }
        if (col == 0) {
            g2.drawLine(0, 0, 0, getHeight()); // Bên trái
        }
        if (row == 8) {
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1); // Đáy dưới cùng
        }
        if (col == 8) {
            g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight()); // Bên phải
        }

        g2.setColor(Color.BLACK.darker());
        g2.setStroke(new BasicStroke(7));
        // Vẽ đường viền cho các khu vực 3x3
        if (row == 2 || row == 5) {
            g2.drawLine(0, getHeight(), getWidth(), getHeight()); // Đường viền ngang
        }
        if (col == 2 || col == 5) {
            g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight()); // Đường viền dọc
        }
    }

    public void cardClicked() {
        System.out.println("row: " + row);
        System.out.println("col: " + col);

        if (!fixedNum[row][col]) {
            //parent.highlight(row, col);
            int selectedValue = parent.getSelectedNumber(); // Lấy số từ nút bên phải
            if (selectedValue != -1) {
                if (parent.solveBoard[row][col] == selectedValue) {
                    turnOn(selectedValue);
                    parent.board[row][col] = selectedValue;
                    fixedNum[row][col] = true; // Đánh dấu là cố định
                    System.out.println(parent.checkRow(row) + " " + parent.checkCol(col));
                    if (parent.checkRow(row)) {
                        parent.updateScore(parent._ONEROWCOL);
                    }
                    if (parent.checkCol(col)) {
                        parent.updateScore(parent._ONEROWCOL);
                    }
                    if (parent.checkBaba(row, col)) {
                        parent.updateScore(parent._ONEBABA);
                    }
                    if (!parent.checkRow(row) && !parent.checkCol(col) && !parent.checkBaba(row, col)) {
                        parent.updateScore(parent._ONECELL);
                    }
                    parent.updateInformation();
                    parent.highlight(row, col);
                    parent.numberButtonClicked(-1);
                } else {
                    parent.highlight(row, col);
                    parent.numberButtonClicked(-1);
                    parent.updateInformation();
                    parent.isWrong();
                    if (parent.countWrong < 5) {
                        JOptionPane.showMessageDialog(parent, "The value is invalid! You have '" + (5 - parent.countWrong) + "' live!");
                    }
                }
            }
        } else {
            parent.highlight(row, col);
            parent.numberButtonClicked(-1);
        }
    }

    private ImageIcon getFace() {
        return new ImageIcon(getClass().getResource("/image/" + value + ".png"));
    }

    public void updateFace() {
        this.setIcon(getFace());
    }

    public boolean isSelect() {
        return true;
    }

    public void turnOn(int value) {
        this.value = value + 10;
        updateFace();
    }
}
