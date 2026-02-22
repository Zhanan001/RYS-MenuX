package cn.reiyans.RYSMenuX.Command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.reiyans.RYSMenuX.Menu.MenuForm;
import cn.reiyans.RYSMenuX.RYSMenuXMain;

public class MenuCommand extends Command {

    public MenuCommand(String name) {//rpgx-eqp默认前缀
        super(name,"RYSMenuX菜单指令","/MenuX");
        this.setPermission("true");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length < 1) {
            return false;}
        switch (args[0]) {
            case "help" -> {
                if (!commandSender.isOp()) {return false;}
                commandSender.sendMessage("§7<=================================>");
                commandSender.sendMessage("§b/menux main §a打开菜单主页面");
                commandSender.sendMessage("§b/menux help §a获取相关帮助");
                commandSender.sendMessage("§b/menux open <菜单文件名> §a打开指定菜单");
                commandSender.sendMessage("§cPS:任何修改配置文件的行为请在关服状态下执行哦！");
                commandSender.sendMessage("§7<=================================>");
            }
            case "main" -> {
                if (!commandSender.isPlayer()) {commandSender.sendMessage("§c该指令只能玩家身份使用");return false;}
                MenuForm.showUI((Player) commandSender, RYSMenuXMain.getMenus().get("main"),"main");
                return true;
            }
            case "open" -> {
                if (!commandSender.isPlayer()) {commandSender.sendMessage("§c该指令只能玩家身份使用");return false;}
                if(RYSMenuXMain.getMenus().containsKey(args[1])){
                    MenuForm.showUI((Player) commandSender, RYSMenuXMain.getMenus().get(args[1]),args[1]);
                }else{
                    MenuForm.tipUI((Player) commandSender,"提示","§c打开失败!不存在的菜单界面!");
                }
                return true;
            }
            default -> { return true;}
        }
        return true;
    }
}
