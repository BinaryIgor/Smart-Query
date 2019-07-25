package com.iprogrammerr.smart.query.example;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.active.ActiveRecord;
import com.iprogrammerr.smart.query.active.UpdateableColumn;

public class UserRecord extends ActiveRecord<Integer, User> {

	public UserRecord(QueryFactory factory, Integer id) {
		super(factory, User.TABLE, new UpdateableColumn<>(User.ID, id), Integer.class, true,
			new UpdateableColumn<>(User.NAME));
	}

	public UserRecord(QueryFactory factory) {
		this(factory, null);
	}

	@Override
	public User fetch() {
		return fetchQuery().fetch(r -> {
			r.next();
			return User.fromResult(r);
		});
	}

	public UserRecord setName(String name) {
		set(User.NAME, name);
		return this;
	}
}