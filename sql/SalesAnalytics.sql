--order history, asociated to products and price for that product
SELECT p.ProductID, p.Price 
from order_history_products

--Users who
SELECT a.AccountID
FROM accounts a 
Inner Join order_history o
    on a.AccountID = o.AccountID



Where in (Select AccountID from accounts a)



Select p.Name from Products p
order by p.ProductID asc


