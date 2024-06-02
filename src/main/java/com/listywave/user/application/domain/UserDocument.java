package com.listywave.user.application.domain;

import static lombok.AccessLevel.PROTECTED;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Document(indexName = "users_sync_idx")
@Setting(settingPath = "elastic/user/es-setting.json")
@Mapping(mappingPath = "elastic/user/es-mapping.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private Long id;

    @Field(name = "nickname", type = FieldType.Text)
    private String nickname;

    @Field(name = "is_delete", type = FieldType.Boolean)
    private boolean isDelete;

    @Field(name = "background_image_url", type = FieldType.Text)
    private String backgroundImageUrl;

    @JsonProperty("profile_image_url")
    @Field(name = "profile_image_url", type = FieldType.Text)
    private String profileImageUrl;

    @Field(name = "description", type = FieldType.Text)
    private String description;

    @Field(name = "follower_count", type = FieldType.Long)
    private Long followerCount;

    @Field(name = "following_count", type = FieldType.Long)
    private Long followingCount;

    @Field(name = "all_private", type = FieldType.Boolean)
    private boolean allPrivate;

    @Field(name = "oauth_id", type = FieldType.Long)
    private Long oauthId;

    @Field(name = "oauth_email", type = FieldType.Text)
    private String oauthEmail;

    @Field(name = "kakao_access_token", type = FieldType.Text)
    private String kakaoAccessToken;

    @Field(name = "updated_date", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime updatedDate;

    @Field(name = "created_date", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime createdDate;

    public void setId(Long id) {
        this.id = id;
    }
}
