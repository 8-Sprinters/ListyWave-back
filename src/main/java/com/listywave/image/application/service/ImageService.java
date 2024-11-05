package com.listywave.image.application.service;

import static com.amazonaws.HttpMethod.PUT;
import static com.amazonaws.services.s3.Headers.S3_CANNED_ACL;
import static com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead;
import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;
import static com.listywave.common.exception.ErrorCode.S3_DELETE_OBJECTS_EXCEPTION;
import static com.listywave.image.application.domain.ImageType.LISTS_ITEM;
import static com.listywave.image.application.domain.ImageType.NOTICE;
import static com.listywave.image.application.domain.ImageType.USER_BACKGROUND;
import static com.listywave.image.application.domain.ImageType.USER_PROFILE;
import static java.util.Locale.ENGLISH;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.listywave.common.exception.CustomException;
import com.listywave.image.application.domain.ImageFileExtension;
import com.listywave.image.application.domain.ImageType;
import com.listywave.image.application.dto.response.ListItemPresignedUrlResponse;
import com.listywave.image.application.dto.response.UserPresignedUrlCreateResponse;
import com.listywave.image.presentation.dto.request.ListImagesCreateRequest.ExtensionRanks;
import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.item.ItemImageUrl;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.ItemRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.notice.application.domain.Notice;
import com.listywave.notice.application.domain.NoticeContent;
import com.listywave.notice.application.dto.NoticeImagePresignedUrlCreateResponse;
import com.listywave.notice.application.dto.OrderAndExtensionDto;
import com.listywave.notice.repository.NoticeContentRepository;
import com.listywave.notice.repository.NoticeRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.net.URL;
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
    private final NoticeRepository noticeRepository;
    private final NoticeContentRepository noticeContentRepository;

    public List<ListItemPresignedUrlResponse> createPresignedUrlOfItem(Long userId, Long listId, List<ExtensionRanks> extensionRanks) {
        User user = userRepository.getById(userId);
        ListEntity list = listRepository.getById(listId);
        list.validateOwner(user);

        return extensionRanks.stream()
                .map(it -> {
                            Item item = itemRepository.findByListIdAndRanking(listId, it.rank())
                                    .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
                            String imageKey = UUID.randomUUID().toString();
                            item.updateItemImageKey(imageKey);

                            String fileName = createFileName(LISTS_ITEM, listId, imageKey, it.extension());
                            GeneratePresignedUrlRequest request = createGeneratePreSignedUrlRequest(fileName);

                            String presignedUrl = amazonS3.generatePresignedUrl(request).toString();
                            return ListItemPresignedUrlResponse.from(it.rank(), presignedUrl);
                        }
                ).toList();
    }

    private String createFileName(
            ImageType imageType,
            Long resourceId,
            String imageKey,
            ImageFileExtension imageFileExtension
    ) {
        return getCurrentProfile()
                + "/" + imageType.name().toLowerCase(ENGLISH)
                + "/" + resourceId
                + "/" + imageKey
                + "." + imageFileExtension.name().toLowerCase(ENGLISH);
    }

    public String getCurrentProfile() {
        return Arrays.stream(environment.getActiveProfiles())
                .filter(profile -> profile.equals("dev") || profile.equals("prod"))
                .findFirst()
                .orElse(LOCAL);
    }

    private GeneratePresignedUrlRequest createGeneratePreSignedUrlRequest(String fileName) {
        var request = new GeneratePresignedUrlRequest(bucket, fileName, PUT)
                .withExpiration(createPresignedUrlExpiration());
        request.addRequestParameter(S3_CANNED_ACL, PublicRead.toString());

        return request;
    }

    private Date createPresignedUrlExpiration() {
        Date expiration = new Date();
        var expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 30;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    public void updateAllItemsImageUrl(Long userId, Long listId, List<ExtensionRanks> extensionRanks) {
        User user = userRepository.getById(userId);
        ListEntity list = listRepository.getById(listId);
        list.validateOwner(user);

        extensionRanks.forEach(it -> {
                    Item item = itemRepository.findByListIdAndRanking(listId, it.rank())
                            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND, "해당 아이템이 존재하지 않습니다."));
                    String imageUrl = createReadImageUrl(LISTS_ITEM, listId, item.getImageKey(), it.extension());
                    item.updateItemImageUrl(new ItemImageUrl(imageUrl));
                }
        );
    }

    private String createReadImageUrl(
            ImageType imageType,
            Long resourceId,
            String imageKey,
            ImageFileExtension imageFileExtension
    ) {
        return IMAGE_DOMAIN_URL
                + "/" + getCurrentProfile()
                + "/" + imageType.name().toLowerCase(ENGLISH)
                + "/" + resourceId
                + "/" + imageKey
                + "." + imageFileExtension.name().toLowerCase(ENGLISH);
    }

    public UserPresignedUrlCreateResponse createPresignedUrlOfUserImage(
            ImageFileExtension profileExtension,
            ImageFileExtension backgroundExtension,
            Long userId
    ) {
        User user = userRepository.getById(userId);

        String profileImageKey = UUID.randomUUID().toString();
        String backgroundImageKey = UUID.randomUUID().toString();

        String profilePresignedUrl = "";
        String backgroundPresignedUrl = "";

        if (profileExtension != null) {
            deleteUserImageFileIfCustomImage(user.getProfileImageUrl());

            var presignedUrlRequest = createUserGeneratePresignedUrlRequest(USER_PROFILE, profileExtension, user, profileImageKey, "");
            profilePresignedUrl = amazonS3.generatePresignedUrl(presignedUrlRequest).toString();
        }
        if (backgroundExtension != null) {
            deleteUserImageFileIfCustomImage(user.getBackgroundImageUrl());

            var presignedUrlRequest = createUserGeneratePresignedUrlRequest(USER_BACKGROUND, backgroundExtension, user, "", backgroundImageKey);
            backgroundPresignedUrl = amazonS3.generatePresignedUrl(presignedUrlRequest).toString();
        }
        return UserPresignedUrlCreateResponse.of(userId, profilePresignedUrl, backgroundPresignedUrl);
    }

    private void deleteUserImageFileIfCustomImage(String imageUrl) {
        if (isCustomUserImage(imageUrl)) {
            String fileFullPath = getFileFullName(imageUrl);
            deleteImageFile(fileFullPath);
        }
    }

    private boolean isCustomUserImage(String url) {
        String[] split = url.split("/");
        if (split.length >= 4) {
            String type = split[3];
            return !type.equals("basic");
        }
        return false;
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

    private void deleteImageFile(String filePath) {
        try {
            amazonS3.deleteObject(bucket, filePath);
        } catch (AmazonServiceException e) {
            throw new CustomException(S3_DELETE_OBJECTS_EXCEPTION);
        }
    }

    private GeneratePresignedUrlRequest createUserGeneratePresignedUrlRequest(
            ImageType imageType,
            ImageFileExtension extension,
            User user,
            String profileImageKey,
            String backgroundImageKey
    ) {
        String imageKey = "";
        if (imageType == USER_PROFILE) {
            imageKey = profileImageKey;
        }
        if (imageType == USER_BACKGROUND) {
            imageKey = backgroundImageKey;
        }
        user.updateUserImageUrl(profileImageKey, backgroundImageKey);

        String fileName = createFileName(imageType, user.getId(), imageKey, extension);
        return createGeneratePreSignedUrlRequest(fileName);
    }

    public void updateUserImages(
            ImageFileExtension profileExtension,
            ImageFileExtension backgroundExtension,
            Long ownerId
    ) {
        User user = userRepository.getById(ownerId);

        String profileImageUrl = "";
        String backgroundImageUrl = "";

        if (profileExtension != null) {
            profileImageUrl = createReadImageUrl(USER_PROFILE, user.getId(), user.getProfileImageUrl(), profileExtension);
        }
        if (backgroundExtension != null) {
            backgroundImageUrl = createReadImageUrl(USER_BACKGROUND, user.getId(), user.getBackgroundImageUrl(), backgroundExtension);
        }

        user.updateUserImageUrl(profileImageUrl, backgroundImageUrl);
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

    public List<NoticeImagePresignedUrlCreateResponse> createNoticeImagePresignedUrl(
            Long noticeId,
            List<OrderAndExtensionDto> requests
    ) {
        Notice notice = noticeRepository.getById(noticeId);

        return requests.stream()
                .map(it -> {
                    String imageKey = UUID.randomUUID().toString();
                    NoticeContent noticeContent = noticeContentRepository.findByNoticeAndOrder(notice, it.order())
                            .orElseThrow();

                    noticeContent.updateImageUrl(imageKey);

                    String fileName = createFileName(NOTICE, noticeId, imageKey, it.extension());
                    GeneratePresignedUrlRequest request = createGeneratePreSignedUrlRequest(fileName);
                    URL presignedUrl = amazonS3.generatePresignedUrl(request);

                    return NoticeImagePresignedUrlCreateResponse.of(it.order(), presignedUrl.toString());
                }).toList();
    }

    public void updateNoticeContentImages(Long noticeId, List<OrderAndExtensionDto> requests) {
        Notice notice = noticeRepository.getById(noticeId);
        requests.forEach(it -> {
            NoticeContent noticeContent = noticeContentRepository.findByNoticeAndOrder(notice, it.order())
                    .orElseThrow();

            String imageUrl = createReadImageUrl(NOTICE, noticeId, noticeContent.getImageUrl(), it.extension());
            noticeContent.updateImageUrl(imageUrl);
        });
    }
}
