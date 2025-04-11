package asoiafnexus.tournament.model;

import asoiafnexus.tournament.pairing.MakePairingsSerDe;
import asoiafnexus.tournament.pairing.RandomPairings;
import asoiafnexus.tournament.pairing.SortedPairings;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;
import java.util.stream.IntStream;

@JsonSerialize(using = MakePairingsSerDe.Serializer.class)
@JsonDeserialize(using = MakePairingsSerDe.Deserializer.class)
public interface MakePairings {
    String name();
    List<Pairing> makePairings(List<Participant> participants, List<Result> results);

    MakePairings RandomPairings = new RandomPairings();
    MakePairings SortedPairings = new SortedPairings();

    List<MakePairings> allStrategies = List.of(RandomPairings, SortedPairings);
}
