package com.listywave.image.application.service;

import static com.listywave.common.exception.ErrorCode.S3_DELETE_OBJECTS_EXCEPTION;
import static com.listywave.image.application.domain.ImageType.LISTS_ITEM;
import static java.util.Locale.ENGLISH;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.image.application.domain.ImageFileExtension;
import com.listywave.image.application.domain.ImageType;
import com.listywave.image.application.dto.ExtensionRanks;
import com.listywave.image.application.dto.response.ItemPresignedUrlResponse;
import com.listywave.image.application.dto.response.UserPresignedUrlResponse;
import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.item.ItemImageUrl;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.ItemRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private static final String IMAGE_DOMAIN_URL = "https://image.listywave.com";
    private static final String LOCAL = "local";
    private static final String DEV = "dev";

    @Value("${cloud.s3.bucket}")
    private String bucket;
    private final Environment environment;
    private final AmazonS3 amazonS3;
    private final ItemRepository itemRepository;
    private final ListRepository listRepository;
    private final UserRepository userRepository;

    public List<ItemPresignedUrlResponse> createListsPresignedUrl(Long loginUserId, Long listId, List<ExtensionRanks> extensionRanks) {
        User user = userRepository.getById(loginUserId);

        ListEntity list = findListById(listId);
        validateListUserMismatch(list, user);

        return extensionRanks.stream()
                .map((extensionRank) -> {
                            String imageKey = generatedUUID();
                            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                                    getGeneratePresignedUrl(LISTS_ITEM, listId, imageKey, extensionRank.extension());
                            updateItemImageKey(listId, extensionRank, imageKey);

                            return ItemPresignedUrlResponse.of(
                                    extensionRank.rank(),
                                    amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString());
                        }
                )
                .toList();
    }

    public void uploadCompleteItemImages(Long loginUserId, Long listId, List<ExtensionRanks> extensionRanks) {
        final User user = userRepository.getById(loginUserId);

        ListEntity list = findListById(listId);
        validateListUserMismatch(list, user);

        extensionRanks.forEach(
                extensionRank -> {
                    Item item = findItem(listId, extensionRank.rank());
                    String imageUrl = createReadImageUrl(LISTS_ITEM, listId, item.getImageKey(), extensionRank.extension());
                    item.updateItemImageUrl(imageUrl);
                }
        );
    }

    public UserPresignedUrlResponse updateUserImagePresignedUrl(
            ImageFileExtension profileExtension,
            ImageFileExtension backgroundExtension,
            Long loginUserId
    ) {
        User user = userRepository.getById(loginUserId);

        if (isExistProfileExtension(profileExtension, backgroundExtension)) {
            deleteCustomUserImageFile(user.getProfileImageUrl());
            return getUserPresignedUrlResponse(
                    ImageType.USER_PROFILE,
                    null,
                    profileExtension,
                    backgroundExtension,
                    user,
                    false
            );
        }

        if (isExistBackgroundExtension(backgroundExtension, profileExtension)) {
            deleteCustomUserImageFile(user.getBackgroundImageUrl());
            return getUserPresignedUrlResponse(
                    null,
                    ImageType.USER_BACKGROUND,
                    profileExtension,
                    backgroundExtension,
                    user,
                    false
            );
        }

        deleteCustomUserImageFile(user.getProfileImageUrl());
        deleteCustomUserImageFile(user.getBackgroundImageUrl());

        return getUserPresignedUrlResponse(
                ImageType.USER_PROFILE,
                ImageType.USER_BACKGROUND,
                profileExtension,
                backgroundExtension,
                user,
                true
        );
    }

    public void uploadCompleteUserImages(
            ImageFileExtension profileExtension,
            ImageFileExtension backgroundExtension,
            Long ownerId
    ) {
        User user = userRepository.getById(ownerId);

        String profileImageUrl = "";
        String backgroundImageUrl = "";
        boolean isBoth = true;

        if (isExistProfileExtension(profileExtension, backgroundExtension)) {
            profileImageUrl = createReadImageUrl(ImageType.USER_PROFILE, user.getId(), user.getProfileImageUrl(), profileExtension);
            user.updateUserImageUrl(profileImageUrl, backgroundImageUrl);
            isBoth = false;
        }

        if (isExistBackgroundExtension(backgroundExtension, profileExtension)) {
            backgroundImageUrl = createReadImageUrl(ImageType.USER_BACKGROUND, user.getId(), user.getBackgroundImageUrl(), backgroundExtension);
            user.updateUserImageUrl(profileImageUrl, backgroundImageUrl);
            isBoth = false;
        }

        if (isBoth) {
            profileImageUrl = createReadImageUrl(ImageType.USER_PROFILE, user.getId(), user.getProfileImageUrl(), profileExtension);
            backgroundImageUrl = createReadImageUrl(ImageType.USER_BACKGROUND, user.getId(), user.getBackgroundImageUrl(), backgroundExtension);
            user.updateUserImageUrl(profileImageUrl, backgroundImageUrl);
        }
    }

    private void deleteCustomUserImageFile(String imageUrl) {
        if (isCustomUserImage(imageUrl)) {
            String fileFullPath = getFileFullName(imageUrl);
            deleteImageFile(fileFullPath);
        }
    }

    private boolean isCustomUserImage(String url) {
        if (url.split("/").length >= 4) {
            String type = url.split("/")[3];
            return !type.equals("basic");
        }
        return false;
    }

    private void deleteImageFile(String fileFullPath) {
        try {
            amazonS3.deleteObject(bucket, fileFullPath);
        } catch (AmazonServiceException e) {
            throw new CustomException(ErrorCode.S3_DELETE_OBJECTS_EXCEPTION);
        }
    }

    private String getFileFullName(String url) {
        String[] parts = url.split("/");
        StringBuilder extracted = new StringBuilder();
        for (int i = 3; i < parts.length; i++) {
            extracted.append(parts[i]);
            if (i < parts.length - 1) {
                extracted.append("/");
            }
        }
        return extracted.toString();
    }

    private UserPresignedUrlResponse getUserPresignedUrlResponse(
            ImageType profileImageType,
            ImageType backgroundImageType,
            ImageFileExtension profileExtension,
            ImageFileExtension backgroundExtension,
            User user,
            Boolean isBoth
    ) {
        if (!isBoth && profileImageType != null) {
            return generateUserPresignedUrlResponse(profileImageType, profileExtension, user, generatedUUID(), "");
        }
        if (!isBoth && backgroundImageType != null) {
            return generateUserPresignedUrlResponse(backgroundImageType, backgroundExtension, user, "", generatedUUID());
        }
        return generateUserPresignedUrlResponseByBoth(profileImageType, backgroundImageType, profileExtension, backgroundExtension, user);
    }

    private UserPresignedUrlResponse generateUserPresignedUrlResponseByBoth(
            ImageType profileImageType,
            ImageType backgroundImageType,
            ImageFileExtension profileExtension,
            ImageFileExtension backgroundExtension,
            User user
    ) {
        String profileImageKey = generatedUUID();
        String backgroundImageKey = generatedUUID();

        GeneratePresignedUrlRequest profileUrlRequest =
                getGeneratePresignedUrl(profileImageType, user.getId(), profileImageKey, profileExtension);
        GeneratePresignedUrlRequest backgroundUrlRequest =
                getGeneratePresignedUrlRequest(backgroundImageType, backgroundExtension, user, profileImageKey, backgroundImageKey);
        return UserPresignedUrlResponse.of(
                user.getId(),
                amazonS3.generatePresignedUrl(profileUrlRequest).toString(),
                amazonS3.generatePresignedUrl(backgroundUrlRequest).toString()
        );
    }

    private UserPresignedUrlResponse generateUserPresignedUrlResponse(
            ImageType imageType,
            ImageFileExtension extension,
            User user,
            String profileImageKey,
            String backgroundImageKey
    ) {
        GeneratePresignedUrlRequest presignedUrlRequest =
                getGeneratePresignedUrlRequest(imageType, extension, user, profileImageKey, backgroundImageKey);
        return UserPresignedUrlResponse.of(
                user.getId(),
                imageType == ImageType.USER_PROFILE ?
                        amazonS3.generatePresignedUrl(presignedUrlRequest).toString() : "",
                imageType == ImageType.USER_BACKGROUND ?
                        amazonS3.generatePresignedUrl(presignedUrlRequest).toString() : ""
        );
    }

    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(
            ImageType imageType,
            ImageFileExtension extension,
            User user,
            String profileImageKey,
            String backgroundImageKey
    ) {
        String imageKey = "";
        if (imageType == ImageType.USER_PROFILE) {
            imageKey = profileImageKey;
        }
        if (imageType == ImageType.USER_BACKGROUND) {
            imageKey = backgroundImageKey;
        }
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                getGeneratePresignedUrl(imageType, user.getId(), imageKey, extension);
        updateUserImageKey(user, profileImageKey, backgroundImageKey);
        return generatePresignedUrlRequest;
    }

    private boolean isExistProfileExtension(
            ImageFileExtension profileExtension,
            ImageFileExtension backgroundExtension
    ) {
        return backgroundExtension == null &&
                profileExtension != null &&
                !profileExtension.getUploadExtension().isEmpty();
    }

    private boolean isExistBackgroundExtension(
            ImageFileExtension backgroundExtension,
            ImageFileExtension profileExtension
    ) {
        return profileExtension == null &&
                backgroundExtension != null &&
                !backgroundExtension.getUploadExtension().isEmpty();
    }

    private GeneratePresignedUrlRequest getGeneratePresignedUrl(
            ImageType type,
            Long targetId,
            String imageKey,
            ImageFileExtension extension
    ) {
        String fileName = createFileName(type, targetId, imageKey, extension);
        return createGeneratePreSignedUrlRequest(bucket, fileName);
    }


    private String createFileName(
            ImageType imageType,
            Long targetId,
            String imageKey,
            ImageFileExtension imageFileExtension
    ) {
        return getCurrentProfile()
                + "/"
                + imageType.name().toLowerCase(ENGLISH)
                + "/"
                + targetId
                + "/"
                + imageKey
                + "."
                + imageFileExtension.getUploadExtension();
    }

    private String createReadImageUrl(
            ImageType imageType,
            Long targetId,
            String imageKey,
            ImageFileExtension imageFileExtension
    ) {
        return IMAGE_DOMAIN_URL
                + "/"
                + getCurrentProfile()
                + "/"
                + imageType.name().toLowerCase(ENGLISH)
                + "/"
                + targetId
                + "/"
                + imageKey
                + "."
                + imageFileExtension.getUploadExtension();
    }

    private void updateItemImageKey(Long listId, ExtensionRanks extensionRank, String imageKey) {
        findItem(listId, extensionRank.rank())
                .updateItemImageKey(imageKey);
    }

    private Item findItem(Long listId, int rank) {
        return itemRepository
                .findByListIdAndRanking(listId, rank)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "해당 아이템이 존재하지 않습니다."));
    }

    private void updateUserImageKey(User user, String profileImageKey, String backgroundImageKey) {
        user.updateUserImageUrl(profileImageKey, backgroundImageKey);
    }

    private GeneratePresignedUrlRequest createGeneratePreSignedUrlRequest(
            String bucket,
            String fileName
    ) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, fileName)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(getPresignedUrlExpiration());

        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString()
        );
        return generatePresignedUrlRequest;
    }

    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        var expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 30;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    private String generatedUUID() {
        return UUID.randomUUID().toString();
    }

    private void validateListUserMismatch(ListEntity list, User user) {
        if (!list.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.INVALID_ACCESS, "리스트를 생성한 유저와 로그인한 계정이 일치하지 않습니다.");
        }
    }

    private ListEntity findListById(Long listId) {
        return listRepository
                .findById(listId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 리스트입니다."));
    }

    public String getCurrentProfile() {
        return Arrays.stream(environment.getActiveProfiles())
                .filter(this::isActivateDev)
                .findFirst()
                .orElse(LOCAL);
    }

    private boolean isActivateDev(String profile) {
        return profile.equals(DEV);
    }

    @Async
    public void deleteAllOfListImages(Long listId) {
        String path = getCurrentProfile() + "/lists_item/" + listId + "/";
        try {
            ListObjectsV2Result listObjects;
            do {
                listObjects = amazonS3.listObjectsV2(bucket, path);
                for (S3ObjectSummary object : listObjects.getObjectSummaries()) {
                    amazonS3.deleteObject(new DeleteObjectRequest(bucket, object.getKey()));
                }
                listObjects.setContinuationToken(listObjects.getNextContinuationToken());
            } while (listObjects.isTruncated());
        } catch (AmazonServiceException e) {
            throw new CustomException(S3_DELETE_OBJECTS_EXCEPTION);
        }
    }

    public void deleteImageOfItem(Long listId, Long itemId, Long loginUserID) {
        User user = userRepository.getById(loginUserID);
        ListEntity list = listRepository.getById(listId);
        Item item = itemRepository.getReferenceById(itemId);

        list.validateOwner(user);
        list.validateHasItem(item);
        ItemImageUrl itemImageUrl = item.getImageUrl();

        String fileFullName = getFileFullName(itemImageUrl.getValue());
        deleteImageFile(fileFullName);
    }
}
