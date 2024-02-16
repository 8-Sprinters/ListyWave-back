package com.listywave.list.application.domain.label;

import static com.listywave.common.exception.ErrorCode.INVALID_COUNT;
import static jakarta.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.list.ListEntity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED, force = true)
public class Labels {

    private static final int MAX_SIZE = 3;

    @OneToMany(mappedBy = "list", cascade = ALL, orphanRemoval = true)
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
        for (Label label : values) {
            label.updateList(list);
        }
        return new Labels(values);
    }

    public List<Label> getValues() {
        return new ArrayList<>(values);
    }
}
