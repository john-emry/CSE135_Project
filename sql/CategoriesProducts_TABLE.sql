/*CategoriesProducts Junction table - associates products and categories*/

CREATE TABLE public.categories_products
(
    "CategoriesProductsID" integer NOT NULL DEFAULT nextval('"CategoriesProducts_CategoriesProductsID_seq"'::regclass),
    "ProductID" integer NOT NULL,
    "CategoryID" integer NOT NULL,
    CONSTRAINT "CategoriesProducts_pkey" PRIMARY KEY ("CategoriesProductsID")
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.categories_products
    OWNER to kyleziegler;