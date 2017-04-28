/*Categories delete*/


/*Incomplete, need to do an inner join on categories_products and see if there are 
any products associated to this category*/
DELETE FROM public.categories
	WHERE "Name" = 'NameToDelete';