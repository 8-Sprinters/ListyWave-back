package com.listywave.list.application.domain.label;

import static com.listywave.common.exception.ErrorCode.INVALID_COUNT;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.list.ListEntity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED, force = true)
public class Labels {

    private static final int MAX_SIZE = 3;

    @OneToMany(fetch = EAGER, mappedBy = "list", cascade = ALL, orphanRemoval = true)
    private final List<Label> values;

    public Labels(List<Label> labels) {
        validateSize(labels);
        this.values = new ArrayList<>(labels);
    }

    private void validateSize(List<Label> labels) {
        if (labels.size() > MAX_SIZE) {
            throw new CustomException(INVALID_COUNT);
        }
    }

    public boolean anyMatch(String keyword) {
        return values.stream()
                .anyMatch(label -> label.isMatch(keyword));
    }

    public Labels updateList(ListEntity list) {
        values.forEach(label -> label.updateList(list));
        return new Labels(values);
    }

    public boolean isChange(Labels newLabels) {
        Set<String> beforeLabelNames = this.values.stream()
                .map(Label::getName)
                .collect(toSet());

        Set<String> newLabelNames = newLabels.values.stream()
                .map(Label::getName)
                .collect(toSet());

        if (beforeLabelNames.size() != newLabelNames.size()) {
            return true;
        }

        for (String beforeLabelName : beforeLabelNames) {
            if (!newLabelNames.contains(beforeLabelName)) {
                return true;
            }
        }
        return false;
    }

    public void updateAll(Labels newLabels, ListEntity list) {
        removeDeletedLabels(newLabels);
        addNewLabels(newLabels, list);
    }

    private void removeDeletedLabels(Labels newLabels) {
        Set<Label> beforeLabels = new HashSet<>(this.values);
        newLabels.values.forEach(beforeLabels::remove);
        this.values.removeAll(beforeLabels);
    }

    private void addNewLabels(Labels newLabels, ListEntity list) {
        Set<Label> newLabelsSet = new HashSet<>(newLabels.values);
        this.values.forEach(newLabelsSet::remove);
        newLabelsSet.forEach(newLabel -> newLabel.updateList(list));
        this.values.addAll(newLabelsSet);
    }

    public List<Label> getValues() {
        return new ArrayList<>(values);
    }
}
