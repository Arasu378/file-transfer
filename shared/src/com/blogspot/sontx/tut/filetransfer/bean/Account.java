package com.blogspot.sontx.tut.filetransfer.bean;

public class Account {
	private static final char DELIM = ' ';
	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public byte[] getBytes() {
		String accountString = String.format("%s%c%s", username, DELIM, password);
		return accountString.getBytes();
	}

	public static Account parse(byte[] bytes, int offset, int length) {
		if (bytes == null)
			return null;
		String accountString = new String(bytes, offset, length);
		int indexOfDelim = accountString.indexOf(DELIM);
		if (indexOfDelim > 0 && indexOfDelim < accountString.length() - 1) {
			Account account = new Account();
			account.setUsername(accountString.substring(0, indexOfDelim));
			account.setPassword(accountString.substring(indexOfDelim + 1));
			return account;
		}
		return null;
	}
}
