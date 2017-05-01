/*Creating the junction table for order history and products*/
CREATE TABLE public.order_history_products
(
    "OrderHistoryProductsID" serial NOT NULL,
    "ProductID" integer NOT NULL,
    "OrderHistoryID" integer NOT NULL,
    "Quantity" integer NOT NULL,
    "Price" text NOT NULL,
    PRIMARY KEY ("OrderHistoryProductsID")
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.order_history_products
    OWNER to kyleziegler;