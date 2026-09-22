package com.example.sellbot;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerInput;

public final class SellBotClient implements ClientModInitializer {
    private static final int SELL_BUTTON_SLOT = 44;
    private static final int PLAYER_INVENTORY_START = 45;
    private static final int PLAYER_INVENTORY_END = 80;

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("sellbot", "main"));

    private static final KeyMapping TOGGLE_KEY = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.sellbot.toggle",
                    InputConstants.Type.KEYSYM,
                    InputConstants.KEY_RSHIFT,
                    CATEGORY
            )
    );

    private boolean enabled;
    private int state;
    private int waitTicks;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(Minecraft client) {
        while (TOGGLE_KEY.consumeClick()) {
            enabled = !enabled;
            state = 0;
            waitTicks = 0;

            if (client.player != null) {
                client.player.sendSystemMessage(
                        Component.literal("SellBot " + (enabled ? "enabled" : "disabled"))
                );
            }
        }

        if (!enabled || client.player == null) {
            return;
        }

        if (waitTicks > 0) {
            waitTicks--;
            return;
        }

        if (state == 0) {
            if (inventoryIsFull(client)) {
                client.player.connection.sendCommand("sell");
                state = 1;
                waitTicks = 10;
            }
            return;
        }

        if (!(client.gui.screen() instanceof AbstractContainerScreen<?> screen)) {
            return;
        }

        if (state == 1) {
            for (int slot = PLAYER_INVENTORY_START; slot <= PLAYER_INVENTORY_END; slot++) {
                client.gameMode.handleContainerInput(
                        screen.getMenu().containerId,
                        slot,
                        0,
                        ContainerInput.QUICK_MOVE,
                        client.player
                );
            }

            state = 2;
            waitTicks = 5;
            return;
        }

        if (state == 2) {
            client.gameMode.handleContainerInput(
                    screen.getMenu().containerId,
                    SELL_BUTTON_SLOT,
                    0,
                    ContainerInput.PICKUP,
                    client.player
            );
            state = 3;
            waitTicks = 10;
            return;
        }

        if (state == 3) {
            client.gui.setScreen(null);
            state = 0;
            waitTicks = 20;
        }
    }

    private boolean inventoryIsFull(Minecraft client) {
        return client.player.getInventory().getNonEquipmentItems().stream()
                .allMatch(stack -> !stack.isEmpty());
    }
}
