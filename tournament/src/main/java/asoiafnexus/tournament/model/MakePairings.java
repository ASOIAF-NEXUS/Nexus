package asoiafnexus.tournament.model;

import asoiafnexus.tournament.pairing.RandomPairings;
import asoiafnexus.tournament.pairing.SortedPairings;

import java.util.*;
import java.util.stream.IntStream;

public interface MakePairings {
    List<Pairing> makePairings(List<Participant> participants);

    MakePairings RandomPairings = new RandomPairings();
    MakePairings SortedPairings = new SortedPairings();
}
