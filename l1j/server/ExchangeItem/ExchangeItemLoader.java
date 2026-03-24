package l1j.server.ExchangeItem;

import java.sql.ResultSet;
import java.util.HashMap;
import l1j.server.MJTemplate.MJSqlHelper.Executors.Selector;
import l1j.server.MJTemplate.MJSqlHelper.Handler.FullSelectorHandler;
import l1j.server.MJTemplate.MJSqlHelper.Handler.SelectorHandler;

public class ExchangeItemLoader {
  private static ExchangeItemLoader _instance;
  
  private HashMap<Integer, ExchangeItemInfo> _exchange_item;
  
  public static ExchangeItemLoader getInstance() {
    if (_instance == null)
      _instance = new ExchangeItemLoader(); 
    return _instance;
  }
  
  public static void reload() {
    if (_instance != null)
      _instance = new ExchangeItemLoader(); 
  }
  
  private ExchangeItemLoader() {
    load();
  }
  
  private void load() {
    final HashMap<Integer, ExchangeItemInfo> Exchange = new HashMap<>(256);
    Selector.exec("select * from exchange_items", (SelectorHandler)new FullSelectorHandler() {
          public void result(ResultSet rs) throws Exception {
            while (rs.next()) {
              ExchangeItemInfo ExInfo = ExchangeItemInfo.newInstance(rs);
              if (ExInfo == null)
                continue; 
              Exchange.put(Integer.valueOf(ExInfo.get_item_id()), ExInfo);
            } 
          }
        });
    this._exchange_item = Exchange;
  }
  
  public ExchangeItemInfo getExchangeItemInfo(int itemId) {
    if (this._exchange_item.containsKey(Integer.valueOf(itemId)))
      return this._exchange_item.get(Integer.valueOf(itemId)); 
    return null;
  }
}
