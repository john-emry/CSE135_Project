--Create 
CREATE TABLE public.sales_log
(
    "SLID" serial NOT NULL,
    "PID" bigint NOT NULL,
    "Price" text,
    "State" text,
    "AccountID" INTEGER NOT NULL,
    "ChangeType" text NOT NULL,
    PRIMARY KEY ("SLID")
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.sales_log
    OWNER to postgres;

--indecies to create
CREATE INDEX sales_log_accounts ON sales_log ("AccountID");
CREATE INDEX sales_log_states ON sales_log ("State");
CREATE INDEX sales_log_productid ON sales_log ("PID");

--Insert script 
INSERT INTO public.sales_log(
	"SLID", "PID", "Price", "State", "AccountID", "ChangeType")
	VALUES (?, ?, ?, ?, ?, ?, ?);


--Delete script

DELETE *
	FROM public.sales_log
    WHERE "AccountID" = ?;



--Select script

SELECT *
	FROM public.sales_log
    WHERE "AccountID" = ?;