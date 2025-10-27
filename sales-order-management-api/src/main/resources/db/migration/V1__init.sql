-- === Catalog Table ===
CREATE TABLE catalog (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  price NUMERIC(19,2) NOT NULL,
  created_at TIMESTAMP DEFAULT now()
);

-- === Orders ===
CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY,
  customer_name VARCHAR(255) NOT NULL,
  creation_date DATE NOT NULL,
  cancellation_date DATE,
  subtotal NUMERIC(19,2) NOT NULL,
  vat NUMERIC(19,2),
  total NUMERIC(19,2),
  version BIGINT DEFAULT 0
);

-- === Order Items ===
CREATE TABLE order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  catalog_item_id BIGINT,
  item_name VARCHAR(255) NOT NULL,
  unit_price NUMERIC(19,2) NOT NULL,
  quantity INTEGER NOT NULL
);

-- === Users and Roles ===
CREATE TABLE roles (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role_id BIGINT NOT NULL REFERENCES roles(id),
  PRIMARY KEY (user_id, role_id)
);

-- === Using initial data for catalog items ===
INSERT INTO catalog (name, price) VALUES ('Widget-A', 10.00), ('Widget-B', 20.00);
