--Inserts into the sales log whenever a user purchases a product
-- for itemTypeChanged - they can be either an 'Amount' or 'ProductName'
Insert into salesLog (PID, State, Price, DateTime, Viewed, ItemTypeChanged)
Values ("1", "California", "300", "13:40:10", "false", "Amount")

--Product name update, or product displayed
Insert into salesLog (PID, State, Price, DateTime, Viewed, ItemTypeChanged)
Values ("1", "California", "0", "13:40:10", "false", "ProductName")


--delete from log table onrefresh


