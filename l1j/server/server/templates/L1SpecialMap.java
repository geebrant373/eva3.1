package l1j.server.server.templates;

public class L1SpecialMap{
	private String _Name;
	private int _mapid;
	private double _expRate;
	private int _dmgreduction;
	private int _mdmgreduction;
	
	private double _dmgRate;
	public double getDmgRate(){
		return _dmgRate;
	}
	public void setDmgRate(double i){
		_dmgRate = i;
	}
	
	public int getMapId(){
		return _mapid;
	}
	public void setMapId(int id){
		_mapid = id;
	}
	
	public double getExpRate(){
		return _expRate;
	}
	public void setExpRate(double i){
		_expRate = i;
	}

	public String getName(){
		return _Name;
	}
	public void setName(String name){
		_Name = name;
	}
	
	public int getDmgReduction(){
		return _dmgreduction;
	}
	public void setDmgReduction(int id){
		_dmgreduction = id;
	}
	
	public int getMdmgReduction(){
		return _mdmgreduction;
	}
	public void setMdmgReduction(int id){
		_mdmgreduction = id;
	}
}