with overall_table as 
(select pc."ProductID",c."State",sum(CAST(pc."Price" as bigint)*pc."Quantity") as amount  
 	from order_history_products pc  
 	inner join order_history sc on (sc."OrderHistoryID" = pc."OrderHistoryID")
 	inner join products p on (pc."ProductID" = p."ProductID") -- add category filter if any
 	inner join accounts c on (sc."AccountID" = c."AccountID")
 	group by pc."ProductID",c."State"
),
top_state as
(select "State", sum(amount) as dollar from (
	select "State", amount from overall_table
	UNION ALL
	select name as "State", 0.0 as amount from states
	) as state_union
 group by "State" order by dollar desc limit 50
),
top_n_state as 
(select row_number() over(order by dollar desc) as state_order, "State", dollar from top_state
),
top_prod as 
(select "ProductID", sum(amount) as dollar from (
	select "ProductID", amount from overall_table
	UNION ALL
	select "ProductID", 0.0 as amount from products
	) as product_union
group by "ProductID" order by dollar desc limit 50
),
top_n_prod as 
(select row_number() over(order by dollar desc) as product_order, "ProductID", dollar from top_prod
)
select ts."State" as header, tp."ProductID", pr."Name", COALESCE(ot.amount, 0.0) as cell_sum, ts.dollar as totalprice, tp.dollar as productprice
	from top_n_prod tp CROSS JOIN top_n_state ts 
	LEFT OUTER JOIN overall_table ot 
	ON ( tp."ProductID" = ot."ProductID" and ts."State" = ot."State")
	inner join products pr ON tp."ProductID" = pr."ProductID"
	order by ts.state_order, tp.product_order