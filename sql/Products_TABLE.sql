CREATE TABLE public.products
(
    "ProductID" serial NOT NULL,
    "SKU" text COLLATE pg_catalog."default" NOT NULL,
    "CategoryID" integer NOT NULL,
    "AccountID" integer NOT NULL,
    "Name" text NOT NULL,
    "Price" text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "Products_pkey" PRIMARY KEY ("ProductID")
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.products
    OWNER to postgres;


    /**John run this update script to add the name column*/

    ALTER TABLE public.products
    ADD COLUMN "Name" text NOT NULL;