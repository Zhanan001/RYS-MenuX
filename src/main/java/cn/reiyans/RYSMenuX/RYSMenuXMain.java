package cn.reiyans.RYSMenuX;

import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemApple;
import cn.nukkit.item.ItemClock;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.reiyans.RYSMenuX.Button.*;
import cn.reiyans.RYSMenuX.Command.MenuCommand;
import cn.reiyans.RYSMenuX.Listener.MenuListener;
import cn.reiyans.RYSMenuX.Menu.Menu;
import cn.reiyans.RYSMenuX.Menu.MenuCheckTask;
import cn.reiyans.RYSMenuX.variable.VariableManageHook;
import lombok.Getter;
import cn.nukkit.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class RYSMenuXMain extends PluginBase {

    @Getter
    private static RYSMenuXMain instance;
    @Getter
    private static ConsoleCommandSender console;
    @Getter
    private static LinkedHashMap<String,Menu> menus;
    @Getter
    private static boolean itemSwitcher;
    @Getter
    private static Item item;
    @Getter
    private static String itemID;
    @Getter
    private static String itemName;
    @Getter
    private static boolean itemIsDrop;
    @Getter
    private static boolean itemIsEnchanting;
    @Getter
    private static int itemCheckTime = 100;//default value
    @Getter
    private static String moneyName;

    @Getter
    private VariableManageHook variableManageHook;

    public void onLoad() {
        instance = this;
        this.saveDefaultConfig();
        this.saveResource("main.yml");
        this.getLogger().info("正在启动RYSMenuX！");
    }

    @Override
    public void onEnable() {
        createPagesFolder(this.getPagesFile());
        console = new ConsoleCommandSender();
        menus = new LinkedHashMap<>();
        itemSwitcher = this.getConfig().getBoolean("MenuItemSwitcher");
        itemID = this.getConfig().getString("MenuItemID");
        itemName = this.getConfig().getString("MenuItemName");
        itemIsDrop = this.getConfig().getBoolean("MenuItemIsDrop");
        itemIsEnchanting = this.getConfig().getBoolean("MenuItemIsEnchanting");
        itemCheckTime = this.getConfig().getInt("MenuItemCheckTime");
        moneyName = this.getConfig().getString("MoneyName");
        //物品先加载信息再初始化
        item = getMenuItem();
        // 初始化 VariableManage Hook（软依赖）
        this.variableManageHook = new VariableManageHook();
        if(this.variableManageHook.isAvailable()){
            this.getLogger().info("已检测到 VariableManage，变量替换已启用。");
        } else {
            this.getLogger().info("未检测到 VariableManage，使用回退替换（仅支持 %p/%i）。");
        }
        //遗失菜单检测任务
        if(itemSwitcher){
            Server.getInstance().getScheduler().scheduleRepeatingTask(new MenuCheckTask(this), getItemCheckTime()*20);
        }
        //主菜单加载
        loadOneMenu(this.getDataFolder(),"main");
        //pages目录下的子菜单加载
        loadAllMenu(this.getPagesFile());
        this.getServer().getCommandMap().register("MenuX",new MenuCommand("MenuX"));
        this.getServer().getPluginManager().registerEvents(new MenuListener(),this);
        this.getLogger().info("RYSMenuX已加载完成！");
    }

    @Override
    public void onDisable() {this.getLogger().info("正在关闭RYSMenuX！");}

    private void loadOneMenu(File file,String menuFileName){
        Config menuConfig = new Config(file.toString() + "/" + menuFileName + ".yml", Config.YAML);
        menus.put(menuFileName,new Menu(menuConfig.getString("PageTitle"),menuConfig.getString("PageText"), loadMenuConfig(menuConfig)));
        this.getLogger().info("加载菜单页面>"+menuFileName);
    }

    private void loadAllMenu(File file){
        for(String menuName:this.getAllMenuFileNames(file)){
            Config menuConfig = new Config(this.getPagesFile().toString() + "/" + menuName + ".yml", Config.YAML);
            menus.put(menuName,new Menu(menuConfig.getString("PageTitle"),menuConfig.getString("PageText"), loadMenuConfig(menuConfig)));
            this.getLogger().info("加载菜单页面>"+menuName);
        }
    }

    public ArrayList<BaseButton> loadMenuConfig(Config menuConfig){
        ArrayList<BaseButton> buttons = new ArrayList<>();
        for(int i=1;menuConfig.exists("Button"+i);i++){
            int type =  menuConfig.getInt("Button"+i+."type");
            String name =  menuConfig.getString("Button"+i+."name");
            String texture =  menuConfig.getString("Button"+i+."texture");
            int money =  menuConfig.getInt("Button"+i+."money");
            List<String> allCmds =  menuConfig.getStringList("Button"+i+."cmd");
            String permission =  menuConfig.getString("Button"+i+."permission");
            switch (type){
                case 1-> buttons.add(new NormalButton(type,name,texture,money,allCmds,permission));
                case 2-> {
                    String tip = menuConfig.getString("Button"+i+."tip");
                    String text = menuConfig.getString("Button"+i+."text");
                    buttons.add(new InputButton(type,name,texture,money,allCmds,tip,text,permission));
                }
                case 3-> {
                    String tip = menuConfig.getString("Button"+i+."tip");
                    List<String> list = menuConfig.getStringList("Button"+i+."list");
                    buttons.add(new ChoseButton(type,name,texture,money,allCmds,tip,new ArrayList<>(list),permission));
                }
                case 4-> {
                    String tip = menuConfig.getString("Button"+i+."tip");
                    double multiplier = menuConfig.getDouble("Button"+i+."multiplier");
                    int min = menuConfig.getInt("Button"+i+."min");
                    int max = menuConfig.getInt("Button"+i+."max");
                    buttons.add(new SliderButton(type,name,texture,money,allCmds,tip,multiplier,min,max,permission));
                }
                case 5-> {
                    String tip = menuConfig.getString("Button"+i+."tip");
                    buttons.add(new ModalButton(type,name,texture,money,allCmds,tip,permission));
                }
            }
        }
        this.getLogger().info("该菜单页面共加载了:+buttons.size()+个按钮!");
        return buttons;
    }

    public File getPagesFile() {return new File(this.getDataFolder() + "/pages");}

    private void createPagesFolder(File file){
        if (!file.exists()) {
            if(file.mkdirs()){
                this.getLogger().info(file.getName()+"(菜单页面)文件夹为空，已重新创建!");
            }
        }
    }

    private String[] getAllMenuFileNames(File file){
        LinkedList<String> names = new LinkedList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File itemFile : files) {
                    String name = itemFile.getName().substring(0, itemFile.getName().lastIndexOf("."));
                    names.add(name);
                }
            }
        }
        return names.toArray(new String[0]);
    }

    private static Item getMenuItem(){
        String[] itemID = getItemID().split(":");
        Item item;
        try {
            item = Item.AIR_ITEM;
        }catch (Exception e){
            // 回退到 ID 获取
            item = Item.get(0);
        }
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("menux",true);
        if(isInteger(itemID[0]) && isInteger(itemID[1])){
            item = new Item(Integer.parseInt(itemID[0]),Integer.parseInt(itemID[1]),1);
            item.setCompoundTag(tag);
            item.setCustomName(getItemName());
            if(isItemIsEnchanting()){
                item.addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL));
            }
        }else if(!getInstance().getServer().getName().equals("Nukkit") && itemID.length == 2){
            item = Item.fromString(getItemID());
            item.setCount(1);
            item.setCompoundTag(tag);
            item.setCustomName(getItemName());
            if(isItemIsEnchanting()){
                item.addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL));
            }
        }else{
            getInstance().getLogger().error("您的MenuItemID菜单物品ID不符规范!请关服后前往config.yml中修改!");
        }
        return item;
    }

    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 对文本进行变量替换：先做基础替换，再尝试 VariableManage
    public String replaceVariables(Player player, String text){
        if(text == null) return null;
        if(player != null){
            text = text.replace("%p", player.getName());
        }
        if(this.variableManageHook != null && this.variableManageHook.isAvailable()){
            return this.variableManageHook.replace(player, text);
        }
        return text;
    }
}
