package com.flaiker.zero.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import java.util.ArrayList;

/**
 * Created by Flaiker on 21.12.2014.
 */
public class AbilityList {
    private ScrollPane    scrollPane;
    private VerticalGroup verticalGroup;
    private ButtonGroup   buttonGroup;

    private boolean               open;
    private ArrayList<AbilityListItem> abilityList;

    public AbilityList(Skin skin) {
        abilityList = new ArrayList<>();
        open = false;

        verticalGroup = new VerticalGroup();

        buttonGroup = new ButtonGroup();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setUncheckLast(true);

        scrollPane = new ScrollPane(verticalGroup, skin);
        scrollPane.setPosition(0, 200);
        scrollPane.setSize(160, 400);
        scrollPane.setOverscroll(false, false);
        scrollPane.setScrollingDisabled(true, false);
        Color color = scrollPane.getColor();
        scrollPane.setColor(color.r, color.g, color.b, 0.8f);

        hide();
    }

    private void updateList() {
        verticalGroup.clear();
        buttonGroup.clear();

        for (AbilityListItem item : abilityList) {
            verticalGroup.addActor(item.getImageTextButton());
            verticalGroup.fill();
            buttonGroup.add(item.getImageTextButton());
        }
    }

    public void addAbility(AbilityListItem item) {
        abilityList.add(item);
        updateList();
    }

    public void removeAbility(AbilityListItem item) {
        abilityList.remove(item);
        updateList();
    }

    public Actor getActor() {
        return scrollPane;
    }

    public void show() {
        scrollPane.setVisible(true);
        open = true;
    }

    public void hide() {
        scrollPane.setVisible(false);
        open = false;
    }

    public void switchState() {
        if (open) hide();
        else show();
    }
}
