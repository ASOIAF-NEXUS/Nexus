Feature: A Tournament Organizer creates a new event

  Scenario: Creating the event
    Given a user "tournament-organizer@testing.com"
    When a tournament is created with the information
      | name             | description | location                         | datetime                 |
      | Winter is Coming |             | 123 Main St. Cleveland, OH 44144 | 2025-01-01T09:00:00.000Z |
    Then a tournament is in the list of all tournaments
      | name             | description | location                         | datetime                 |
      | Winter is Coming |             | 123 Main St. Cleveland, OH 44144 | 2025-01-01T09:00:00.000Z |