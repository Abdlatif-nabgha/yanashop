CREATE TABLE products (
    id          UUID    PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    price_amount NUMERIC(19, 4) NOT NULL,
    price_currency  VARCHAR(3)  NOT NULL,
    stock_quantity  INTEGER     NOT NULL DEFAULT 0,
    category        VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_price_positive    CHECK ( price_amount >= 0 ),
    CONSTRAINT chk_stock_non_negative CHECK ( stock_quantity >= 0 )
);

CREATE INDEX idx_products_category   ON products (category);
CREATE INDEX idx_products_name ON products (name);
