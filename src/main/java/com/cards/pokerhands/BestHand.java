package com.cards.pokerhands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class BestHand {
    private final List<Card> cards;
    public ArrayList<Hand> hands = new ArrayList<Hand>();
    HashMap<String, Integer> strength = new HashMap<String, Integer>();

    public BestHand(List<Card> cards) {
        if (cards == null || cards.size() < 5 || cards.size() > 52) {
            throw new IllegalArgumentException("The best single 5-card require atleast 5 cards and at most 52 cards.");
        }
        this.cards = new ArrayList<Card>(cards);
        Collections.sort(this.cards);
        strength.put("RFH", 10);
        strength.put("SFH", 9);
        strength.put("FOAKH", 8);
        strength.put("FHH", 7);
        strength.put("FH", 6);
        strength.put("SH", 5);
        strength.put("TOAKH", 4);
        strength.put("TPH", 3);
        strength.put("OPH", 2);
        strength.put("HCH", 1);
    }

    public String singleBestHand() {
        TreeMap<Integer, String> pairs = new TreeMap<Integer, String>();
        combinations(5, 0, new Card[5]);
        for (Hand hand : hands) {
            pairs.put(strength.get(hand.checkHandStrength()), hand.describeHandRank());
        }
        return pairs.get(pairs.lastKey());
    }

    public void combinations(int handSize, int startPosition, Card[] result){
        if (handSize == 0){
            hands.add(new Hand(Arrays.asList(result)));
            return;
        }

        for (int i = startPosition; i <= cards.size()-handSize; i++){
            result[5 - handSize] = cards.get(i);
            combinations(handSize-1, i+1, result);
        }
    }
}