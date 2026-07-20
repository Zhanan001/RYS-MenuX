package cn.reiyans.RYSMenuX.variable;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class VariableManageHook {
    private final Plugin plugin;
    private final Method replaceMethod;

    public VariableManageHook() {
        this.plugin = Server.getInstance().getPluginManager().getPlugin("VariableManage");
        this.replaceMethod = findReplaceMethod();
    }

    public boolean isAvailable() {
        return plugin != null && plugin.isEnabled() && replaceMethod != null;
    }

    public String replace(Player player, String text) {
        if (!isAvailable() || text == null) return text;
        try {
            Class<?>[] params = replaceMethod.getParameterTypes();
            Object result;
            if (params.length == 1) {
                result = replaceMethod.invoke(plugin, text);
            } else if (params.length == 2) {
                // try (Player, String) or (String, Player)
                if (params[0].isAssignableFrom(player.getClass())) {
                    result = replaceMethod.invoke(plugin, player, text);
                } else {
                    result = replaceMethod.invoke(plugin, text, player);
                }
            } else {
                return text;
            }
            return (result instanceof String) ? (String) result : text;
        } catch (Throwable t) {
            t.printStackTrace();
            return text;
        }
    }

    private Method findReplaceMethod() {
        if (plugin == null) return null;
        List<String> candidates = Arrays.asList("replaceVariables", "replace", "format", "parse", "parseVariables", "replaceAll");
        for (Method m : plugin.getClass().getMethods()) {
            if (candidates.contains(m.getName())) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 1 && p[0] == String.class) return m;
                if (p.length == 2 && (p[0] == String.class || p[1] == String.class || p[0].getSimpleName().equals("Player") || p[1].getSimpleName().equals("Player")))
                    return m;
            }
        }
        // fallback: accept any method returning String with 1 or 2 params
        for (Method m : plugin.getClass().getMethods()) {
            if (m.getReturnType() == String.class) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 1 && p[0] == String.class) return m;
                if (p.length == 2 && (p[0] == String.class || p[1] == String.class)) return m;
            }
        }
        return null;
    }
}
