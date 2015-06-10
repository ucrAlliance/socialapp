drop sequence msg_id_seq;
drop trigger send_msg_trigger on message;
drop function msg_id_func();
drop language plpgsql;

create sequence msg_id_seq start with 27812;

create language plpgsql;
create or replace function msg_id_func()
	returns "trigger" as
	$BODY$
	BEGIN
		new.msgid=nextval('msg_id_seq');
		new.sendtime=date_trunc('second',NOW());
		return new;
	END;
	$BODY$
language plpgsql volatile;

create trigger send_msg_trigger
before insert
on message
for each row
execute procedure msg_id_func();
