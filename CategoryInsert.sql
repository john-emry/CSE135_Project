/*Category insert*/
DO $$
DECLARE newCategoryName TEXT DEFAULT 'New Name!';
DECLARE newCategoryDesc TEXT DEFAULT 'New Description!';
DECLARE userID integer DEFAULT 1;
BEGIN

INSERT INTO public.Categories("Name", "Description", "AccountID")
	SELECT newCategoryName, newCategoryDesc, userID
    WHERE NOT EXISTS (SELECT * from public.Categories where "Name" = newCategoryName);

END $$;
