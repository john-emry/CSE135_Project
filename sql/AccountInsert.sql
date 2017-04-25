/*Account insert*/
\Set newName "John"

IF(Select top 1 from Accounts where "Username" = newName) > =1
    Select Status as "Fail" 
ELSE(Insert into Accounts ("Username", "Age", "State", "Role")
    Values (newName, 24, 'CA', 'Owner')
    Select Status as "Success"
)





