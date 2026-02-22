package cn.reiyans.RYSMenuX.Menu;

import cn.nukkit.Player;
import cn.nukkit.form.element.*;
import cn.reiyans.RYSMenuX.Button.*;
import cn.reiyans.RYSMenuX.RYSMenuXMain;
import moe.him188.gui.window.FormCustom;
import moe.him188.gui.window.FormModal;
import moe.him188.gui.window.FormSimple;

import java.util.LinkedHashMap;

public class MenuForm {

    public static void showUI(Player player,Menu menu,String menuFileName){
        FormSimple form = new FormSimple(menu.getTitle(), menu.getText());
        LinkedHashMap<Integer,BaseButton> buttonTemp = new LinkedHashMap<>();
        int i = 0;
        for(BaseButton button:menu.getButtons()){
            if(button.getPermission().equals("true") || player.hasPermission(button.getPermission())){
                if(button.isEnableTexture()){
                    form.addButton(new ElementButton(button.getName(),new ElementButtonImageData("path",button.getTexture())));
                }else{
                    form.addButton(button.getName());
                }
                buttonTemp.put(i,button);
                i++;
            }
        }
        player.showFormWindow(form.onClicked(response -> actButton(player,buttonTemp.get(response),menuFileName)));
    }


    public static void noticeUI(Player player, String title, String text,String menuFileName){
        FormModal form = new FormModal(title,text, "返回上个界面","取消");
        player.showFormWindow(form.onResponded(response -> {
            if(response){
                showUI(player, RYSMenuXMain.getMenus().get(menuFileName),menuFileName);
            }
        }));
    }

    public static void tipUI(Player player, String title, String text){
        FormModal form = new FormModal(title,text, "知道了","取消");
        player.showFormWindow(form.onResponded(response -> {
        }));
    }

    public static void actButton(Player player,BaseButton button,String menuFileName){
        switch (button.getType()){
            case 1-> button.costMoneyExecuteCmd(player,"",menuFileName);
            case 2->{
                InputButton button1 = (InputButton)button;
                FormCustom form = new FormCustom();form.addElement(new ElementInput(button1.getTip(),button1.getText()));
                player.showFormWindow(form.onResponded(response -> button1.costMoneyExecuteCmd(player,response.getInputResponse(0),menuFileName)));

            }
            case 3->{
                ChoseButton button1 = (ChoseButton) button;
                FormCustom form = new FormCustom();form.addElement(new ElementDropdown(button1.getTip(),button1.getList()));
                player.showFormWindow(form.onResponded(response -> button1.costMoneyExecuteCmd(player,response.getDropdownResponse(0).getElementContent(),menuFileName)));
            }
            case 4->{
                SliderButton button1 = (SliderButton) button;
                FormCustom form = new FormCustom();form.addElement(new ElementSlider(button1.getTip(),(int)Math.floor(button1.getMin()),(int)Math.floor(button1.getMax()),1,(int)Math.floor(button1.getMin())));
                player.showFormWindow(form.onResponded(response -> button1.costMoneyExecuteCmd(player,button1.getMultiplier()*(int)Math.floor(response.getSliderResponse(0))+"",menuFileName)));
            }
            case 5->{
                ModalButton button1 = (ModalButton) button;
                FormModal form = new FormModal(button1.getTip(),"确认","取消");
                player.showFormWindow(form.onResponded(response -> {
                    if(response){
                        button1.costMoneyExecuteCmd(player,"",menuFileName);
                    }
                }));
            }
        }
    }
}
