ALTER TABLE tb_product ADD COLUMN image_url VARCHAR(255);
UPDATE tb_product SET image_url = '';