CREATE TABLE IF NOT EXISTS sellers (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
                                       contact_info VARCHAR(500),
                                       registration_date TIMESTAMP NOT NULL,
                                       deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                       version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGSERIAL PRIMARY KEY,
                                            seller_id BIGINT NOT NULL,
                                            amount NUMERIC(19, 5) NOT NULL,
                                            payment_type VARCHAR(50) NOT NULL,
                                            transaction_date TIMESTAMP NOT NULL,
                                            deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                            version BIGINT NOT NULL DEFAULT 0,
                                            FOREIGN KEY (seller_id) REFERENCES sellers(id)
);

CREATE INDEX IF NOT EXISTS idx_transactions_seller_id ON transactions(seller_id);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(transaction_date);
CREATE INDEX IF NOT EXISTS idx_transactions_deleted ON transactions(deleted);
CREATE INDEX IF NOT EXISTS idx_sellers_deleted ON sellers(deleted);

CREATE SEQUENCE IF NOT EXISTS seller_seq START 1;
CREATE SEQUENCE IF NOT EXISTS transaction_seq START 1;