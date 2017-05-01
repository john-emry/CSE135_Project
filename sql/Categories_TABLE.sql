CREATE TABLE public.categories
(
    "CategoryID" serial NOT NULL,
    "Name" text COLLATE pg_catalog."default" NOT NULL,
    "Description" text COLLATE pg_catalog."default" NOT NULL,
    "AccountID" integer NOT NULL,
    CONSTRAINT "Categories_pkey" PRIMARY KEY ("CategoryID")
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.categories
    OWNER to kyleziegler;