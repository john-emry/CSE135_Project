/*This script only allows the user to buy one item.
going to write another script to allow the user 
to purchase more, by inserting into the junction table,
associating products to purchases*/

INSERT INTO public.order_history("TotalPrice", "Date", "AccountID")
	select '$1,543', '01 Mar 2017', 2;
    select LASTVAL();

/*Run the above script first, and it will return the order_histor id, then we can use it when inserting below, 
you'll need to keep that ID server side while you are inserting products into the order_history_products table*/



INSERT INTO public.order_history_products("ProductID", "OrderHistoryID", "Quantity", "Price")
	select 12, (last val here), 2, '$10';