ALTER TABLE PostImage CHANGE COLUMN image image_url text;

ALTER TABLE `PostImage` ADD CONSTRAINT `FK_Post_TO_PostImage_1` FOREIGN KEY (
    `post_id`) REFERENCES `Post` (`post_id`);
