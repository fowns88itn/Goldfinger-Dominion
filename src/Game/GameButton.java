package Game;

import javafx.scene.control.Button;

/**
 * Created by camillo.schweizer on 13.10.2017.
 */
public class GameButton extends Button {
    Card card;
    int amount;


    public GameButton(Card card) {
        this.card = card;
        this.getStyleClass().add(card.getCardName());
    }

    public GameButton(Card card, int amount) {
        this.card = card;
        this.amount = amount;
        this.getStyleClass().add(card.getCardName()+"Small");
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
