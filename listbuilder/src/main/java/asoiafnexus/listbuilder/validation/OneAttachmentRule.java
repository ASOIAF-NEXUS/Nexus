package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.ComposableEntry;
import asoiafnexus.listbuilder.model.ListEntry;
import asoiafnexus.listbuilder.model.Unit;

import java.util.Collections;
import java.util.List;

/**
 * Normally, a unit can only have one attachment.
 * Some attachments are allowed to be taken and not count towards that limit.
 * All of those attachments are defined here.
 * @param ignoresAttachmentLimits
 */
public record OneAttachmentRule (List<EntryMatcher> ignoresAttachmentLimits)
implements ValidationRule {

    public List<String> validationMessages(List<ListEntry> list) {
        var illegalAttachmentList = list.stream()
                .filter(ComposableEntry.class::isInstance)
                .map(ComposableEntry.class::cast)
                .map(ComposableEntry::attachments)
                .map(this::filterIgnoresAttachmentLimits)
                .filter(a -> a.size() > 1)
                .findAny();

        if(illegalAttachmentList.isPresent()) {
            return List.of("Each unit may only have one attachment, unless a unit ignores the attachment limit.");
        }

        return Collections.emptyList();
    }

    public List<Unit> filterIgnoresAttachmentLimits(List<Unit> attachments) {
        return attachments.stream()
                .filter(e -> ignoresAttachmentLimits.stream().noneMatch(m -> m.match(e)))
                .toList();
    }

}
