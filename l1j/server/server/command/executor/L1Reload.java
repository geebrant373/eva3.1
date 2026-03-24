/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.DollBonusEventSystem.DollBonusEventLoader;
import l1j.server.GameSystem.Timernpc.NewNpcSpawnTable;
import l1j.server.NpcStatusDamage.NpcStatusDamageInfo;
import l1j.server.TowerOfDominance.DominanceDataLoader;
import l1j.server.server.datatables.AccessoryBalanceTable;
import l1j.server.server.datatables.AccessoryEnchantInformationTable;
import l1j.server.server.datatables.AccessoryEnchantInformationTable1;
import l1j.server.server.datatables.AddRewardMonTable;
import l1j.server.server.datatables.ArmorBalanceTable;
import l1j.server.server.datatables.ArmorEnchantTable;
import l1j.server.server.datatables.AttrEnchantControlTable;
import l1j.server.server.datatables.CharacterBalance;
import l1j.server.server.datatables.CharactersAcTable;
import l1j.server.server.datatables.CharactersMrTable;
import l1j.server.server.datatables.CharactersReducTable;
import l1j.server.server.datatables.CraftListTable;
import l1j.server.server.datatables.DropEventTable;
import l1j.server.server.datatables.DropItemTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.FishExpTable;
import l1j.server.server.datatables.ForceItem;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MapFixKeyTable;
import l1j.server.server.datatables.Map_Event;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.MobSkillTable;
import l1j.server.server.datatables.MonsterBalance;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.NpcShopAdenTypeTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ResolventTable;
import l1j.server.server.datatables.ShopNpcSpawnTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsDmgTable;
import l1j.server.server.datatables.SkillsProbabilityDetailTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SpawnBossTable;
import l1j.server.server.datatables.SpawnTable;
import l1j.server.server.datatables.SpecialMapTable;
import l1j.server.server.datatables.WantedTeleportTable;
import l1j.server.server.datatables.WeaponMagicNpcBalanceTable;
import l1j.server.server.datatables.WeaponMagicPcBalanceTable;
import l1j.server.server.datatables.WeaponNpcBalanceTable;
import l1j.server.server.datatables.WeaponPcBalanceTable;
import l1j.server.server.datatables.huntingbookTable;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.utils.LotationStatics;

public class L1Reload implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Reload.class.getName());

	private L1Reload() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Reload();
	}

	@Override
	public void execute(L1PcInstance gm, String cmdName, String arg) {
		if (arg.equalsIgnoreCase("드랍")) {
			DropTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 드랍"));
		} else if (arg.equalsIgnoreCase("스킬확률")) {
			SkillsProbabilityDetailTable.getInstance().reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 스킬확률"));
		} else if (arg.equalsIgnoreCase("낚시경험치")) {
			FishExpTable.getInstance().reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 낚시경험치"));
		} else if (arg.equalsIgnoreCase("몹밸런스")) {
			MonsterBalance.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 몹밸런스"));
		} else if (arg.equalsIgnoreCase("아머밸런스")){
		      ArmorBalanceTable.reload();
		      gm.sendPackets(new S_SystemMessage("리로드 완료: armor_balance"));
		} else if (arg.equalsIgnoreCase("수배맵")){
			WantedTeleportTable.reload();
		      gm.sendPackets(new S_SystemMessage("리로드 완료: wantedteleportmaps"));
		} else if (arg.equalsIgnoreCase("장신구밸런스")){
		      AccessoryBalanceTable.reload();
		      gm.sendPackets(new S_SystemMessage("리로드 완료: Accessary_balance"));
		} else if (arg.equalsIgnoreCase("드랍아이템")) {
			DropItemTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 드랍아이템"));
		} else if (arg.equalsIgnoreCase("상점아덴타입")) {
			NpcShopAdenTypeTable.getInstance().reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 상점아덴타입"));
		} else if (arg.equalsIgnoreCase("속성인챈제한")) {
			AttrEnchantControlTable.getInstance().reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 속성인챈제한"));
		} else if (arg.equalsIgnoreCase("탑보스")) {
			DominanceDataLoader.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료:탑보스"));
		} else if (arg.equalsIgnoreCase("몹밸런스")) {
			 MonsterBalance.reload();
			gm.sendPackets("리로드 완료:몬스터밸런스");
		} else if (arg.equalsIgnoreCase("스킬대미지")) {
			SkillsDmgTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료:스킬대미지"));
		} else if(arg.equalsIgnoreCase("방어구인첸")) {
			ArmorEnchantTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료:방어구인첸리스트"));
		} else if (arg.equalsIgnoreCase("변신")) {
			PolyTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 변신"));
		} else if (arg.equalsIgnoreCase("악세인챈정보")) {
			AccessoryEnchantInformationTable.reload();
			AccessoryEnchantInformationTable1.reload();
			gm.sendPackets("\\aGDB:[accessory_enchant_lis] 테이블 리로드 완료!");
		} else if (arg.equalsIgnoreCase("제작")) {
			CraftListTable.reload();
			gm.sendPackets(new S_SystemMessage("제작 테이블 내용이 최신화 되었습니다."));
		} else if (arg.equalsIgnoreCase("엔피씨대미지")) {
			NpcStatusDamageInfo.do_load();
			gm.sendPackets("\\aGDB:[npc_status_dmg] 리로드 완료!");
		} else if (arg.equalsIgnoreCase("보스스폰")) {
			SpawnBossTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 보스스폰"));
		} else if (arg.equalsIgnoreCase("타임엔피씨스폰")) {
			NewNpcSpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 타임엔피씨스폰"));
		} else if (arg.equalsIgnoreCase("용해제")) {
			ResolventTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 용해제"));
		} else if (arg.equalsIgnoreCase("인형이벤트")) {
			DollBonusEventLoader.reload();
			gm.sendPackets(new S_SystemMessage("\\aGDB:[doll_bonus_event_system] 테이블 리로드 완료!"));
		} else if (arg.equalsIgnoreCase("보너스맵")) {
			SpecialMapTable.reload();
			gm.sendPackets("\\aGDB:[Bonus_map] 테이블 리로드 완료!");
		} else if (arg.equalsIgnoreCase("박스")) {
			L1TreasureBox.load();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 트레져박스"));
		} else if (arg.equalsIgnoreCase("스킬")) {
			SkillsTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 스킬"));
		} else if (arg.equalsIgnoreCase("몹스킬")) {
			MobSkillTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 몹 스킬"));
		} else if (arg.equalsIgnoreCase("맵픽스")) {
			MapFixKeyTable.reload();
		} else if (arg.equalsIgnoreCase("상점")) {
			ShopTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 상점"));
		
		} else if (arg.equalsIgnoreCase("한입만")) {
			AddRewardMonTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 보스한입만시스템"));
		} else if (arg.equalsIgnoreCase("로테이션")) {
			LotationStatics.reload();
			gm.sendPackets(new S_SystemMessage("\\f:로테이션맵 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("기운템")) {
			ForceItem.reload();
			gm.sendPackets(new S_SystemMessage("\\f:리로드 완료: 기운템"));
		} else if (arg.equalsIgnoreCase("엔피씨액션")) {
			NPCTalkDataTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 엔피씨액션"));
		} else if (arg.equalsIgnoreCase("컨피그")) {
			gm.sendPackets(new S_SystemMessage("리로드 완료: 컨피그"));
			Config.load();
			// } else if (arg.equalsIgnoreCase("아이피")) {
			// gm.sendPackets(new S_SystemMessage("리로드 완료: 아이피"));
			// Config.load1();
		} else if (arg.equalsIgnoreCase("스폰리스트")) {
			SpawnTable.getInstance().reload1();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 스폰리스트"));
		} else if (arg.equalsIgnoreCase("아이템")) {
			ItemTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 아이템"));
		} else if (arg.equalsIgnoreCase("엔피씨")) {
			NpcTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 엔피씨"));
		} else if (arg.equalsIgnoreCase("포탈")) {
			Dungeon.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 포탈"));
		} else if (arg.equalsIgnoreCase("맵")) {
			MapsTable.getInstance().reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 맵"));
		} else if (arg.equalsIgnoreCase("맵이벤트")) {
			Map_Event.getInstance().reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 맵이벤트"));
		} else if (arg.equalsIgnoreCase("드랍이벤트")) {
			DropEventTable.getInstance().reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 드랍이벤트"));
		} else if (arg.equalsIgnoreCase("무기밸런스")) {
			WeaponPcBalanceTable.reload();
			WeaponNpcBalanceTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 무기밸런스"));
		} else if (arg.equalsIgnoreCase("무기마법피씨밸런스")) {
			WeaponMagicPcBalanceTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 무기마법피씨밸런스"));
		} else if (arg.equalsIgnoreCase("무기마법엔피씨밸런스")) {
			WeaponMagicNpcBalanceTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 무기마법앤피씨밸런스"));
		} else if (arg.equalsIgnoreCase("캐릭터밸런스")) {
			CharacterBalance.getInstance().reload();
			CharactersMrTable.getInstance().reload();
		    CharactersAcTable.getInstance().reload();
		    CharactersReducTable.getInstance().reload();
			gm.sendPackets(new S_SystemMessage("리로드 완료: 캐릭터밸런스"));
		} else if (arg.equalsIgnoreCase("기억책")) {
			huntingbookTable.reload();
			gm.sendPackets((ServerBasePacket) new S_SystemMessage("huntingbookTable 리로드 완료"));
		} else {
			gm.sendPackets("\\fY━━━━━━━━━━━━ 운영자 리로드 ━━━━━━━━━━━━");
			gm.sendPackets("\\fR[기본]:.드랍 .맵 .상점 .아이피 .드랍아이템");
			gm.sendPackets("\\fR[기본]:.변신 .용해제 .포켓 .캐릭터밸런스");
			gm.sendPackets("\\fR[기본]:.컨피그 .보스스폰 .스폰리스트");
			gm.sendPackets("\\fR[기본]:.무기밸런스 .맵이벤트 .드랍이벤트");
			gm.sendPackets("\\fR[기본]:.한입만 .포켓 .로테이션 .기운템 ");
			gm.sendPackets("\\fR[기본]:.인형이벤트 .악세인챈정보 .엔피씨대미지 ");
			gm.sendPackets("\\fR[기본]:.보너스맵 .스킬대미지 .몹밸런스 .무기마법피씨밸런스");
			gm.sendPackets("\\fR[기본]:.장신구밸런스 .타임엔피씨스폰 .수배맵 .무기마법엔피씨밸런스");

			// gm.sendPackets(new S_SystemMessage(cmdName + " : [드랍, 맵, 상점,아이피,
			// 드랍아이템, 변신, 용해제, 포켓, 컨피그, 보스스폰, 스폰리스트, 무기밸런스, 맵이벤트,
			// 드랍이벤트,한입만,로테이션,기운템,인형이벤트,벨런스,악세인챈정보,엔피씨대미지]"));
		}
	}
}
