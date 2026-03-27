package com.burot;

import com.burot.event.GameEventProcessor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanID;
import net.runelite.client.ui.PluginPanel;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.util.List;

public class BurotPanel extends PluginPanel {

    public BurotPanel(List<GameEventProcessor> activeEventProcessors, Client gameClient) {
        super();
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new GridLayout(0, 1, 0, 10));

        for (GameEventProcessor processor : activeEventProcessors) {
            JButton simulationButton = new JButton("Simulate " + processor.retrieveProcessorName());

            simulationButton.addActionListener(actionEvent -> {
                Player localPlayerEntity = gameClient.getLocalPlayer();
                String activePlayerName = (localPlayerEntity != null) ? localPlayerEntity.getName() : "DevPlayer";

                ClanChannel activeClanChannel = gameClient.getClanChannel(ClanID.CLAN);
                String activeClanName = (activeClanChannel != null) ? activeClanChannel.getName() : "";

                processor.simulateEventExecution(activePlayerName, activeClanName);
            });

            buttonContainer.add(simulationButton);
        }

        add(buttonContainer);
    }
}