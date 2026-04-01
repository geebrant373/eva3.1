package l1j.server.server.templates;

public class L1AddRewardMon {
	private int _npcid;
	private String _npcName;
	private boolean _isItem;
	private int _itemid;
	private int _itemcount;
	
	public boolean isGiveItem(){
		return _isItem;
	}
	public void setGiveItem(boolean flag){
		_isItem = flag;
	}
	
	public int getItemId(){
		return _itemid;
	}
	public void setItemId(int id){
		_itemid = id;
	}
	
	public int getItemCount(){
		return _itemcount;
	}
	public void setItemCount(int i){
		_itemcount = i;
	}
	
	public int getNpcId(){
		return _npcid;
	}
	public void setNpcId(int id){
		_npcid = id;
	}
	
	public String getNpcName(){
		return _npcName;
	}
	public void setNpcName(String name){
		_npcName = name;
	}
}