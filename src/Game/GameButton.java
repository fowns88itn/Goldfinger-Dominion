package Game;

import javafx.scene.control.Button;

import java.io.Serializable;

/**
 * Created by camillo.schweizer on 13.10.2017.
 *
 * Jeweils ein  Konstruktor für Handkarten und Feldkarten. Es wird ein Kartenobjekt mitgegeben und der Style anhand
 * vom Namen festgelegt
 */
public class GameButton extends Button implements Serializable {
    Card card;
    int amount;

    public GameButton() {
    }

    public GameButton(Card card) {
        this.card = card;
        this.getStyleClass().add("mediumButton");
        this.getStyleClass().add(card.getName());

    }

    public GameButton(Card card, int amount) {
        this.card = card;
        this.amount = amount;
        this.getStyleClass().add("smallButton");
        this.getStyleClass().add(card.getName()+"Small");
        this.setText(""+amount);
    }

    public Card getCard() {
        return card;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
