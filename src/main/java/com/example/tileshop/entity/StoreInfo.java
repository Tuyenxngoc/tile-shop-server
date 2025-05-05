package com.example.tileshop.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreInfo {
    private String name;

    private String address;

    private String phone;

    private String phoneSupport;

    private String email;

    private String openingHours;

    private String facebookUrl;

    private String youtubeUrl;

    private String zaloUrl;

    private String bannerLink;

    private String logo;

    private String logoSmall;

    private String bannerImage;

    private String backgroundImage;

    private String backgroundColor;
}
