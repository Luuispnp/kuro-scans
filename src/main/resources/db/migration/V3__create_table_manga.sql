CREATE TABLE manga (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    synopsis TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ONGOING',
    author VARCHAR(100),
    artist VARCHAR(100),
    cover_image_key VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE manga_genre (
    manga_id UUID NOT NULL REFERENCES manga(id) ON DELETE CASCADE,
    genre_id UUID NOT NULL REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (manga_id, genre_id)
);