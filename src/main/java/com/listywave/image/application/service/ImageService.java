package com.listywave.image.application.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.common.util.UserUtil;
import com.listywave.image.application.domain.ImageFileExtension;
import com.listywave.image.application.domain.ImageType;
import com.listywave.image.application.dto.ExtensionRanks;
import com.listywave.image.presentation.dto.response.PresignedUrlResponse;
import com.listywave.list.application.domain.Item;
import com.listywave.list.application.domain.Lists;
import com.listywave.list.repository.ItemRepository;
import com.listywave.list.repository.ListRepository;
import com.listywave.user.application.domain.User;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    @Value("${cloud.s3.bucket}")
    private String bucket;
    //TODO: endPoint는 cloudFron 적용하면 지우기
    @Value("${cloud.s3.endpoint}")
    private String endPoint;
    private final AmazonS3 amazonS3;
    private final UserUtil userUtil;
    private final ItemRepository itemRepository;
    private final ListRepository listRepository;

    public List<PresignedUrlResponse> createListsPresignedUrl(Long ownerId, Long listId, List<ExtensionRanks> extensionRanks) {

        //TODO: 회원이 존재하는지 않하는지 검증 (security)
        final User user = userUtil.getUserByUserid(ownerId);

        Lists lists = findListById(listId);
        validateListUserMismatch(lists, user);

        return extensionRanks.stream()
                .map((extensionRank) -> {
                            String imageKey = generatedUUID();
                            String fileName =
                                    createFileName(
                                            ImageType.LISTS_ITEM,
                                            listId,
                                            imageKey,
                                            extensionRank.extension()
                                    );
                            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                                    createGeneratePreSignedUrlRequest(
                                            bucket,
                                            fileName
                                    );

                            updateImageKey(listId, extensionRank, imageKey);

                            return PresignedUrlResponse.of(
                                    extensionRank.rank(),
                                    amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString());
                        }
                )
                .toList();
    }

    public void uploadCompleteItemImages(Long ownerId, Long listId, List<ExtensionRanks> extensionRanks) {
        //TODO: 보내는 회원이 존재하는지 검증 (security)
        final User user = userUtil.getUserByUserid(ownerId);

        Lists lists = findListById(listId);
        validateListUserMismatch(lists, user);

        extensionRanks.forEach(
                extensionRank -> {
                    Item item = findItem(listId, extensionRank.rank());
                    String imageUrl = createReadImageUrl(
                            ImageType.LISTS_ITEM,
                            listId,
                            item.getImageKey(),
                            extensionRank.extension()
                    );
                    item.updateItemImageUrl(imageUrl);
                }
        );

    }

    private String createReadImageUrl(
            ImageType imageType,
            Long targetId,
            String imageKey,
            ImageFileExtension imageFileExtension
    ) {
        //TODO: CloudFront 적용되면 endpoint를 UrlConstants.IMAGE_DOMAIN_URL.getValue()로 교체;
        return endPoint
                + "/"
                + "local"
                + "/"
                + imageType.getValue()
                + "/"
                + targetId
                + "/"
                + imageKey
                + "."
                + imageFileExtension.getUploadExtension();
    }

    private String createFileName(
            ImageType imageType,
            Long targetId,
            String imageKey,
            ImageFileExtension imageFileExtension
    ) {
        //TODO: 맨앞에 prefix는 현제 작업 환경의 profile 이름 가져오는 Util 별도 제작
        return "local"
                + "/"
                + imageType.getValue()
                + "/"
                + targetId
                + "/"
                + imageKey
                + "."
                + imageFileExtension.getUploadExtension();
    }

    private void updateImageKey(Long listId, ExtensionRanks extensionRank, String imageKey) {
        findItem(listId, extensionRank.rank())
                .updateItemImageKey(imageKey);
    }

    private Item findItem(Long listId, int rank) {
        return itemRepository
                .findByListIdAndRanking(listId, rank)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 아이템이 존재하지 않습니다."));
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

    private void validateListUserMismatch(Lists lists, User user) {
        if (!lists.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.INVALID_ACCESS, "리스트를 생성한 유저와 로그인한 계정이 일치하지 않습니다.");
        }
    }

    private Lists findListById(Long listId) {
        return listRepository
                .findById(listId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 리스트입니다."));
    }
}