package l1j.server.server.templates;

public class L1Mannequin {
	
	public L1Mannequin() { }

	private String _name;
	public String getName(){ return _name; }
	public void setName(String s){ _name = s; }
	
	private int _npcid;
	public int getNpcId(){ return _npcid; }
	public void setNpcid(int i){ _npcid = i; }

	private int _heading;
	public int getHeading(){ return _heading; }
	public void setHeading(int i){ _heading = i; }
	
	private int _locx;
	public int getX() { return _locx; }
	public void setX(int i){ _locx = i; }
	
	private int _locy;
	public int getY(){ return _locy; }
	public void setY(int i){ _locy = i; }
	
	private short _mapid;
	public short getMapId(){ return _mapid; }
	public void setMapId(short i) { _mapid = i; }
}
