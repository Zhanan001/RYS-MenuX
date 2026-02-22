package cn.reiyans.RYSMenuX.Listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.reiyans.RYSMenuX.Menu.MenuForm;
import cn.reiyans.RYSMenuX.RYSMenuXMain;

public class MenuListener implements Listener {

    @EventHandler
    private void PlayerInitEvent(PlayerLocallyInitializedEvent event){
        Player player = event.getPlayer();
        Item itemAir;
        try {
            itemAir = Item.AIR_ITEM;
        }catch (Exception e){
            // 回退到 ID 获取
            itemAir = Item.get(0);
        }
        if(RYSMenuXMain.isItemSwitcher()){
            if(RYSMenuXMain.getItem() != itemAir && !player.getInventory().contains(RYSMenuXMain.getItem())){
                player.giveItem(RYSMenuXMain.getItem());
                player.sendMessage("§a您获得了 §e"+RYSMenuXMain.getItemName()+" §a!手持它点击就能打开菜单主页面啦!");
            }
        }
    }

    @EventHandler
    private void PlayerInteractEntityEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(RYSMenuXMain.isItemSwitcher()){
            CompoundTag tag = player.getInventory().getItemInHand().getNamedTag();
            if(tag!= null && tag.contains("menux")){
                MenuForm.showUI(player, RYSMenuXMain.getMenus().get("main"),"main");
            }
        }
    }

    @EventHandler
    private void PlayerDropEvent(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if(RYSMenuXMain.isItemSwitcher()){
            CompoundTag tag = player.getInventory().getItemInHand().getNamedTag();
            if(tag != null && !RYSMenuXMain.isItemIsDrop() && player.getInventory().getItemInHand().getNamedTag().contains("menux")){
                event.setCancelled();
                player.sendMessage("§c您暂时无法丢弃 §e"+RYSMenuXMain.getItemName()+" §c!");
            }
        }
    }

}
