
package l1j.server.GameSystem;

import java.util.ArrayList;
import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.GameServerSetting;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.ShopNpcSpawnTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1MannequinInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.gametime.BaseTime;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.gametime.TimeListener;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Mannequin;
import l1j.server.server.templates.L1Npc;


public class MannequinSystem implements TimeListener{

	private static ArrayList<L1MannequinInstance> MannequinList = new ArrayList<L1MannequinInstance>();

	private GeneralThreadPool _threadPool = GeneralThreadPool.getInstance();

	private static MannequinSystem _instance;

	public static MannequinSystem getInstance() {
		if (_instance == null) {
			_instance = new MannequinSystem();
			RealTimeClock.getInstance().addListener(_instance);
		}
		return _instance;
	}

	private static Random _random = new Random();

	private boolean _power = false;

	static class MannequinTimer implements Runnable {

		public MannequinTimer() { }

		@Override
		public void run() {
			try {
				ArrayList<L1Mannequin> list = ShopNpcSpawnTable.getInstance().getMannequinList();
				for(int i = 0; i < list.size(); i++){

					L1Mannequin vpc = list.get(i);

					L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(vpc.getNpcId());
					npc.setId(ObjectIdFactory.getInstance().nextId());
					npc.setMap(vpc.getMapId());

					npc.getLocation().set(vpc.getX(),vpc.getY(),vpc.getMapId());
					npc.getLocation().forward(5);

					npc.setHomeX(npc.getX());
					npc.setHomeY(npc.getY());
					npc.getMoveState().setHeading(vpc.getHeading());

					npc.setNameId(npc.getName());
					npc.getGfxId().setGfxId(npc.getGfxId().getGfxId());
					npc.setTitle(npc.getTitle());
					npc.setTempLawful(npc.getLawful());
					switch(npc.getGfxId().getGfxId()) {
					case 48: //여기사
						npc.setActionStatus(50);
						break;
					case 61:
						npc.setActionStatus(4);
						break;
					case 37: //여요정
					case 138: //남요정
						npc.setActionStatus(20);
						break;
					case 734: //남법사
					case 1186://여법사
						npc.setActionStatus(40);
						break;	
					case 2786:
					case 2796://남다엘
						npc.setActionStatus(54);
						break;
					}
					//npc.setPassispeed(npc.getPassispeed());

					L1MannequinInstance obj = (L1MannequinInstance)npc;

					L1Npc template = null;
					L1DollInstance doll = null;
					int chance = _random.nextInt(2);
					if (chance == 0) {
						template = NpcTable.getInstance().getTemplate(80107);
						new L1DollInstance(template, npc, L1DollInstance.DOLLTYPE_SUCCUBUS);
					}
					/*L1Character cha = null;
					for (L1Object obj1 : L1World.getInstance().getVisibleObjects(npc)) {
						if (obj1 instanceof L1Character) {
							cha = (L1Character) obj1;
							Broadcaster.broadcastPacket(cha, new S_Fishing(cha.getId(), ActionCodes.ACTION_Fishing, 32754, 32821),
									true);
						}
					}*/
					
					
					L1World.getInstance().storeObject(npc);
					L1World.getInstance().addVisibleObject(npc); 

					//npc.addDoll(doll);
					
					add_Mannequin(obj);
					npc.getLight().turnOnOffLight();
					GameServerSetting.getInstance().set_fakePlayerNum(GameServerSetting.getInstance().get_fakePlayerNum() + 1);
					Thread.sleep(10000);
				}
				list.clear();
			} catch (Exception exception) {
				return; }
		}
	}

	@Override
	public void onMonthChanged(BaseTime time) {}
	@Override
	public void onDayChanged(BaseTime time) { 
	}
	@Override
	public void onHourChanged(BaseTime time) {
	}
	@Override	
	public void onMinuteChanged(BaseTime time) {
/*		for(L1MannequinInstance npc : L1World.getInstance().getAllMannequin()){ 
			if (npc == null) continue;
			int action = _random.nextInt(3) + 1;
			int[] gfxId = new int[]  { 189, 194, 197 };
			int chance = _random.nextInt(gfxId.length);
			if(npc.getMapId() != 4) continue;
			switch(npc.getGfxId().getGfxId()){
			case 37: //여요정
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 2178));
				}
				break;
			case 61: //남기사
				if (action == 1){
				Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				} else if (action == 2) {
				Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 751));
				} else if (action == 3) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
				}
				break;
			case 138: //남요정
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				} else if (action == 2) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance]));// 물약
				}
				break;
			case 734: //남법사
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 190)); //파랭이

				} else if (action == 2) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
				} else if (action == 3) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 3936)); //홀리
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				}
				break;

			case 6137: //52데스
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
				} else if (action == 2) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 191)); //촐기
				} else if (action == 3) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				}
				break;
			case 6140: //52다크엘프
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				} else if (action == 2) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 744));

				}
				break;
			case 6267: //다크나이트
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				} else if (action == 2) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 191)); //촐기
				} else if (action == 3) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
				}
				break;
			case 6268: //다크메이지
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				}
				break;
			case 6269: //다크스카우터
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				} else if (action == 2) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
				}
				break;
			case 6279: //다크어쌔신
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
				}
				break;
			case 6270: //실버나이트
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 190)); //파랭이
				}
				break;
			case 6271: //실버메이지
				Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 3936)); //홀리
				Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 190)); //파랭이
				break;
			case 6272: //실버스카우터
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				}
				break;
			case 6280: //실버어쌔신
				if (action == 1) {//더블브레이크
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 2949));
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 190)); //파랭이
				} else if (action == 2) {//드레스마이티
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 3909));
				}
				break;
			case 6273: //소드나이트
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				} else if (action == 2) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 191)); //촐기
				}
				break;
			case 6274: //소드메이지
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				}
				break;
			case 6275: //소드스카우터
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				}
				break;
			case 6281:// 소드어쌔신
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), gfxId[chance])); //물약
				}
				break;
			case 6276: //아크나이트
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 190)); //파랭이
				}
				break;
			case 6277: //아크메이지
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 190)); //파랭이
				}
				break;
			case 6278: //아크스카우터
				if (action == 1) {
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 830));
					Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
				} else if (action ==2){
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 191)); //촐기
				}
				break;
			case 6282: //아크어쌔신
				if (action == 1) {//더블브레이크
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 2949));
				} else if (action == 2) {//드레스마이티
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 3909));
					Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 191)); //촐기
				}
				break;
			case 2786: //남다엘
				break;
			case 2796: //여다엘
				break;
			}
		}*/
	}

	public boolean isPower(){
		return _power;
	}

	public void MannequinStart() {
		MannequinTimer ns = new MannequinTimer();
		_threadPool.execute(ns);
		_power = true;
	}
	
	private static L1MannequinInstance[] Mannequin_list() {
		return MannequinList.toArray(new L1MannequinInstance[MannequinList.size()]);
	}

	private static void add_Mannequin(L1MannequinInstance npc) {
		if(!MannequinList.contains(npc)) MannequinList.add(npc);;
	}

	public L1MannequinInstance getMannequin(String name){
		L1MannequinInstance[] npc = Mannequin_list();
		for(int i=0 ; i< npc.length ; i++){
			if(npc[i].getNpcTemplate().get_name().equalsIgnoreCase(name)) return npc[i];
		}
		return null;
	}

	public void remove(L1MannequinInstance npc) {
		MannequinList.remove(npc);
	}

}
