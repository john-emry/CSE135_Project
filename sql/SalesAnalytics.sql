
--Total products 
Select count (*) from products

--Headers, Alphabetical, init
Select p."Name", p."ProductID"
from products p
Group by  p."Name", p."ProductID"
Order by p."Name" asc
Limit 10;

--Headers, Alphabetical, next 10
Select p."Name", p."ProductID"
from products p
Where p."ProductID" > ?
AND p."ProductID" < ?
Order by "Name" Asc
Limit 10;

--State rows, Alphabetical, init
SELECT a."Username", a."AccountID", ohp."Price"
FROM products p, order_history_products ohp, accounts a, order_history oh
WHERE a."AccountID" = oh."AccountID"
AND
ohp."OrderHistoryID" = oh."OrderHistoryID"
AND
ohp."ProductID" = p."ProductID"
Group by a."State", a."AccountID", ohp."Price"
Order by "State" asc
LIMIT 20;

--State rows, Alphabetical, init
SELECT a."State", a."AccountID", ohp."Price"
FROM products p, order_history_products ohp, accounts a, order_history oh
WHERE a."AccountID" = oh."AccountID"
AND
ohp."OrderHistoryID" = oh."OrderHistoryID"
AND
ohp."ProductID" = p."ProductID"
Group by a."State", a."AccountID", ohp."Price"
Order by "State" asc
LIMIT 20;
