CREATE TABLE public.products
(
    "ProductID" integer NOT NULL DEFAULT nextval('"Products_ProductID_seq"'::regclass),
    "SKU" text COLLATE pg_catalog."default" NOT NULL,
    "CategoryID" integer NOT NULL,
    "Price" text COLLATE pg_catalog."default" NOT NULL,
    "CategoriesProductsID" integer,
    CONSTRAINT "Products_pkey" PRIMARY KEY ("ProductID")
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.products
    OWNER to kyleziegler;