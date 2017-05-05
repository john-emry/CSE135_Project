/*Creating the OrderHistory Table*/
CREATE TABLE public.order_history
(
    "OrderHistoryID" serial NOT NULL,
    "TotalPrice" text NOT NULL,
    "Date" date NOT NULL,
    "AccountID" integer NOT NULL,
    PRIMARY KEY ("OrderHistoryID")
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.order_history
    OWNER to kyleziegler;