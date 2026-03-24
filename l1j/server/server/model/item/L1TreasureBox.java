package l1j.server.server.model.item;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1TreasureBox {

	private static Logger _log = Logger.getLogger(L1TreasureBox.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "TreasureBoxList")
	private static class TreasureBoxList implements Iterable<L1TreasureBox> {
		@XmlElement(name = "TreasureBox")
		private List<L1TreasureBox> _list;

		public Iterator<L1TreasureBox> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Item {
		@XmlAttribute(name = "ItemId")
		private int _itemId;

		@XmlAttribute(name = "Count")
		private int _count;

		@XmlAttribute(name = "Enchant")
		private int _enchant;
		@XmlAttribute(name = "Bless")
		private int _bless = 1;
		@XmlAttribute(name = "Iden")
		private boolean _identified;
		@XmlAttribute(name = "ClassAbbr")
		private String _classAbbr;
		private int _chance;

		@SuppressWarnings("unused")
		@XmlAttribute(name = "Chance")
		private void setChance(double chance) {
			_chance = (int) (chance * 10000);
		}

		public int getItemId() {
			return _itemId;
		}

		public int getCount() {
			return _count;
		}

		public int getEnchant() {
			return _enchant;
		}

		public int getBless() {
			return _bless;
		}

		public double getChance() {
			return _chance;
		}

		public boolean isIdentified() {
			return this._identified;
		}

		public String getClassAbbr() {
			return this._classAbbr;
		}
	}

	private static enum TYPE {
		RANDOM, SPECIFIC, RANDOM_SPECIFIC
	}

	private static final String PATH = "./data/xml/Item/TreasureBox.xml";

	private static final HashMap<Integer, L1TreasureBox> _dataMap = new HashMap<Integer, L1TreasureBox>();

	public static L1TreasureBox get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _boxId;

	@XmlAttribute(name = "Type")
	private TYPE _type;

	private int getBoxId() {
		return _boxId;
	}

	private TYPE getType() {
		return _type;
	}

	@XmlElement(name = "Item")
	private CopyOnWriteArrayList<Item> _items;

	private List<Item> getItems() {
		return _items;
	}

	private int _totalChance;

	private int getTotalChance() {
		return _totalChance;
	}

	private void init() {
		for (Item each : getItems()) {
			_totalChance += each.getChance();
			if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
				getItems().remove(each);
				_log.warning("아이템 ID " + each.getItemId() + " 의 템플릿이 발견되지 않았습니다.");
			}
		}
		if (getType() == TYPE.RANDOM && getTotalChance() != 1000000) {
			_log.warning("ID " + getBoxId() + "의 확률의 합계가 100%가 되지 않습니다.");
			System.out.println("ID " + getBoxId() + "의 확률의 합계가 100%가 되지 않습니다.");
		}
	}

	public static void load() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("reloading " + _log.getName().substring(_log.getName().lastIndexOf(".") + 1) + "...");
		System.out.println("OK! " + timer.elapsedTimeMillis() + "ms");
		try {
			JAXBContext context = JAXBContext.newInstance(L1TreasureBox.TreasureBoxList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(PATH);
			TreasureBoxList list = (TreasureBoxList) um.unmarshal(file);

			for (L1TreasureBox each : list) {
				each.init();
				_dataMap.put(each.getBoxId(), each);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, PATH + "의 로드에 실패.", e);
			System.exit(0);
		}
		// System.out.println("OK! " + timer.get() + "ms");
	}

	public boolean open(L1PcInstance pc) {
		L1ItemInstance item = null;
		Random random = null;
		if (getType().equals(TYPE.SPECIFIC)) {
			if (pc.getInventory().getSize() > 175) {
				pc.sendPackets(new S_SystemMessage("소지하고 있는 아이템이 너무 많습니다."));
				return false;
			}
			if (pc.getInventory().getWeight240() >= 200) {
				pc.sendPackets(new S_SystemMessage("인벤 확인 : 무게 초과 행동이 제한됩니다."));
				return false;
			}
			for (Item each : getItems()) {
				if ((each.getClassAbbr() != null)
						&& (!each.getClassAbbr().contains(pc.getClassFeature().getClassAbbr()))) {
					continue;
				}
				item = ItemTable.getInstance().createItem(each.getItemId());
				if (item != null) {
					item.setCount(each.getCount());
					item.setEnchantLevel(each.getEnchant());
					item.setBless(each.getBless());
					item.setIdentified(each.isIdentified());
					storeItem(pc, item);
				}
			}
		} else if (getType().equals(TYPE.RANDOM)) {
			random = new Random();
			int chance = 0;

			int r = random.nextInt(getTotalChance());
			if (pc.getInventory().getSize() > 175) {
				pc.sendPackets(new S_SystemMessage("소지하고 있는 아이템이 너무 많습니다."));
				return false;
			}
			if (pc.getInventory().getWeight240() >= 200) {
				pc.sendPackets(new S_SystemMessage("인벤 확인 : 무게 초과 행동이 제한됩니다."));
				return false;
			}
			for (Item each : getItems()) {
				chance += each.getChance();
				if ((each.getClassAbbr() != null)
						&& (!each.getClassAbbr().contains(pc.getClassFeature().getClassAbbr()))) {
					continue;
				}
				if (r < chance) {
					item = ItemTable.getInstance().createItem(each.getItemId());
					if (item != null) {
						item.setCount(each.getCount());// 겹치기실패0303
						item.setEnchantLevel(each.getEnchant());// 겹치기실패0303
						item.setBless(each.getBless());// 겹치기실패0303
						item.setIdentified(each.isIdentified());// 겹치기실패0303
						// item.setCount(each.getCount());<<이두개로하면 랜덤서스팩트효과
						// item.setEnchantLevel(each.getEnchant());<<이두개로하면 랜덤서스팩트효과
						storeItem(pc, item);// 겹치기실패0303

						if ((item.getItemId() >= 30152 && item.getItemId() <= 30155)
								|| (each.getItemId() >= 210130 && each.getItemId() <= 210132)
								|| each.getItemId() == 40222 || each.getItemId() == 41148 || each.getItemId() == 5559
								|| each.getItemId() == 210125) {
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
									"아덴 월드의 어느 용사가 " + item.getName() + " 를(을) 획득하였습니다."));
							L1World.getInstance().broadcastPacketToAll(
									new S_SystemMessage("아덴 월드의 어느 용사가 " + item.getName() + " 를(을) 획득하였습니다."));
						}
					}
					break;
				}
			}

		} else if (getType().equals(TYPE.RANDOM_SPECIFIC)) {
			random = new Random(System.nanoTime());
			int chance = 0;

			int r = random.nextInt(getTotalChance());
			if (pc.getInventory().getSize() > 175) {
				pc.sendPackets(new S_SystemMessage("소지하고 있는 아이템이 너무 많습니다."));
				return false;
			}
			if (pc.getInventory().getWeight240() >= 200) {
				pc.sendPackets(new S_SystemMessage("인벤 확인 : 무게 초과 행동이 제한됩니다."));
				return false;
			}
			for (Item each : getItems()) {
				if (each.getChance() == 0) {
					item = ItemTable.getInstance().createItem(each.getItemId());
					if (item != null) {
						item.setCount(each.getCount());
						item.setEnchantLevel(each.getEnchant());
						storeItem(pc, item);
					}
					continue;
				}
				chance += each.getChance();
				if (r < chance) {
					item = ItemTable.getInstance().createItem(each.getItemId());
					if (item != null) {
						item.setCount(each.getCount());
						item.setEnchantLevel(each.getEnchant());
						storeItem(pc, item);
						if ((item.getItemId() >= 30152 && item.getItemId() <= 30155)
								|| (each.getItemId() >= 210130 && each.getItemId() <= 210132)
								|| each.getItemId() == 40222 || each.getItemId() == 41148 || each.getItemId() == 5559
								|| each.getItemId() == 210125) {
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
									"아덴 월드의 어느 용사가 " + item.getName() + " 를(을) 획득하였습니다."));
							L1World.getInstance().broadcastPacketToAll(
									new S_SystemMessage("아덴 월드의 어느 용사가 " + item.getName() + " 를(을) 획득하였습니다."));
						}
					}
					break;
				}
			}
		}
		if (item == null) {
			return false;
		} else {
			int itemId = getBoxId();

			if (itemId == 40576 || itemId == 40577 || itemId == 40578 || itemId == 40411 || itemId == 49013) {
				pc.death(null);
			}
			return true;
		}
	}

	private static void storeItem(L1PcInstance pc, L1ItemInstance item) {
		L1Inventory inventory;

		if (pc.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
			inventory = pc.getInventory();
		} else {
			inventory = L1World.getInstance().getInventory(pc.getLocation());
		}
		if (item.getItemId() == L1ItemId.DRAGON_KEY) {
			pc.sendPackets(new S_Message_YN(1565, ""));
			L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(2922));
		}
		inventory.storeItem(item);
		pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
	}
}
