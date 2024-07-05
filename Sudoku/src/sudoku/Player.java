/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;
import java.io.Serializable;

/**
 *
 * @author Bui Quoc Tin - CE180935
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private int score;

    public Player(String id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return id+"#"+name+"#"+score+"\n";
    }
    
}
