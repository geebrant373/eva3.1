package l1j.server.server.clientpackets;


import server.LineageClient;
import l1j.server.Warehouse.PackageWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_RetrievePackageList;

public class C_InventoryWarehouse extends ClientBasePacket {

	private static final String C_INVENTORY_WHREHOUSE = "[C] C_InventoryWarehouse";
	
	public C_InventoryWarehouse(byte abyte0[], LineageClient client)  throws Exception {
		super(abyte0);
		if (client == null){
			return;
		}
		int type = readC();
		L1PcInstance pc = client.getActiveChar();
		
		if(type == 6){
			PackageWarehouse w = WarehouseManager.getInstance().getPackageWarehouse(pc.getAccountName());
			if (w.getSize() != 0){
				pc.sendPackets(new S_RetrievePackageList(pc.getId(), pc));
			} else {   
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noitemret")); 
			}
		}
	}
	@Override
	public String getType() {
		return C_INVENTORY_WHREHOUSE;
	}
}