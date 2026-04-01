/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
/*
 * $Header: /cvsroot/l2j/L2_Gameserver/java/net/sf/l2j/Server.java,v 1.5 2004/11/19 08:54:43 l2chef Exp $
 *
 * $Author: l2chef $
 * $Date: 2004/11/19 08:54:43 $
 * $Revision: 1.5 $
 * $Log: Server.java,v $
 * Revision 1.5  2004/11/19 08:54:43  l2chef
 * database is now used
 *
 * Revision 1.4  2004/07/08 22:42:28  l2chef
 * logfolder is created automatically
 *
 * Revision 1.3  2004/06/30 21:51:33  l2chef
 * using jdk logger instead of println
 *
 * Revision 1.2  2004/06/27 08:12:59  jeichhorn
 * Added copyright notice
 */
package server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;


import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;
import server.netty.CodecFactory;
import server.netty.ProtocolHandler;

public class Server {
	private volatile static Server uniqueInstance;
	//private static Logger _log = Logger.getLogger(Server.class.getName());
	private static final String LOG_PROP = "./config/log.properties";//로그 설정 파일
	
	//private MonitorManager monitorServer;

	public static Calendar StartTime;
	
	public Server() {
	//	monitorServer = new MonitorManager();
		//loginServer = new ConnectionAcceptor(); 
	}
	
	public static Server createServer() {
		if(uniqueInstance == null) {
			synchronized (Server.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new Server();
				}
			}
		}
		return uniqueInstance;
	}
	
	public void start() {
		initLogManager();
		initDBFactory();
		try {
			PerformanceTimer timer = new PerformanceTimer();
			clearDB();
			timer.reset();
			clearDB();			
			timer = null;
		} catch (SQLException e) {}
		startGameServer();
	//	startTelnetServer();
		startLoginServer();
		
		
	//	monitorServer.register(loginServer);
	}
	
	public static InetSocketAddress Address;

	static private ServerBootstrap sb;
	static private CodecFactory cf;
	@SuppressWarnings("unused")
	static private Channel channel;
	
	private void startLoginServer() {
		try {
			//loginServer.initialize();
			String pla = "";
			// TCP/IP 서버 클레스 활성화
			LoginController.getInstance().setMaxAllowedOnlinePlayers(Config.MAX_ONLINE_USERS);
			sb = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
			cf = new CodecFactory(new ProtocolHandler());
			sb.setPipelineFactory(cf);

			// 서버 성능을 높이기위해 클라가 접속을 끊더라도 세션을 유지하는 알고리즘임..필요없으므로 false
			sb.setOption("child.keepAlive", false);
			// Naggle 비활성.
			sb.setOption("child.tcpNoDelay", true);
			// 받을 패킷의 최대양.
			sb.setOption("child.receiveBufferSize", 2048);
			sb.setOption("connectTimeoutMillis", 300);
			// 서버 활성화.
			channel = sb.bind(new InetSocketAddress(Config.GAME_SERVER_PORT));
			pla = "Netty";
			System.out.println("플랫폼 : " + pla + "  서버포트 " + Config.GAME_SERVER_PORT + "번으로 서버 가동이 완료 되었습니다.");
		} catch(Exception e) { /*e.printStackTrace();*/ };
		// FIXME StrackTrace하면 error  
	}

	public void shutdown() {
		//loginServer.shutdown();
		GameServer.getInstance().shutdown();		
		//System.exit(0);
	}
	
	private void initLogManager() {
		File logFolder = new File("log");
		logFolder.mkdir();

		try {
			InputStream is = new BufferedInputStream(new FileInputStream(LOG_PROP));
			LogManager.getLogManager().readConfiguration(is);
			is.close();
		} catch (IOException e) {
			//_log.log(Level.SEVERE, "Failed to Load " + LOG_PROP + " File.", e);
			System.exit(0);
		}
		try {
			Config.load();
		} catch (Exception e) {
			//_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			System.exit(0);
		}
	}
	
	private void initDBFactory() {// L1DatabaseFactory 초기설정
		L1DatabaseFactory.setDatabaseSettings(Config.DB_DRIVER, Config.DB_URL,
				                              Config.DB_LOGIN, Config.DB_PASSWORD);
		try {
			L1DatabaseFactory.getInstance();
		} catch(Exception e) { /*e.printStackTrace();*/ };
		// FIXME StrackTrace하면 error 
	}
	
	private void startGameServer() {
		try {
			GameServer.getInstance().initialize();
			StartTime = Calendar.getInstance();
			StartTime.setTimeInMillis(System.currentTimeMillis());
		} catch(Exception e) { /*e.printStackTrace();*/ };
		// FIXME StrackTrace하면 error  
	}
	/*
	private void startTelnetServer() {
		if (Config.TELNET_SERVER) {
			TelnetServer.getInstance().start();
		}
	}
	*/
	private void clearDB() throws SQLException{
		Connection c = null;
		PreparedStatement p = null;
		try {
			c = L1DatabaseFactory.getInstance().getConnection();
			p = c.prepareStatement("call deleteData(?)");
			p.setInt(1, Config.DELETE_DB_DAYS);
			p.execute();
		} catch (Exception e) {
		} finally {
			SQLUtil.close(p);
			SQLUtil.close(c);
		}
	}
}
