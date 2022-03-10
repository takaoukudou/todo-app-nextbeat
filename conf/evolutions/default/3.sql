# --- !Ups
INSERT INTO to_do_category(name,slug,color) values('フロントエンド','front',1);
INSERT INTO to_do_category(name,slug,color) values('バックエンド','back',2);
INSERT INTO to_do_category(name,slug,color) values('インフラ','infra',3);
INSERT INTO `to_do`(category_id,title,body,state) values(1, 'デザインをいい感じにする','ヘッダーのデザインをもっといい感じに',0);
INSERT INTO `to_do`(category_id,title,body,state) values(2, 'Controllerの修正','Controller名をもっといい感じに',1);
INSERT INTO `to_do`(category_id,title,body,state) values(3, '新しいDB環境の作成','タイトル通り',2);

# --- !Downs
DELETE FROM `to_do_category`;
DELETE FROM `to_do`;