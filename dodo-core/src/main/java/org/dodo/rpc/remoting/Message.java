package org.dodo.rpc.remoting;

/**
 * 通讯信息包定义
 * @author maxlim
 *
 */
public class Message {
	public final static int HEADER_LENGTH = 14;//协议头固定长度
	public final static byte PROTOCOL_VERSION = 1;
	public enum Command {
		PING((byte)0),
		PONG((byte)1),
		REQUEST((byte)2),
		RESPONSE((byte)3),
//		ERROR((byte)4),
		;
		private byte type;
		private Command(byte type) {
			this.type = type;
		}
		public byte get() {
			return this.type;
		}
		public static Command getType(byte type) {
			for(Command val: Command.values()) {
				if(val.get() == type) return val;
			}
			return null;
		}
	}
	private static final RequestSeqMaker SEQ_INCR = new RequestSeqMaker();
	
	//消息头start
	private int length;
	private byte version = PROTOCOL_VERSION;
	private Command cmd;
	private long seq;//消息序号，递增
	//消息头end
	
	//消息体
	private byte[] body;

	private String remoteIp;

	public static Message buildPing(byte[] body) {
		Message msg = new Message();
		msg.seq = SEQ_INCR.incrementAndGet();
		msg.cmd = Command.PING;
		msg.body = body;
		msg.length = Message.HEADER_LENGTH + (msg.body != null ? msg.body.length : 0);
		return msg;
	}

	public Message buildPong(byte[] body) {
		Message msg = new Message();
		msg.seq = this.seq;
		msg.cmd = Command.PONG;
		msg.body = body;
		msg.length = Message.HEADER_LENGTH + (msg.body != null ? msg.body.length : 0);
		return msg;
	}

	public static Message buildRequest(byte[] body) {
		Message msg = new Message();
		msg.seq = SEQ_INCR.incrementAndGet();
		msg.cmd = Command.REQUEST;
		msg.body = body;
		msg.length = Message.HEADER_LENGTH + (msg.body != null ? msg.body.length : 0);
		return msg;
	}

	public static Message buildResponse(long seq, byte[] body) {
		Message msg = new Message();
		msg.seq = seq;
		msg.cmd = Command.RESPONSE;
		msg.body = body;
		msg.length = Message.HEADER_LENGTH + (msg.body != null ? msg.body.length : 0);
		return msg;
	}

	public Message buildResponseByRequest(byte[] body) {
		Message msg = new Message();
		msg.seq = this.seq;
		msg.cmd = Command.RESPONSE;
		msg.body = body;
		msg.length = Message.HEADER_LENGTH + (msg.body != null ? msg.body.length : 0);
		return msg;
	}

//	public Message buildErrorByRequest(byte[] body) {
//		Message msg = new Message();
//		msg.seq = this.seq;
//		msg.cmd = Command.ERROR;
//		msg.body = body;
//		msg.length = Message.HEADER_LENGTH + (msg.body != null ? msg.body.length : 0);
//		return msg;
//	}
//
//	public static Message buildError(long seq, byte[] body) {
//		Message msg = new Message();
//		msg.seq = seq;
//		msg.cmd = Command.ERROR;
//		msg.body = body;
//		msg.length = Message.HEADER_LENGTH + (msg.body != null ? msg.body.length : 0);
//		return msg;
//	}
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public Command getType() {
		return cmd;
	}

	public void setType(Command type) {
		this.cmd = type;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public long getSeq() {
		return seq;
	}

	public void setSeq(long seq) {
		this.seq = seq;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public Message setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
		return this;
	}

	@Override
	public String toString() {
		return "Message{" +
				"length=" + length +
				", version=" + version +
				", cmd=" + cmd +
				", seq=" + seq +
				", remoteIp='" + remoteIp + '\'' +
				'}';
	}
}
