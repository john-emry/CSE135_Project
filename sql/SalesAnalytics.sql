
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

SELECT producttable."Username" as header, producttable."Name", producttable."ProductID", coalesce(SUM(orders."Price"), 0) as productprice, producttable.totalprice as totalprice
FROM (Select * FROM (
SELECT a."Username", a."AccountID", coalesce(SUM(CAST(oh."TotalPrice" as bigint)), 0) as TotalPrice
FROM accounts a 
LEFT OUTER JOIN order_history oh 
ON a."AccountID" = oh."AccountID"
GROUP BY a."Username", a."AccountID"
ORDER BY a."Username"
LIMIT 20 OFFSET ?) a CROSS JOIN (
SELECT "ProductID", p."Name"
FROM products p, categories c
WHERE p."CategoryID" = c."CategoryID"
AND c."Name" = ?
ORDER BY p."Name"
LIMIT 10 OFFSET ?) product) producttable
LEFT OUTER JOIN (SELECT oh."AccountID", ohp."ProductID", CAST(ohp."Price" as bigint)
FROM order_history_products ohp, order_history oh
WHERE ohp."OrderHistoryID" = oh."OrderHistoryID"
) orders
ON orders."ProductID" = producttable."ProductID"
AND orders."AccountID" = producttable."AccountID"
GROUP BY producttable."Username", producttable.totalprice, producttable."Name", producttable."ProductID"
ORDER BY header, totalprice, producttable."Name", productprice


--Indexes that will benifit: account Username, product name, category name, 
--Notes: We already have indexes on the primary keys (by default)

CREATE INDEX userNameIndex
ON accounts ("Username")

CREATE INDEX categoryNameIndex
ON categories ("Name")

CREATE INDEX productName
ON products ("Name")
