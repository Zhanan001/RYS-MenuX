package cn.reiyans.RYSMenuX.Menu;

import cn.nukkit.Player;
import cn.nukkit.form.element.*;
import cn.reiyans.RYSMenuX.Button.*;
import cn.reiyans.RYSMenuX.RYSMenuXMain;
import moe.him188.gui.window.FormCustom;
import moe.him188.gui.window.FormModal;
import moe.him188.gui.window.FormSimple;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

public class MenuForm {

    public static void showUI(Player player, Menu menu, String menuFileName){
        String title = RYSMenuXMain.getInstance().replaceVariables(player, menu.getTitle());
        String body = RYSMenuXMain.getInstance().replaceVariables(player, menu.getText());
        FormSimple form = new FormSimple(title, body);
        LinkedHashMap<Integer,BaseButton> buttonTemp = new LinkedHashMap<>();
        int i = 0;
        for(BaseButton button : menu.getButtons()){
            if(button.getPermission().equals("true") || player.hasPermission(button.getPermission())){
                String btnName = RYSMenuXMain.getInstance().replaceVariables(player, button.getName());
                if(button.isEnableTexture()){
                    form.addButton(new ElementButton(btnName, new ElementButtonImageData("path", button.getTexture())));
                } else {
                    form.addButton(btnName);
                }
                buttonTemp.put(i, button);
                i++;
            }
        }
        player.showFormWindow(form.onClicked(response -> actButton(player, buttonTemp.get(response), menuFileName)));
    }

    public static void noticeUI(Player player, String title, String text, String menuFileName){
        String t = RYSMenuXMain.getInstance().replaceVariables(player, title);
        String b = RYSMenuXMain.getInstance().replaceVariables(player, text);
        FormModal form = new FormModal(t, b, "返回上个界面", "取消");
        player.showFormWindow(form.onResponded(response -> {
            if(response){
                showUI(player, RYSMenuXMain.getMenus().get(menuFileName), menuFileName);
            }
        }));
    }

    public static void tipUI(Player player, String title, String text){
        String t = RYSMenuXMain.getInstance().replaceVariables(player, title);
        String b = RYSMenuXMain.getInstance().replaceVariables(player, text);
        FormModal form = new FormModal(t, b, "知道了", "取消");
        player.showFormWindow(form.onResponded(response -> {
        }));
    }

    public static void actButton(Player player, BaseButton button, String menuFileName){
        if(button == null) return;
        switch (button.getType()){
            case 1 -> button.costMoneyExecuteCmd(player, "", menuFileName);
            case 2 -> {
                InputButton button1 = (InputButton) button;
                String tip = RYSMenuXMain.getInstance().replaceVariables(player, button1.getTip());
                String def = RYSMenuXMain.getInstance().replaceVariables(player, button1.getText());
                FormCustom form = new FormCustom();
                form.addElement(new ElementInput(tip, def));
                player.showFormWindow(form.onResponded(response ->
                        button1.costMoneyExecuteCmd(player, response.getInputResponse(0), menuFileName)));
            }
            case 3 -> {
                ChoseButton button1 = (ChoseButton) button;
                String tip = RYSMenuXMain.getInstance().replaceVariables(player, button1.getTip());
                // replace each list item
                List<String> rawList = button1.getList();
                ArrayList<String> replaced = new ArrayList<>();
                for(String item : rawList){
                    replaced.add(RYSMenuXMain.getInstance().replaceVariables(player, item));
                }
                FormCustom form = new FormCustom();
                form.addElement(new ElementDropdown(tip, replaced));
                player.showFormWindow(form.onResponded(response ->
                        button1.costMoneyExecuteCmd(player, response.getDropdownResponse(0).getElementContent(), menuFileName)));
            }
            case 4 -> {
                SliderButton button1 = (SliderButton) button;
                String tip = RYSMenuXMain.getInstance().replaceVariables(player, button1.getTip());
                int min = (int)Math.floor(button1.getMin());
                int max = (int)Math.floor(button1.getMax());
                int def = min;
                FormCustom form = new FormCustom();
                form.addElement(new ElementSlider(tip, min, max, 1, def));
                player.showFormWindow(form.onResponded(response ->
                        button1.costMoneyExecuteCmd(player, button1.getMultiplier() * (int)Math.floor(response.getSliderResponse(0)) + "", menuFileName)));
            }
            case 5 -> {
                ModalButton button1 = (ModalButton) button;
                String tip = RYSMenuXMain.getInstance().replaceVariables(player, button1.getTip());
                FormModal form = new FormModal(tip, "确认", "取消");
                player.showFormWindow(form.onResponded(response -> {
                    if(response){
                        button1.costMoneyExecuteCmd(player, "", menuFileName);
                    }
                }));
            }
        }
    }
}
