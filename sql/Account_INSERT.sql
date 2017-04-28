/*Account insert*/

DO $$
DECLARE newName TEXT DEFAULT 'John';
BEGIN

Insert into public.Accounts("Username", "Age", "State", "Role")
Select newName, '25', 'CA', 'Owner'
WHERE 
	Not Exists (Select * from public.Accounts where "Username" = newName);

END $$;




