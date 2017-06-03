

--limit 100, top product pairs that 

SELECT p1."ProductID" as vid1 ,p2."ProductID" as vid2, SUM(v1.dimension*v2.dimension)/(SQRT(SUM(v1.dimension*v1.dimension))*SQRT(SUM(v2.dimension*v2.dimension))) as cos
from (Select * from accounts ) as p1 inner join products p2 on p1.did=p2.did and p1.vid<>p2.vid
group by p1.vid, p2.vid 
Limit 100;


--total of each product sold
Select "ProductID", SUM(CAST("Price" AS bigint)) as totalProductSales from order_history_products
Group by "ProductID"
order by "ProductID" asc

--Total product bought by each customer
Select p."ProductID", a."AccountID", SUM(CAST(ohp."Price" AS bigint)) as price
from products p, order_history_products ohp, accounts a, order_history oh
where a."AccountID" = oh."AccountID"
AND oh."OrderHistoryID" = ohp."OrderHistoryID" 
AND ohp."ProductID" = p."ProductID"
Group by p."ProductID", a."AccountID"
order by p."ProductID" asc


--final
WITH similarProducts AS (	SELECT ohp."ProductID" as product_id, ohp."Price" AS price, a."AccountID" as customer_id 
                            FROM order_history_products ohp, accounts a Group BY ohp."ProductID", customer_id, price)
    SELECT p1."Name" AS product1, p2."Name" AS product2,

	(COALESCE((SELECT SUM(CAST(ohp1."Price" as bigint) * CAST(ohp2."Price" as bigint)) 
                                                    FROM order_history_products ohp1, 
                                                    order_history_products ohp2,
                                                    order_history oh,
                                                    accounts a 

WHERE ohp1."ProductID" = p1."ProductID" AND ohp2."ProductID" = p2."ProductID" AND oh."AccountID" = a."AccountID"),0))/
    ((SELECT SUM(CAST("Price" AS bigint)) from order_history_products  ohp WHERE ohp."ProductID" = p1."ProductID") * (SELECT SUM(CAST("Price" AS bigint)) from order_history_products  ohp WHERE ohp."ProductID" = p2."ProductID"))
AS cosine
FROM products p1, products p2 
WHERE p1 < p2 
GROUP BY p1."ProductID", product1, p2."ProductID", product2 
ORDER BY cosine desc
LIMIT 100;

--updated latest
WITH similarProducts AS (	SELECT ohp."ProductID" as product_id, ohp."Price" AS price, a."AccountID" as customer_id FROM order_history_products ohp, accounts a Group BY ohp."ProductID", customer_id, price)
    SELECT p1."Name" AS product1, p2."Name" AS product2,

	(COALESCE((SELECT SUM(CAST(ohp1."Price" as bigint) * CAST(ohp2."Price" as bigint)) 
                                                    FROM order_history_products ohp1, 
                                                    order_history_products ohp2,
                                                    order_history oh,
                                                    accounts a 

WHERE ohp1."ProductID" = p1."ProductID" AND ohp2."ProductID" = p2."ProductID" AND oh."AccountID" = a."AccountID"),0))/
    ((SELECT SUM(CAST("Price" AS bigint)) from order_history_products  ohp WHERE ohp."ProductID" = p1."ProductID") * (SELECT SUM(CAST("Price" AS bigint)) from order_history_products  ohp WHERE ohp."ProductID" = p2."ProductID"))
AS cosine
FROM products p1, products p2 
WHERE p1 < p2 
GROUP BY p1."ProductID", product1, p2."ProductID", product2 
ORDER BY cosine desc
LIMIT 100;