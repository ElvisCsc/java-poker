package com.cards.pokerhands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> cards = new ArrayList<Card>(52);

    public Deck() {
        for (Suit suit : Suit.values()) {
            for (CardRank rank : CardRank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(cards);
    }

    public synchronized int getNumberOfCards() {
        return cards.size();
    }

    public synchronized Card[] pick(int numberOfCards)
    {
        Card[] hand = new Card[numberOfCards];

        for (int i = 0; i < numberOfCards; i++) {

            if (getNumberOfCards() > 0) {
                hand[i] =cards.remove(0);
            } else {
                break;
            }
        }
        return hand;
    }
}
