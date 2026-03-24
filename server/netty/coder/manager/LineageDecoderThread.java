package server.netty.coder.manager;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import server.CircleArray;
import server.LineageClient;

public class LineageDecoderThread implements Runnable{
	
	public LineageDecoderThread(){
		_client = new LinkedBlockingQueue<LineageClient>();
	}
	
	private LinkedBlockingQueue<LineageClient> _client;
	int count = 0;
	int pa_size;
	ByteBuffer buf;
	public void run(){
		while(true){
			try {
				LineageClient client =  _client.take();
					if(client!=null){
						// 연결 해제된거 정리.
						if(!client.isConnected()||client.ckclose==true){
							client.close();
							removeClient(client);							
							continue;
						}
						
						CircleArray Circle = client.getCircleArray();
						while((pa_size = Circle.isPacketPull2()) > 0){
							client.encryptD(Circle.Pull(pa_size));
						}
						
						
					}else{
						removeClient(client);
					}
			} catch (Exception e) {
				//Logger.getInstance().error(getClass().toString()+" run()\r\n"+e.toString(), Config.LOG.error);
			}
		}
	}
	
	// 패킷크기 값 리턴.
	private int PacketSize(byte[] data){
		int length = data[0] &0xff;
		length |= data[1] << 8 &0xff00;
		return length;
	}
	
	private int PacketBufSize(ByteBuffer buf){
		if(buf.remaining() < 2)return 0;
		int length = buf.get() &0xff;
		length |= buf.get() << 8 &0xff00;
		return length;
	}
	
	// 클라 등록
	public void putClient(LineageClient c){
		try {
			//if(!_client.contains(c)) {
				_client.put(c);
			//}
		} catch (Exception e) {
			//Logger.getInstance().error(getClass().toString()+" putClient(LineageClient c)\r\n"+e.toString(), Config.LOG.error);
		}
	}
	
	// 클라 찾기
	public LineageClient getClient(String id){
		if(id!=null){
			try {
				for(LineageClient c : _client){
					if(c!=null){
						if(c.getID()!=null && c.getID().equalsIgnoreCase(id)) {
							return c;
						}
					}else{
						removeClient(c);
					}
				}
			} catch (Exception e) {
				//Logger.getInstance().error(getClass().toString()+" getClient(String id)\r\n"+e.toString(), Config.LOG.error);
			}
		}
		return null;
	}
	
	// 클라 삭제
	public void removeClient(LineageClient c){
		/** LINALL CONNECT SOURCE START **/
		/*if(Config.AUTH_CONNECT) {
			if (c.getAccount() != null) {
				if(LinAllDataSync.getInstance().is_Account(c.getAccount())) {
					LinAllDataSync.getInstance().account_Delete(c.getAccount());
				}
			}
		}*/
		/** LINALL CONNECT SOURCE END **/
		while(_client.contains(c))
				_client.remove(c);
	}
	
	// 클라 갯수
	public int ClientCount(){
		return _client.size();
	}
	
	// 클라 등록되어 있는지 체크
	public boolean ContainsClient(LineageClient c){
		return _client.contains(c);
	}
	public LinkedBlockingQueue<LineageClient> getAllClient(){
		return this._client;
	}
}

