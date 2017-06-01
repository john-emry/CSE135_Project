
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

--Start regular grid queries

--User rows, Alphabetical, init
SELECT a."Username", p."ProductID", ohp."Price"
FROM products p, order_history_products ohp, accounts a, order_history oh
WHERE a."AccountID" = oh."AccountID"
AND
ohp."OrderHistoryID" = oh."OrderHistoryID"
AND
ohp."ProductID" = p."ProductID"
Group by a."Username", p."ProductID", ohp."Price"
Order by "Username" asc, p."Name" asc
LIMIT 20;


--latest from kyle
--State rows, Alphabetical, init
SELECT a."State", p."ProductID", ohp."Price" as totalSales
FROM products p, order_history_products ohp, accounts a, order_history oh
WHERE a."AccountID" = oh."AccountID"
AND
ohp."OrderHistoryID" = oh."OrderHistoryID"
AND
ohp."ProductID"  = p."ProductID"
AND p."ProductID" in (Select "ProductID" from products order by "Name" asc limit 10)
Group by a."State", p."ProductID", ohp."Price"
Order by "State" asc, p."Name" asc
LIMIT 20;

--john updated
Select *
FROM (SELECT ROW_NUMBER() OVER (partition by a."State" ORDER BY p."Name") as r, a."State", p."ProductID", ohp."Price"
FROM products p, order_history_products ohp, accounts a, order_history oh
WHERE a."AccountID" = oh."AccountID"
AND
ohp."OrderHistoryID" = oh."OrderHistoryID"
AND
ohp."ProductID" = p."ProductID"
Group by a."State", p."ProductID", ohp."Price"
Order by "State" asc, p."Name"
) x
WHERE x.r <= 10;














--State rows, Alphabetical, Category filter
SELECT a."State", a."AccountID", ohp."Price"
FROM products p, order_history_products ohp, accounts a, order_history oh
WHERE a."AccountID" = oh."AccountID"
AND
ohp."OrderHistoryID" = oh."OrderHistoryID"
AND
ohp."ProductID" = p."ProductID"
AND
p."ProductID" = ?
Group by a."State", a."AccountID", ohp."Price"
Order by "State" asc
LIMIT 20;



--Top K --Working on these

--Close, but right now they are grouped by the top item sales in each state and
--not the entire state sales
--Top K states
SELECT a."State", p."ProductID", SUM(CAST(ohp."Price" AS bigint)) as totalStateSales
FROM products p, order_history_products ohp, accounts a, order_history oh
WHERE a."AccountID" = oh."AccountID"
AND
ohp."OrderHistoryID" = oh."OrderHistoryID"
AND
ohp."ProductID" = p."ProductID"
Group by a."State", p."ProductID"
Order by totalStateSales desc
LIMIT 20;
