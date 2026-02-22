package cn.reiyans.RYSMenuX.Menu;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.PluginTask;
import cn.reiyans.RYSMenuX.RYSMenuXMain;

public class MenuCheckTask extends PluginTask<RYSMenuXMain> {

    private final Item itemAir = Item.get(0);

    public MenuCheckTask( RYSMenuXMain owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        for(Player player: Server.getInstance().getOnlinePlayers().values()){
            if(player != null && RYSMenuXMain.isItemSwitcher()){
                Item item = RYSMenuXMain.getItem();
                if(item != itemAir && !player.getInventory().contains(item)){
                    player.giveItem(item);
                    player.sendMessage("§a您的 §e"+RYSMenuXMain.getItemName()+" §a似乎遗失了!现为您重新发放!");
                }
            }
        }
    }
}
