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
import java.awt.HeadlessException;
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

    public Card() {
    }
    
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
        parent.setNumberLock(row, col);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                parent.getRowCol[i][j] = false;
            }
        }
        System.out.println("row: " + row);
        System.out.println("col: " + col);
        setHighLight(row, col);
        parent.getRowCol[row][col] = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(parent.getRowCol[i][j] + " ");
            }
            System.out.println("");
        }
    }
    
    private void setHighLight(int row, int col) {
        parent.getRowCol[row][col] = true;
        parent.highlight(row, col);
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
}
