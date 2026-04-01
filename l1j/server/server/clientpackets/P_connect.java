package l1j.server.server.clientpackets;

import java.util.ArrayList;
import java.util.Collection;

import server.LineageClient;

import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.P_Itemlist;
import l1j.server.server.templates.L1PrivateShopSellList;

public class P_connect extends ClientBasePacket {

	public P_connect(byte[] abyte0, LineageClient client) {
		super(abyte0);
		int type0 = readC();
		int type1 = readC();
		int type = readC();
		if(type == 1){
			ArrayList<itemdata> sitemlist = new ArrayList<itemdata>();
			String name = readS();
			int enchent = readC();
			int price;
			Collection<L1PcInstance> list = null;
			list = L1World.getInstance().getAllPlayers();
			for(L1PcInstance pc : list){
				if(pc.isPrivateShop()){
					ArrayList<?> itemlist = pc.getSellList();
					if(itemlist == null)continue;
					if(itemlist.size() <= 0)continue;
					for (int i = 0; i < itemlist.size() ; i++) {
						
						L1PrivateShopSellList pssl= (L1PrivateShopSellList) itemlist.get(i);
						price = pssl.getSellPrice();
						L1ItemInstance item = pc.getInventory().getItem(pssl.getItemObjectId());
						if(item != null){
							if(item.getName().contains(name)){
								if(enchent == 255||(enchent < 200&&item.getEnchantLevel() == enchent)){
									String enchentt ="";
									if (item.getEnchantLevel() >= 0) {
										enchentt = "+" + item.getEnchantLevel();
									} else if (item.getEnchantLevel() < 0) {
										enchentt = String.valueOf(item.getEnchantLevel());

									}
									sitemlist.add(new itemdata(enchentt+item.getName(),pc.getName(),item.getBless(),
											price,pc.getLocation()));
								}
							}
						}
					}
				}
			}
			/**º¸³»ÀÚ*/
			client.sendPacket2(new P_Itemlist(sitemlist));
		}
		
		
		
		
		
	}
	public class itemdata{
		public String name ="";
		public String pcname ="";
		public String loc = "";
		public int blass;
		public int price;
		
		public itemdata(String _name,String _pcname,int _blass,int _price,L1Location _loc){
			name = _name;
			pcname = _pcname;
			blass =_blass;
			price =_price;
			/*45121,45123*/
			loc = _loc.getX()+","+_loc.getY();
		}
			
	}
}


