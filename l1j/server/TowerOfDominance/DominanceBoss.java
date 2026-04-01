package l1j.server.TowerOfDominance;

import java.util.Random;


import l1j.server.server.utils.SystemUtil;

public class DominanceBoss {
	private int _bossnum;
	private String _bossname;
	private int _npcid;
	private int _mapx;
	private int _mapy;
	private int _mapid;
	private int[][] _bosstime;
	private boolean _isment;
	private String _ment;
	private boolean alleffect;
	private int _effid;
	private int _rnds;
	private String[] yoil;
	
	public int getBossNum(){
		return _bossnum;
	}
	public void setBossNum(int num){
		_bossnum = num;
	}
	
	public String getBossName(){
		return _bossname;
	}
	public void setBossName(String name){
		_bossname = name;
	}
	
	public int getNpcId(){
		return _npcid;
	}
	public void setNpcId(int id){
		_npcid = id;
	}
	
	public int getMapX(){
		return _mapx;
	}
	public void setMapX(int x){
		_mapx = x;
	}
	
	public int getMapY(){
		return _mapy;
	}
	public void setMapY(int y){
		_mapy = y;
	}
	
	public int getMapId(){
		return _mapid;
	}
	public void setMapId(int mapid){
		_mapid = mapid;
	}
	
	public int[][] getBossTime(){
		return _bosstime;
	}
	public void setBossTime(int[][] time){
		_bosstime = time;
	}
	
	private int[][] real_time;

	public int[][] getRealTime() {
		return real_time;
	}

	public void setRealTime(int[][] time) {
		this.real_time = time;
	}
	
	public boolean isMentuse(){
		return _isment;
	}
	
	public DominanceBoss setMentuse(boolean flag){
		_isment = flag;
		return this;
	}
	
	public String getMent(){
		return _ment;
	}
	
	public void setMent(String ment){
		_ment = ment;
	}
	
	public boolean isAllEffect(){
		return alleffect;
	}
	
	public DominanceBoss setAllEffect(boolean flag){
		alleffect = flag;
		return this;
	}
	
	public int getEffectNum(){
		return _effid;
	}
	
	public void setEffectNum(int num){
		_effid = num;
	}
	
	public int getRandomSpawn(){
		return _rnds;
	}
	
	public void setRandomSpawn(int num){
		_rnds = num;
	}
	
	private int rnd_time_minute;

	public int getRndMinuteTime() {
		return rnd_time_minute;
	}

	public void setRndMinuteTime(int i) {
		rnd_time_minute = i;
	}
	
	public String[] getYoil() {
		return yoil;
	}

	public void setYoil(String[] yoil) {
		this.yoil = yoil;
	}

	
	public boolean isSpawnTime(int h, int m, long current_time) {
		String now_y = SystemUtil.getYoil(System.currentTimeMillis());
		boolean isYoil = false;
		for (String y : yoil) {
			if (y.equalsIgnoreCase("ÀüÃ¼") || y.equalsIgnoreCase(now_y))
				isYoil = true;
		}
		if (isYoil == false)
			return false;

		for (int[] t : _bosstime) {
			if (t[0] == h && t[1] == m) {
				return true;
			}
		}
		return false;
	}
	
	private static Random _random = new Random(System.nanoTime());
	
	public void resetSpawnTime() {
		if (real_time == null || _bosstime == null) {
			return;
		}

		int rnd_min;

		for (int i = 0; i < _bosstime.length; i++) {
			int[] t = _bosstime[i];
			int[] real_t = real_time[i];//³Î

			rnd_min = rnd_time_minute == 0 ? 0 : _random.nextInt(rnd_time_minute);

			t[0] = real_t[0];
			t[1] = real_t[1] + rnd_min;

			while (t[1] >= 60) {
				t[1] -= 60;
				t[0] += 1;
			}

			if (t[0] >= 24) {
				t[0] -= 24;
			}

		//	MJUIAdapter.on_boss_append(_npcid, _bossname + " " + t[0] + ":" + t[1] + " ", _mapx, _mapy, _mapid);
			 System.out.println(_npcid+"/"+ _bossname + " " + t[0] + ":" + t[1] + " "+ _mapx+"/" +_mapy+"//"+ _mapid);
		}
	}
}