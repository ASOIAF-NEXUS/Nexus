package asoiafnexus.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class User {
  private final String username;
  private final double rating;
  private final List<String> matchHistory;
  private final int totalMatches;
  private final int winMatches;
  private final int loseMatches;
}
