package com.cards.pokerhands.handrank;

import com.cards.pokerhands.Card;
import com.cards.pokerhands.CardRank;
import com.cards.pokerhands.Suit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FiveCardPokerHandRanker implements HandRanker {

    public HandRank findBestHandRank(List<Card> cards) {
        if (cards == null || cards.size() != 5) {
            throw new IllegalArgumentException("PokerHandRanker needs exactly 5 cards to make a poker ranked hand.");
        }
        boolean isFlush = true;
        boolean isStraight = true;
        Card firstCard = cards.get(0);
        int prevRank = firstCard.getRank().getValue();
        Suit suit = firstCard.getSuit();
        for (int i = 1; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.getSuit() != suit) {
                isFlush = false;
            }
            if (card.getRank().getValue() != --prevRank) {
                isStraight = false;
            }
            if (!isFlush && !isStraight) {
                break;
            }
        }
        if (isFlush && isStraight) {
            if (firstCard.getRank() == CardRank.ACE) {
                // Royal flush
                return new RoyalFlushHandRank(firstCard.getSuit());
            } else {
                // Straight flush
                return new StraightFlushHandRank(firstCard.getRank());
            }
        }
        if (isFlush){
            return new FlushHandRank(cards);
        }
        if (isStraight){
            return new StraightHandRank(firstCard.getRank());
        }
        Map<CardRank, Integer> rankCount = new TreeMap<CardRank, Integer>();
        for (Card card : cards) {
            CardRank rank = card.getRank();
            if (rankCount.containsKey(card.getRank())) {
                rankCount.put(rank, rankCount.get(rank) + 1);
            } else {
                rankCount.put(rank, 1);
            }
        }
        int size = rankCount.size();
        if (size == 5) {
            // High card
            return new HighCardHandRank(cards);
        }
        if (size == 4) {
            // 1 pair
            CardRank pair = null;
            List<CardRank> rest = new ArrayList<CardRank>(3);
            for (Card card : cards) {
                CardRank rank = card.getRank();
                if (rankCount.get(rank) > 1) {
                    pair = rank;
                } else {
                    rest.add(rank);
                }
            }
            return new OnePairHandRank(pair, rest);
        }
        int maxCount = 0;
        for (Integer count : rankCount.values()) {
            maxCount = Math.max(maxCount, count);
        }
        CardRank[][] countRank = new CardRank[4][2];
        for (Map.Entry<CardRank, Integer> entry : rankCount.entrySet()) {
            int index = entry.getValue() - 1;
            if (countRank[index][0] == null) {
                countRank[index][0] = entry.getKey();
            } else {
                countRank[index][1] = entry.getKey();
            }

        }
        if (size == 3) {
            if (maxCount == 3) {
                // 3 of a kind
                return new ThreeOfAKindHandRank(countRank[2][0]);
            } else {
                // 2 pair
                return new TwoPairHandRank(countRank[1][1], countRank[1][0], countRank[0][0]);
            }
        } else {
            if (maxCount == 4) {
                // 4 of a kind
                return new FourOfAKindHandRank(countRank[3][0]);
            } else {
                // full house
                return new FullHouseHandRank(countRank[2][0], countRank[1][0]);
            }
        }
    }
}
