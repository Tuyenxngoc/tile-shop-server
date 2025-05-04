package com.example.tileshop.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Slide implements Comparable<Slide> {
    @EqualsAndHashCode.Include
    private String id;

    private Long index;

    private String link;

    private String description;

    private String imageUrl;

    @Override
    public int compareTo(Slide other) {
        return this.index.compareTo(other.index);
    }
}
