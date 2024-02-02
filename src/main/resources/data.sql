INSERT INTO users(oauth_id, oauth_email, nickname, background_image_url, profile_image_url, description,
                  following_count, follower_count, created_date, updated_date, all_private)
VALUES (1, 'userA@naver.com', 'userA', 'https://userA.backgroundImageUrl.com', 'https://userA.profileImageUrl.com',
        'My name is userA', 0, 0, now(), now(), 'false');

INSERT INTO users(oauth_id, oauth_email, nickname, background_image_url, profile_image_url, description,
                  following_count, follower_count, created_date, updated_date, all_private)
VALUES (1, 'userB@naver.com', 'userB', 'https://userB.backgroundImageUrl.com', 'https://userB.profileImageUrl.com',
        'My name is userB', 11, 22, now(), now(), 'false');

INSERT INTO users(oauth_id, oauth_email, nickname, background_image_url, profile_image_url, description,
                  following_count, follower_count, created_date, updated_date, all_private)
VALUES (1, 'userC@naver.com', 'userC', 'https://userC.backgroundImageUrl.com', 'https://userC.profileImageUrl.com',
        'My name is userC', 33, 44, now(), now(), 'true');
