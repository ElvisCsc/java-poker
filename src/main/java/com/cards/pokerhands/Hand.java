package com.cards.pokerhands;

import com.sprinthive.pokerhands.handrank.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

public class Hand implements Comparable<Hand> {

    private final List<Card> cards;
    private TreeMap<Integer, Integer> pairs;
    FiveCardPokerHandRanker op = new FiveCardPokerHandRanker();
    private HandRank handRank;

    public Hand(List<Card> cards) {
        if (cards == null) {
            cards = Collections.emptyList();
        }
        this.cards = new ArrayList<Card>(cards);
        Collections.sort(cards);
    }

    public int getNumberOfCards() {
        return cards.size();
    }
    public List<Card> getCards() {
        return cards;
    }

    public String describeHandRank() {
        Collections.reverse(cards);
        if (getNumberOfCards() == 5) {
            handRank = op.findBestHandRank(cards);
        } else {
            handRank = new NotRankableHandRanker(cards);
        }
       return handRank.describeHand();
    }

    private boolean isFlush() {
        for (int i = 0; i < 4; i++) {
            if (cards.get(i).getSuit() != cards.get(i+1).getSuit()) {
                return false;
            }
        }
        return true;
    }

    private boolean isConsecutive() {

        int[] values = new int[5];
        for (int i = 0; i < 5; i++) {
            values[i] = cards.get(i).getRank().getValue();
        }
        Arrays.sort(values);

        for (int i = 0; i < 4; i++) {
            if (values[i+1] - values[i] != 1) {
                return false;
            }
        }
        return true;
    }

    private void checkForPairs() {
        pairs = new TreeMap<Integer, Integer>();

        for (Card card : cards) {
            pairs.putIfAbsent(card.getRank().getValue(), 0);
            pairs.put(card.getRank().getValue(), pairs.get(card.getRank().getValue()) + 1);
        }
    }

    public String checkHandStrength() {
        if (isFlushHandRank()) {
            return "FH";
        } else if (isFourOfAKindHandRank()) {
            return "FOAKH";
        } else if (isFullHouseHandRank()) {
            return "FHH";
        } else if (isOnePairHandRank()) {
            return "OPH";
        }else if (isRoyalFlushHandRank()) {
            return "RFH";
        }else if (isStraightFlushHandRank()) {
            return "SFH";
        }else if (isStraightHandRank()) {
            return "SH";
        }else if (isThreeOfAKindHandRank()) {
            return "TOAKH";
        }else if (isTwoPairHandRank()) {
            return "TPH";
        } else {
            return "HCH";
        }
    }

    private boolean isFlushHandRank() {
        if (isFlush() && !isConsecutive())
            return true;
        return false;
    }

    private boolean isFourOfAKindHandRank() {
        checkForPairs();
        if (pairs != null && pairs.size() == 2) {
            for(Map.Entry<Integer, Integer> entry : pairs.entrySet()) {
                if ( entry.getValue() == 1 || entry.getValue() == 4) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFullHouseHandRank() {
        checkForPairs();
        if (pairs != null && pairs.size() == 2) {
            for(Map.Entry<Integer, Integer> entry : pairs.entrySet()) {
                if ( entry.getValue() != 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOnePairHandRank() {
        checkForPairs();
        if (pairs != null && pairs.size() == 4) {
            return true;
        }
        return false;
    }

    private boolean isRoyalFlushHandRank() {
        checkForPairs();
        if (pairs != null && isFlush() && isConsecutive()) {
            if (pairs.firstKey() == 10)
                return true;
        }
        return false;
    }

    private boolean isStraightFlushHandRank() {
        checkForPairs();
        if (pairs != null && isFlush() && isConsecutive()) {
            if (pairs.firstKey() != 10)
                return true;
        }
        return false;
    }

    private boolean isStraightHandRank() {
        HashMap<Suit, Integer> mapSuits = new HashMap<Suit, Integer>();

        for (Card card : cards) {
            mapSuits.putIfAbsent(card.getSuit(), 0);
            mapSuits.put(card.getSuit(), mapSuits.get(card.getSuit()) + 1);
        }
        checkForPairs();
        if (isConsecutive())
            return true;
        return false;
    }

    private boolean isThreeOfAKindHandRank() {
        boolean twoPair = isTwoPairHandRank();
        boolean threePair = false;
        for (Map.Entry<Integer, Integer> entry : pairs.entrySet()) {
            if ( entry.getValue() == 3){
                threePair = true;
                break;
            }
        }
        return twoPair && threePair;
    }

    private boolean isTwoPairHandRank() {
        checkForPairs();
        if (pairs != null && pairs.size() == 3) {
            return true;
        }
        return false;
    }

    public int compareTo(Hand other) {
        HashMap<String, Integer> strength = new HashMap<String, Integer>();

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

        int leftHand;
        int rightHand;

        if (getNumberOfCards() == 5 && other.getNumberOfCards() == 5) {
           leftHand = strength.get(this.checkHandStrength());
           rightHand = strength.get(other.checkHandStrength());
        }  else if (getNumberOfCards() != 5 && other.getNumberOfCards() == 5) {
            leftHand = 0;
            rightHand = strength.get(other.checkHandStrength());
        }  else if (getNumberOfCards() == 5 && other.getNumberOfCards() != 5) {
            leftHand = strength.get(this.checkHandStrength());
            rightHand = 0;
        } else {
            leftHand = 0;
            rightHand = 0;
        }


        if ( leftHand == rightHand) {
            if (leftHand == 0)
                return 0;

            int leftValues = 0;
            int rightValues = 0;
            List<Card> rightCards = other.getCards();

            for (int i = 0; i < 5; i++) {
                leftValues += cards.get(i).getRank().getValue();
                rightValues += rightCards.get(i).getRank().getValue();
            }

            if ( leftValues > rightValues) {
                return 1;
            }

            if ( leftValues < rightValues) {
                return -1;
            }

            return 0;
        } else if ( leftHand > rightHand) {
            return 1;
        } else {
            return -1;
        }
    }
}

