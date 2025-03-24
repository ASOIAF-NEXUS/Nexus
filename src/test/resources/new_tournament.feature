Feature: A Tournament Organizer creates a new event

  Background: All of these users have signed up
    Given The following users have signed up
      | username                         |
      | tournament-organizer@testing.com |
      | player1@testing.com              |
      | player2@testing.com              |
      | player3@testing.com              |
      | player4@testing.com              |
      | player5@testing.com              |
      | player6@testing.com              |
      | player7@testing.com              |
      | player8@testing.com              |

  Scenario: Creating the event
    Given a user "tournament-organizer@testing.com"
    When a tournament is created with the information
      | name             | description | location                         | datetime                 |
      | Winter is Coming |             | 123 Main St. Cleveland, OH 44144 | 2025-01-01T09:00:00.000Z |
    Then a tournament is in the list of all tournaments
      | name             | description | location                         | datetime                 |
      | Winter is Coming |             | 123 Main St. Cleveland, OH 44144 | 2025-01-01T09:00:00.000Z |

  Scenario: Updating event details
    Given a user "tournament-organizer@testing.com"
    And a tournament named "Winter is Coming"
    When tournament details for "Winter is Coming" are updated
      | name             | description     | location                          | datetime                 |
      | Winter is Coming | Details Updated | 987 First St. Cleveland, OH 44144 | 2025-03-15T09:30:00.000Z |
    Then a tournament is in the list of all tournaments
      | name             | description     | location                          | datetime                 |
      | Winter is Coming | Details Updated | 987 First St. Cleveland, OH 44144 | 2025-03-15T09:30:00.000Z |

  Scenario: Players Signup For An Event
    Given a tournament named "Winter is Coming"
    When a player signs up for the tournament "Winter is Coming"
      | username            |
      | player1@testing.com |
      | player2@testing.com |
      | player3@testing.com |
      | player4@testing.com |
      | player5@testing.com |
      | player6@testing.com |
      | player7@testing.com |
      | player8@testing.com |
    Then the tournament "Winter is Coming" shows a list of participants
      | username            |
      | player1@testing.com |
      | player2@testing.com |
      | player3@testing.com |
      | player4@testing.com |
      | player5@testing.com |
      | player6@testing.com |
      | player7@testing.com |
      | player8@testing.com |

  Scenario: Players Withdraw From An Event
    Given a tournament named "Winter is Coming"
    When a player withdraws from the tournament "Winter is Coming"
      | username            |
      | player1@testing.com |
      | player2@testing.com |
      | player3@testing.com |
      | player4@testing.com |
    Then the tournament "Winter is Coming" shows a list of participants
      | username            |
      | player5@testing.com |
      | player6@testing.com |
      | player7@testing.com |
      | player8@testing.com |

  Scenario: Starting An Event
    Given a user "tournament-organizer@testing.com"
    And a tournament named "Winter is Coming"
    When the tournament "Winter is Coming" is started
    Then 2 random pairings are created for the tournament "Winter is Coming"
    And any player can no longer sign up for the tournament "Winter is Coming"

  Scenario: Override Pairings
    Given a user "tournament-organizer@testing.com"
    And a tournament named "Winter is Coming"
    And the tournament "Winter is Coming" is already started
    When pairings are provided to the tournament "Winter is Coming"
      | p1                  | p2                  |
      | player5@testing.com | player8@testing.com |
      | player7@testing.com | player6@testing.com |
    Then the latest pairings for the tournament "Winter is Coming" are
      | p1                  | p2                  |
      | player5@testing.com | player8@testing.com |
      | player7@testing.com | player6@testing.com |