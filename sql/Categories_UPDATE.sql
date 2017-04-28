/*This will allow users to update categories, they should only be able to 
dit categories that belong to them so we don't have to include that in the script.
Since name is a unique identifier we can go off that, or we can use the category ID if we
have that client side*/
UPDATE public.categories
	SET "Name"= 'Updated Category', "Description"= 'New Description'
	WHERE "Name" = 'EntryNameToChange';