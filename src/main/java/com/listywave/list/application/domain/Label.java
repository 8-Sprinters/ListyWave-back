package com.listywave.list.application.domain;

import com.listywave.list.application.vo.LabelName;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "list_Id")
    private Lists list;

    @Embedded
    private LabelName labelName;

    public static Label createLabel(String labels){
        return Label.builder()
                .labelName(
                        LabelName.builder()
                                .value(labels)
                                .build()
                )
                .build();
    }
}
