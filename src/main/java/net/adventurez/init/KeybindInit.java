package net.adventurez.init;

import org.lwjgl.glfw.GLFW;

import net.adventurez.entity.DragonEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

@Environment(EnvType.CLIENT)
public class KeybindInit {

    public static KeyMapping dragonFlyDownKeyBind;
    public static KeyMapping dragonFireBreathBind;

    public static void init() {
        // Keybinds
        dragonFlyDownKeyBind = new KeyMapping("key.adventurez.dragonflydown", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "category.adventurez.keybind");
        dragonFireBreathBind = new KeyMapping("key.adventurez.dragonfirebreath", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "category.adventurez.keybind");
        // Registering
        KeyBindingHelper.registerKeyBinding(dragonFlyDownKeyBind);
        KeyBindingHelper.registerKeyBinding(dragonFireBreathBind);
        // Callback
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (dragonFlyDownKeyBind.isPressed()) {
                DragonEntity.flyDragonDown(client.player, dragonFlyDownKeyBind.getBoundKeyTranslationKey());
                return;
            } else if (dragonFireBreathBind.wasPressed()) {
                DragonEntity.dragonFireBreath(client.player);
                return;
            }
        });
    }

}
