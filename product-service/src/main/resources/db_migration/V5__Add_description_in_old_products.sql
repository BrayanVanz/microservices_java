ALTER TABLE tb_product ADD COLUMN IF NOT EXISTS description TEXT;

UPDATE tb_product SET description = 'Um dos álbuns mais icônicos dos Beatles, gravado nos estúdios Abbey Road em Londres. Apresenta clássicos como "Come Together" e "Something", além da famosa faixa medley do lado B.' WHERE title = 'Abbey Road' AND artist = 'The Beatles';

UPDATE tb_product SET description = 'O álbum mais vendido de todos os tempos, com hits como "Billie Jean", "Beat It" e "Thriller". Uma obra-prima do pop que redefiniu a música e os videoclipes.' WHERE title = 'Thriller' AND artist = 'Michael Jackson';

UPDATE tb_product SET description = 'Considerado um dos maiores álbuns de hard rock já gravados. Lançado após a morte do vocalista Bon Scott, apresenta o inconfundível riff de "Back in Black" e "You Shook Me All Night Long".' WHERE title = 'Back in Black' AND artist = 'AC/DC';

UPDATE tb_product SET description = 'Uma celebração da música eletrônica com influências de disco, funk e jazz. Vencedor do Grammy de Álbum do Ano em 2014, com destaque para "Get Lucky" e "Instant Crush".' WHERE title = 'Random Access Memories' AND artist = 'Daft Punk';

UPDATE tb_product SET description = 'Considerado o álbum de jazz mais importante já gravado. Uma exploração modal que influenciou gerações de músicos, com o lendário quinteto de Miles Davis.' WHERE title = 'Kind of Blue' AND artist = 'Miles Davis';

UPDATE tb_product SET description = 'O álbum mais vendido do Fleetwood Mac, gravado em meio a turbulências pessoais entre os membros da banda. Traz clássicos como "Go Your Own Way", "Dreams" e "The Chain".' WHERE title = 'Rumours' AND artist = 'Fleetwood Mac';

UPDATE tb_product SET description = 'Álbum visual e sonoro que explora temas de infidelidade, raiva, empoderamento e perdão. Uma obra profundamente pessoal com colaborações de Jack White, Kendrick Lamar e James Blake.' WHERE title = 'Lemonade' AND artist = 'Beyoncé';

UPDATE tb_product SET description = 'Um dos álbuns mais vendidos da história, com a icônica capa do prisma. Explora temas como tempo, ganância e loucura através de um som progressivo único.' WHERE title = 'The Dark Side of the Moon' AND artist = 'Pink Floyd';

UPDATE tb_product SET description = 'Álbum aclamado pela crítica que mistura R&B, soul e pop experimental. Uma obra introspectiva e intimista que consolidou Frank Ocean como um dos artistas mais importantes de sua geração.' WHERE title = 'Blonde' AND artist = 'Frank Ocean';

UPDATE tb_product SET description = 'O álbum que definiu o grunge e mudou o rock para sempre. Com produção de Butch Vig, apresenta "Smells Like Teen Spirit", "Come as You Are" e "Lithium".' WHERE title = 'Nevermind' AND artist = 'Nirvana';

UPDATE tb_product SET description = 'Obra seminal do rap contemporâneo que narra a adolescência de Kendrick Lamar em Compton. Aclamado pela crítica como um dos melhores álbuns de hip-hop de todos os tempos.' WHERE title = 'good kid, m.A.A.d city' AND artist = 'Kendrick Lamar';

UPDATE tb_product SET description = 'Um dos álbuns de pop mais bem recebidos dos últimos anos, repleto de hits dançantes como "Don''t Start Now", "Levitating" e "Physical". Lançado no início da pandemia de 2020.' WHERE title = 'Future Nostalgia' AND artist = 'Dua Lipa';