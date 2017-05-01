/*only insert when we have unique sku and 
accountid is owner*/

INSERT INTO public.products("SKU", "CategoryID", "Price", "Name")
Select ?, ?, ?, ?
WHERE 
	Not Exists (Select * from public.products where "SKU" = ?)
    AND (Select "Role" from public.accounts where "AccountID" = ?) = 'Owner';



    /*Example below*/
       
INSERT INTO public.products("SKU", "CategoryID", "Price", "Name")
Select 'new sku', 1, '$10', 'new name'
WHERE 
	Not Exists (Select * from public.products where "SKU" = 'new sku')
    AND (Select "Role" from public.accounts where "AccountID" = 1) = 'Owner';


    /*Notice that I did not change the account type = 'owner' part*/

