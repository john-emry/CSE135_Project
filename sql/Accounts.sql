CREATE TABLE public."Accounts"
(
    "AccountID" integer NOT NULL DEFAULT nextval('"Accounts_AID_seq"'::regclass),
    "Username" text COLLATE pg_catalog."default",
    "Age" integer,
    "State" text COLLATE pg_catalog."default",
    "Role" text COLLATE pg_catalog."default",
    "SessionToken" text COLLATE pg_catalog."default",
    CONSTRAINT "Accounts_pkey" PRIMARY KEY ("AID")
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public."Accounts"
    OWNER to kyleziegler;