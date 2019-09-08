package org.dodo.rpc.remoting;

/**
 * 心跳包生成及判断
 * @author maxlim
 *
 */
public interface HeartbeatData<T> {
	public T ping();
	public boolean isPing(Object object);
	public T pong(T msg);
	public boolean isPong(Object object);

	public static HeartbeatData MESSAGE_HEARTBEAT = new MessageHeartbeat();
	public class MessageHeartbeat implements HeartbeatData<Message> {
		@Override
		public Message ping() {
			return Message.buildPing(null);
		}

		@Override
		public Message pong(Message msg) {
			if( ! isPing(msg)) return null;
			return msg.buildPong(null);
		}

		@Override
		public boolean isPing(Object object) {
			if(object instanceof Message) {
				Message msg = (Message) object;
				return msg.getType() == Message.Command.PING;
			}
			return false;
		}

		@Override
		public boolean isPong(Object object) {
			if(object instanceof Message) {
				Message msg = (Message) object;
				return msg.getType() == Message.Command.PONG;
			}
			return false;
		}
	}
}
