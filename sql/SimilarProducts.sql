

--limit 100, top product pairs that 

SELECT p1."ProductID" as vid1 ,p2."ProductID" as vid2, SUM(v1.dimension*v2.dimension)/(SQRT(SUM(v1.dimension*v1.dimension))*SQRT(SUM(v2.dimension*v2.dimension))) as cos
from products p1 inner join products p2 on p1.did=p2.did and p1.vid<>p2.vid
group by p1.vid, p2.vid 
Limit 100;