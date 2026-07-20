package cn.reiyans.RYSMenuX.Button;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.reiyans.RYSMenuX.Menu.MenuForm;
import cn.reiyans.RYSMenuX.RYSMenuXMain;
import lombok.Getter;
import lombok.Setter;
import me.onebone.economyapi.EconomyAPI;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseButton{
    @Setter
    @Getter
    private int type;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private boolean enableTexture;

    @Setter
    @Getter
    private String texture;

    @Setter
    @Getter
    private boolean enableMoney;

    @Setter
    @Getter
    private int money;

    @Setter
    @Getter
    private ArrayList<String> opCmds = new ArrayList<>();

    @Setter
    @Getter
    private ArrayList<String> playerCmds = new ArrayList<>();

    @Setter
    @Getter
    private String permission;

    public BaseButton(int type, String name, String texture, int money, List<String> allCmds,String permission){
        this.type = type;
        this.name = name;
        this.texture = texture;
        this.enableTexture = !this.texture.equals("无");
        this.money = money;
        this.enableMoney = this.money != 0;
        this.permission = permission;
        for(String cmd:allCmds){
            String[] cmds = cmd.split("@");
            if(cmds[1].equals("op")){
                this.opCmds.add(cmds[0]);
            }else if(cmds[1].equals("player")){
                this.playerCmds.add(cmds[0]);
            }
        }
    }

    public void costMoneyExecuteCmd(Player player,String var,String menuFileName){
        if(this.enableMoney){
            if(EconomyAPI.getInstance().myMoney(player) >= this.money){
                EconomyAPI.getInstance().reduceMoney(player,this.money);
                executeCmd(player,var);
            }else{
                MenuForm.noticeUI(player,"提示","§c您的"+RYSMenuXMain.getMoneyName()+"不足"+this.money+",操作失败!",menuFileName);
            }
        }else{
            executeCmd(player,var);
        }
    }

    private void executeCmd(Player player,String var){
        for(String cmd:this.getOpCmds()){
            String rendered = cmd.replace("%p",player.getName()).replace("%i",var);
            rendered = RYSMenuXMain.getInstance().replaceVariables(player, rendered);
            Server.getInstance().dispatchCommand(RYSMenuXMain.getConsole(),rendered);
        }
        for(String cmd:this.getPlayerCmds()){
            String rendered = cmd.replace("%p",player.getName()).replace("%i",var);
            rendered = RYSMenuXMain.getInstance().replaceVariables(player, rendered);
            Server.getInstance().dispatchCommand(player,rendered);
        }
    }
}
