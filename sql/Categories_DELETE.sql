/*Categories delete*/

/*Deletes from categories if there are no products associated to it*/
Delete from Categories where "CategoryID" = ?
AND
NOT EXISTS (Select * from Products where "CategoryID" = ?)

