CREATE TABLE public."Products"
(
    "ProductID" integer NOT NULL DEFAULT nextval('"Products_ProductID_seq"'::regclass),
    "SKU" text COLLATE pg_catalog."default" NOT NULL,
    "CategoryID" integer NOT NULL,
    "Price" text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "Products_pkey" PRIMARY KEY ("ProductID")
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public."Products"
    OWNER to kyleziegler;