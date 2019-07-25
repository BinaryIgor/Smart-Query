package com.iprogrammerr.smart.query.example;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserBook {

	public static final String TABLE = "USER_BOOK";
	public static final String USER_ID = "user_id";
	public static final String BOOK_ID = "book_id";

	public final Integer userId;
	public final Integer bookId;

	public UserBook(Integer userId, Integer bookId) {
		this.userId = userId;
		this.bookId = bookId;
	}

	public static UserBook fromResult(ResultSet result, String userIdLabel, String bookIdLabel) throws Exception {
		Integer userId = result.getInt(userIdLabel);
		Integer bookId = result.getInt(bookIdLabel);
		return new UserBook(userId, bookId);
	}

	public static UserBook fromResult(ResultSet result) throws Exception {
		return fromResult(result, USER_ID, BOOK_ID);
	}

	public static List<UserBook> listFromResult(ResultSet result, String userIdLabel, String bookIdLabel) throws Exception {
		List<UserBook> list = new ArrayList<>();
		do {
			list.add(fromResult(result, userIdLabel, bookIdLabel));
		} while (result.next());
		return list;
	}

	public static List<UserBook> listFromResult(ResultSet result) throws Exception {
		return listFromResult(result, USER_ID, BOOK_ID);
	}
}