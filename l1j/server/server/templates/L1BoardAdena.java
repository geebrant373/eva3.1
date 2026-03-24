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

package l1j.server.server.templates;

import java.util.Calendar;

public class L1BoardAdena {
	public L1BoardAdena() {
	}
	
	private int _id;
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}
	
	private String _name;
	/**
	 * 0-판매중, 1-거래중, 2-판매완료
	 */
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		_name = name;
	}
	
	private String _date;
	public String getDate() {
		return _date;
	}
	public void setDate(String date) {
		_date = date;
	}
	
	private String _title;
	public String getTitle() {
		return _title;
	}
	public void setTitle(String title) {
		_title = title;
	}
	
	private String _content;
	public String getContent() {
		return _content;
	}
	public void setContent(String content) {
		_content = content;
	}
	
	private int _type;
	public int getType() {
		return _type;
	}
	public void setType(int type) {
		_type = type;
	}
	
	private int _trade_number;
	public int getTradeNumber() {
		return _trade_number;
	}
	public void setTradeNumber(int trade_number) {
		_trade_number = trade_number;
	}
	
	private int _cha_id;
	public int getChaId() {
		return _cha_id;
	}
	public void setChaId(int cha_id) {
		_cha_id = cha_id;
	}
	
	private String _cha_name;
	public String getChaName() {
		return _cha_name;
	}
	public void setChaName(String cha_name) {
		_cha_name = cha_name;
	}
	
	private String _cha_account_name;
	public String getChaAccountName() {
		return _cha_account_name;
	}
	public void setChaAccountName(String cha_account_name) {
		_cha_account_name = cha_account_name;
	}
	
	private int _trade_id;
	public int getTradeId() {
		return _trade_id;
	}
	public void setTradeId(int trade_id) {
		_trade_id = trade_id;
	}
	
	private String _trade_name;
	public String getTradeName() {
		return _trade_name;
	}
	public void setTradeName(String trade_name) {
		_trade_name = trade_name;
	}
	
	private String _trade_account_name;
	public String getTradeAccountName() {
		return _trade_account_name;
	}
	public void setTradeAccountName(String trade_account_name) {
		_trade_account_name = trade_account_name;
	}
	
	private int _adena_count;
	public int getAdenaCount() {
		return _adena_count;
	}
	public void setAdenaCount(int adena_count) {
		_adena_count = adena_count;
	}
	
	private int _sell_count;
	public int getSellCount() {
		return _sell_count;
	}
	public void setSellCount(int sell_count) {
		_sell_count = sell_count;
	}
	
	private Calendar _time;

	public Calendar getAdenaTime() {
		return _time;
	}

	public void setAdenaTime(Calendar i) {
		_time = i;
	}
}