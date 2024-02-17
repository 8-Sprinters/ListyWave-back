package com.listywave.list.application.domain.label;

import static com.listywave.common.util.StringUtils.match;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.list.application.domain.list.ListEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED, force = true)
public class Label {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "list_Id")
    private ListEntity list;

    @Embedded
    private LabelName name;

    public static Label init(LabelName name) {
        return new Label(null, null, name);
    }

    public boolean isMatch(String keyword) {
        return match(getName(), keyword);
    }

    public void updateList(ListEntity list) {
        this.list = list;
    }

    public String getName() {
        return name.getValue();
    }
}
