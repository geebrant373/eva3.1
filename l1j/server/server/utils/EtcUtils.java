package l1j.server.server.utils;

import java.util.ArrayList;
import java.util.StringTokenizer;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;

// -- TYUTIL -- 자주쓰는 유틸을 모아두는 곳^ㅡ^

public class EtcUtils {
	public static ArrayList<Integer> GetIntTokenizeArrayList( String str ) {
		String temp2 = "";
		StringTokenizer values = new StringTokenizer(str, " ");
		while (values.hasMoreElements()) temp2 += values.nextToken();
		
		StringTokenizer List = new StringTokenizer(temp2, ",");
		ArrayList<Integer> list = new ArrayList<Integer>();
		while (List.hasMoreElements()) {
			String value = List.nextToken();
			list.add(new Integer( value ));
		}
		return list;
		
	}
	public static int[] GetIntTokenizeIntArray( String str ) {
		int[] returnValue;
		ArrayList<Integer> list =  GetIntTokenizeArrayList( str );
		returnValue = new int[list.size()];
		for (int i = 0; i < list.size(); i++) returnValue[i] = list.get(i);
		return returnValue;
	}
	
	public static enum MESSAGE_TYPE{ NOTICE, GREEN_MESSAGE, BOTH, GREEN, YELLOW, ORANGE };
	public static void SetBroadcastMessage( String str, MESSAGE_TYPE type ) {
		switch( type ) {
		case BOTH:
			L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, str));
			L1World.getInstance().broadcastServerMessage(str);
			break;
		case NOTICE:
			L1World.getInstance().broadcastServerMessage(str);
			break;
		case GREEN:
			L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, str));
			break;
		case YELLOW:
			L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=" + str));
			break;
		case ORANGE:
			L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\aH" + str));
			break;
		}
	}
	public static void BrdCst( String str ) { SetBroadcastMessage( str ); }
	public static void SetBroadcastMessage( String str ) {
		SetBroadcastMessage(str, MESSAGE_TYPE.BOTH);
	}
	public static void HeadMsg( L1PcInstance pc, String msg ) { pc.sendPackets( new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f>" + msg) ); }
	
	public static void SysMsg( L1PcInstance pc, String msg ) {
		pc.sendPackets( new S_SystemMessage( msg ));
	}
	public static void SetSystemMessage( L1PcInstance pc, String msg ) {
		pc.sendPackets( new S_SystemMessage( msg ));
	}
	
	public static void SetSystemMessageBoth( L1PcInstance pc, String msg ) {
		pc.sendPackets( new S_SystemMessage( msg ));
		pc.sendPackets(new S_PacketBox(84, msg));
	}
	
}
