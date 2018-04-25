package com.cy.omniknight.socket.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;

/**
 * 发送和接收的网络包结构
 */
public class NetPack implements Serializable {
	private static final long serialVersionUID = 2170532940750809810L;

	private static final int NET_PACK_HEADER_LEN = 16;
	private static final int NET_PACK_VERSION = 0x1;

	public static class Header implements Serializable {
		private static final long serialVersionUID = 5353487952020405289L;

		public int totalLen;
		public short headLen;
		public short version;

		public int opCmd;
		public int seq;

		public Header(int seq, int dataLen, int opCmd) {
			headLen = NET_PACK_HEADER_LEN;
			version = NET_PACK_VERSION;

			this.totalLen = dataLen + headLen;
			this.opCmd = opCmd;
			this.seq = seq;
		}

		@Override
		public String toString() {
			return "NetPack.Header: totalLen = " + totalLen + ",headLen = " + headLen + ",opCmd = " + opCmd + ",seq = " + seq;
		}
	}

	/**
	 * 发送的包结构
	 */
	public static class Request implements Serializable {
		private static final long serialVersionUID = 5211534703311390513L;

		private Header head;
		private byte[] body;

		public Request(int seq, int cmd, byte[] data) {
			head = new Header(seq, data.length, cmd);
			body = data;
		}

		public Response getResponse() {
			return new Response(this);
		}

		public int length() {
			return head.totalLen;
		}

		public byte[] body() {
			return body;
		}

		public Header head() {
			return head;
		}

		public byte[] serialize() {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(bos);
				out.writeInt(head.totalLen);
				out.writeShort(head.headLen);
				out.writeShort(head.version);
				out.writeInt(head.opCmd);
				out.writeInt(head.seq);
				out.write(body);
				out.close();
				return bos.toByteArray();
			} catch (IOException e) {
			}
			return null;
		}

		public boolean unserialize(DataInputStream in) throws IOException {
			head.totalLen = in.readInt();
			head.headLen = in.readShort();
			head.version = in.readShort();
			head.opCmd = in.readInt();
			head.seq = in.readInt();
			// read all
			int toRead = head.totalLen - head.headLen;
			body = new byte[toRead];
			in.readFully(body);
			FlowControl.dealWithRead(head.totalLen);
			return true;
		}
	}

	public static class Response implements Serializable {
		private static final long serialVersionUID = 3844743134836867556L;

		private Header head;
		private byte[] body;

		public Response() {
			head = new Header(0, 0, 0);
			body = new byte[0];
		}

		public Response(Request req) {
			head = new Header(req.head.seq, 0, req.head.opCmd);
			body = new byte[0];
		}

		public Response(int seq, int cmd) {
			head = new Header(seq, 0, cmd);
			body = new byte[0];
		}

		public Response(int seq, int cmd, byte[] data) {
			head = new Header(seq, data.length, cmd);
			body = data;
		}

		public int length() {
			return head.totalLen;
		}

		public byte[] body() {
			return body;
		}

		public Header head() {
			return head;
		}

		synchronized public boolean unserialize(DataInputStream in) throws IOException {
			head.totalLen = in.readInt();
			head.headLen = in.readShort();
			head.version = in.readShort();
			// confirm
//			if (head.headLen != ConstantsProtocal.NET_PACK_HEADER_LEN || head.version != ConstantsProtocal.NET_PACK_VERSION) {
//				//Log.e(TAG, "Response.unserialize invalid header, length=" + head.headLen + ", version=" + head.version);
//				return false;
//			}
			head.opCmd = in.readInt();
			head.seq = in.readInt();
			// read all
			int toRead = head.totalLen - head.headLen;
			if (head.totalLen > 10000 || head.totalLen <= 0) {
				toRead = in.available();
			}
			body = new byte[toRead];
			try {
				in.readFully(body);
//				in.readFully(body, 0, toRead);
			} catch (EOFException e) {
			}

			FlowControl.dealWithRead(head.totalLen);
			return true;
		}
	}

}