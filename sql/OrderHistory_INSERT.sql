/*This script only allows the user to buy one item.
going to write another script to allow the user 
to purchase more, by inserting into the junction table,
associating products to purchases*/
DO $$
DECLARE tempID INTEGER;
BEGIN
INSERT INTO public.order_history("TotalPrice", "Date", "AccountID", "OrderHistoryProductsID")
	select '$1,543', '01 Mar 2017', 2, 123456;
    tempID := LASTVAL();
    
INSERT INTO public.order_history_products("ProductID", "OrderHistoryID", "Quantity", "Price")
	select 12, LASTVAL(), 2, '$10';

Update public.order_history
	Set "OrderHistoryProductsID" = LASTVAL() 
    WHERE "OrderHistoryID" = tempID;
   

END $$;