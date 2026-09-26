package com.example.happyghast;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {

    private static final Category CATEGORY = Category.create(Identifier.of(HappyGhastParkMod.MOD_ID, "controls"));
    private static KeyBinding sprintKey;
    private static boolean wasSprinting = false;

    public static void register() {
        sprintKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.happyghastpark.sprint", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, CATEGORY)
        );

        ClientTickEvents.END_CLIENT_TICK.register(ModKeyBindings::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
      if (client == null || client.player == null) {
            wasSprinting = false;
            return;
        }
        boolean keyBindingDown = sprintKey != null && (sprintKey.isPressed() || sprintKey.wasPressed());
        boolean inputSprinting = false;
        if (client.player.input != null && client.player.input.playerInput != null) {
            inputSprinting = client.player.input.playerInput.sprint();
          }

        boolean isSprintingNow = keyBindingDown || inputSprinting;

        if (isSprintingNow != wasSprinting) {
            ClientPlayNetworking.send(new GhastSprintPayload(isSprintingNow));
            wasSprinting = isSprintingNow;
        }
    }
}