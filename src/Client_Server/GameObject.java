package Client_Server;

import Game.Card;
import Game.Player;

import java.io.Serializable;

/**
 * Created by camillo.schweizer on 23.10.2017.
 */
public class GameObject implements Serializable {
    private Player player;
    private Card card;
    private int action;


    //Action 0 = gespielt, Action 1 = gekauft
    public GameObject(Player player, Card card, int action){
        this.player = player;
        this.card = card;
        this.action = action;

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}