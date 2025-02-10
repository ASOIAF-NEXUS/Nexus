package asoiafnexus.listbuilder.model;

import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Common interface for composable and atomic list elements
 */
public interface ListEntry {
    // maybe not needed anymore?
    List<Unit> allEntries();
}
