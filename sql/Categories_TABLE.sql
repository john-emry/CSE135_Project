CREATE TABLE public.categories
(
    "CategoryID" integer NOT NULL DEFAULT nextval('"Categories_CategoryID_seq"'::regclass),
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