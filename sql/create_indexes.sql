--I have used hash index everywhere because we are mostly concerned with equality queries.

--Hash index on User
CREATE INDEX usrindex on usr using hash (userid);

--Hash index on Message
CREATE INDEX msgindex on message using hash (msgid);

--Hash index on Work Experience
CREATE INDEX wrkindex on work_expr using hash (userid);

--Hash index on Education Details
CREATE INDEX eduindex on educational_details using hash (userid);
