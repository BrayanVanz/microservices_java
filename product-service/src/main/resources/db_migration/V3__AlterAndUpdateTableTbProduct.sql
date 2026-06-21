ALTER TABLE tb_product ADD COLUMN image_url VARCHAR(255);
UPDATE tb_product SET image_url = '';
UPDATE tb_product SET image_url = 'https://www.google.com/imgres?q=iphone%2015%20128%20gb&imgurl=https%3A%2F%2Fm.media-amazon.com%2Fimages%2FI%2F41RpmPYWXLL._AC_UF894%2C1000_QL80_.jpg&imgrefurl=https%3A%2F%2Fwww.amazon.com.br%2FApple-iPhone-15-128-GB%2Fdp%2FB0CP69NT2N&docid=H-nYYY1Qlgvd0M&tbnid=5dwjm0D9befqXM&vet=12ahUKEwjCv_qOpJeVAxUCGbkGHYPqCMUQnPAOegUIrgIQAA..i&w=765&h=1000&hcb=2&ved=2ahUKEwjCv_qOpJeVAxUCGbkGHYPqCMUQnPAOegUIrgIQAA'
WHERE description = 'Iphone 15 128GB';