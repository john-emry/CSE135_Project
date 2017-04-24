CREATE TABLE public."Category"
(
    "CategoryID" serial NOT NULL,
    "Name" text NOT NULL,
    "Description" text NOT NULL,
    PRIMARY KEY ("CategoryID")
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public."Category"
    OWNER to kyleziegler;