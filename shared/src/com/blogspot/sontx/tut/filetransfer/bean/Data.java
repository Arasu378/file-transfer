package com.blogspot.sontx.tut.filetransfer.bean;

public class Data {
	public static final byte TYPE_FILE_INFO = 0;
	public static final byte TYPE_FILE_DATA = 2;
	public static final byte TYPE_CMD_CANCEL = 3;
	public static final byte TYPE_CMD_OK = 4;
	public static final byte TYPE_CMD_SNDFILE = 5;
	public static final byte TYPE_CMD_DISCONNECT = 6;
	public static final byte TYPE_CMD_CONNECT = 7;
	public static final byte TYPE_ACC_LOGIN = 8;
	public static final byte TYPE_FILE_UUID = 9;
	public static final byte TYPE_CMD_LIST = 10;
	public static final byte TYPE_CMD_FRIEND = 11;
	public static final byte TYPE_CMD_DENIED = 12;
	public static final byte TYPE_CMD_FRIEND_ADDED = 13;
	public static final byte TYPE_CMD_FRIEND_REMOVED = 14;
	public static final byte TYPE_ACC_REGISTER = 15;
	public static final byte TYPE_ACC_REGISTER_OK = 16;
	public static final byte TYPE_ACC_REGISTER_CANCEL = 17;

	private byte type = TYPE_CMD_OK;
	private byte[] extra = null;

	public Data() {
	}

	public Data(byte type, byte[] extra) {
		this.type = type;
		this.extra = extra;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte[] getExtra() {
		return extra;
	}

	public void setExtra(byte[] extra) {
		this.extra = extra;
	}

	public void setExtra(byte[] extra, int offset, int length) {
		this.extra = new byte[length];
		System.arraycopy(extra, offset, this.extra, 0, length);
	}

	public byte[] getBytes() {
		byte[] bytes = new byte[1 + (extra != null ? extra.length : 0)];
		bytes[0] = type;
		if (extra != null)
			System.arraycopy(extra, 0, bytes, 1, extra.length);
		return bytes;
	}

	public static Data parse(byte[] bytes, int offset, int length) {
		Data data = new Data();
		data.type = bytes[offset];
		if (length > 1) {
			data.extra = new byte[length - 1];
			System.arraycopy(bytes, offset + 1, data.extra, 0, length - 1);
		}
		return data;
	}
}
