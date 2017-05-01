CREATE TABLE public.products
(
    "ProductID" integer NOT NULL DEFAULT nextval('"Products_ProductID_seq"'::regclass),
    "SKU" text COLLATE pg_catalog."default" NOT NULL,
    "CategoryID" integer NOT NULL,
    "Name" text NOT NUL,
    "Price" text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "Products_pkey" PRIMARY KEY ("ProductID")
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.products
    OWNER to kyleziegler;


    /**John run this update script to add the name column*/

    ALTER TABLE public.products
    ADD COLUMN "Name" text NOT NULL;