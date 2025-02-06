package asoiafnexus.user.model;

import java.util.List;

public record User(
    String username,
    double rating,
    List<String> matchHistory,
    int totalMatches,
    int winMatches,
    int loseMatches
) {
}
