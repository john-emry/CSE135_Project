/*Categories delete*/

/*Incomplete, need to do an inner join on categories_products and see if there are 
any products associated to this category*/
/*Replace the 6 with the id of the category that you are trying to delete*/
SELECT c from categories c
Left Join categories_products cp on cp."CategoryID" = '6'
Where cp."CategoryID" = NULL

