/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author SU24_CSD201_IA1802_GROUP4
 */
public class ButtonImage extends JLabel {

    int value;
    public Sudoku parent;
    private final MouseAdapter mouseClicked;

    public ButtonImage(Sudoku parent, int value) {
        this.parent = parent;
        this.value = value;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.mouseClicked = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardClicked();
            }

        };
        this.addMouseListener(mouseClicked);
        updateFace();
    }

    private ImageIcon getFace() {
        switch (value) {
            case 0:
                return new ImageIcon(getClass().getResource("/image/btImage.png"));
            case 1:
                return new ImageIcon(getClass().getResource("/image/btImageAboutUs.png"));
            case 2:
                return new ImageIcon(getClass().getResource("/image/btImageHowToPlay.png"));
            case 3:
                return new ImageIcon(getClass().getResource("/image/btImageQuit.png"));
            case 4:
                return new ImageIcon(getClass().getResource("/image/btImageSignUp.png"));
            case 5:
                return new ImageIcon(getClass().getResource("/image/btImageSignIn.png"));
            case 6:
                return new ImageIcon(getClass().getResource("/image/homeIcon.png"));
            case 7:
                return new ImageIcon(getClass().getResource("/image/nextIcon.png"));
            case 8:
                return new ImageIcon(getClass().getResource("/image/backIcon.png"));
            case 9:
                return new ImageIcon(getClass().getResource("/image/btImagePlay.png"));
            case 10:
                return new ImageIcon(getClass().getResource("/image/btImageBack.png"));
        }
        return null;
    }

    public void updateFace() {
        this.setIcon(getFace());
    }

    private void cardClicked() {
        System.out.println(value);
        if (parent.isMenu == true) {
            System.out.println("menu: " + parent.isMenu);
            if (value == 0) {
                parent.pnlPrepare.setVisible(false);
                parent.information();
            } else if (value == 1) {
                parent.pnlPrepare.setVisible(false);
                parent.showAboutUs();
            } else if (value == 2) {
                parent.pnlPrepare.setVisible(false);
                parent.showRules();
            }
            if (value == 3) {
                System.exit(0);
            }
        } else if (parent.isInfo == true) {
            System.out.println("info: " + parent.isInfo);
            if (value == 4) {
                parent.pnlPrepare.setVisible(false);
                parent.newGame();
            } else if (value == 5) {
                parent.pnlPrepare.setVisible(false);
                parent.oldGame();
            } else if (value == 3) {
                parent.isInfo = false;
                parent.isMenu = true;
                parent.pnlPrepare.setVisible(false);
                parent.menuSudoku();
            }
        } else if (parent.isAboutUS == true || parent.isRule == true) {
            System.out.println("us: " + parent.isAboutUS);
            System.out.println("rule: " + parent.isRule);
            if (value == 6) {
                parent.pnlPrepare.setVisible(false);
                parent.menuSudoku();
            } else if (value == 7) {
                parent.currentImageIndex++;
                if (parent.isAboutUS == true) {
                    parent.currentImageIndex = parent.currentImageIndex % 6;
                    pageNum("/6");
                } else {
                    parent.currentImageIndex = parent.currentImageIndex % 9;
                    pageNum("/9");
                }
                parent.updateImage();
            } else if (value == 8) {
                if (parent.currentImageIndex > 0) {
                    parent.currentImageIndex--;
                    if (parent.isAboutUS == true) {
                        pageNum("/6");
                    } else {
                        pageNum("/9");
                    }
                    parent.updateImage();
                }
            }
        } else if (parent.isNewGame == true) {
             System.out.println("newgame: " + parent.isNewGame);
            if (value == 9) {
                parent.idName();
            } else if (value == 10) {
                System.out.println("back");
                parent.pnlPrepare.setVisible(false);
                parent.information();
                
            }
        }  else if (parent.isOldGame == true) {
             System.out.println("newgame: " + parent.isNewGame);
            if (value == 9) {
                parent.id();
            } else if (value == 10) {
                System.out.println("back");
                parent.pnlPrepare.setVisible(false);
                parent.information();
                
            }
        }
    }

    private void pageNum(String str) {
        parent.label.setText((parent.currentImageIndex + 1) + str + "");
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
