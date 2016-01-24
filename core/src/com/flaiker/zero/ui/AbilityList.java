/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.flaiker.zero.abilities.AbstractAbility;
import com.flaiker.zero.entities.Player;

import java.util.ArrayList;

/**
 * Created by Flaiker on 21.12.2014.
 */
public class AbilityList {
    private ScrollPane    scrollPane;
    private VerticalGroup verticalGroup;
    private ButtonGroup   buttonGroup;

    private boolean                    open;
    private ArrayList<AbstractAbility> abilityList;
    private Player                     player;

    public AbilityList(Player player, Skin skin) {
        abilityList = new ArrayList<>();
        open = false;
        this.player = player;

        verticalGroup = new VerticalGroup();

        buttonGroup = new ButtonGroup();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setUncheckLast(true);

        scrollPane = new ScrollPane(verticalGroup, skin);
        scrollPane.setPosition(-160, 200);
        scrollPane.setSize(160, 400);
        scrollPane.setOverscroll(false, false);
        scrollPane.setScrollingDisabled(true, false);
        Color color = scrollPane.getColor();
        scrollPane.setColor(color.r, color.g, color.b, 0.8f);
    }

    private void updateList() {
        verticalGroup.clear();
        buttonGroup.clear();

        for (final AbstractAbility item : abilityList) {
            verticalGroup.addActor(item.getImageTextButton());
            verticalGroup.fill();
            ImageTextButton button = item.getImageTextButton();
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (actor instanceof ImageTextButton) {
                        if (((ImageTextButton) actor).isChecked())
                            selectAbility((AbstractAbility) actor.getUserObject());
                    }
                }
            });
            buttonGroup.add(button);
        }
    }

    private void selectAbility(AbstractAbility ability) {
        player.switchSelectedAbility(ability);
    }

    public void addAbility(AbstractAbility item) {
        abilityList.add(item);
        updateList();
    }

    public void removeAbility(AbstractAbility item) {
        abilityList.remove(item);
        updateList();
    }

    public Actor getActor() {
        return scrollPane;
    }

    public void show() {
        scrollPane.addAction(Actions.moveTo(0, 200, 1));
        open = true;
    }

    public void hide() {
        scrollPane.addAction(Actions.moveTo(-160, 200, 1));
        open = false;
    }

    public void switchState() {
        if (open) hide();
        else show();
    }
}
