package com.flaiker.zero.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.flaiker.zero.Zero;

/**
 * Created by Flaiker on 27.12.2014.
 */
public class LoadingScreen extends AbstractScreen {
    public static final String LOG = LoadingScreen.class.getSimpleName();

    private LoadingCalls loadingCalls;
    private ProgressBar  progressBar;
    private Label        loadingMessage;

    public LoadingScreen(Zero zero, LoadingCalls loadingCalls) {
        super(zero);
        this.loadingCalls = loadingCalls;
    }

    @Override
    public void show() {
        super.show();
        // add ui
        Table table = new Table(skin);
        table.setSize(500, 100);
        table.setPosition(AbstractScreen.SCREEN_WIDTH / 2f - table.getWidth() / 2f,
                          AbstractScreen.SCREEN_HEIGHT / 2f - table.getHeight() / 2f);
        uiStage.addActor(table);

        loadingMessage = new Label(loadingCalls.getCurrentLoadingMessage(), skin);
        table.add(loadingMessage).fillX().row();

        progressBar = new ProgressBar(0, 1, 0.1f, false, skin);
        table.add(progressBar).fillX();

        // start loading
        loadingCalls.doLoad();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        float progress = loadingCalls.reportProgress();
        progressBar.setValue(progress);
        loadingMessage.setText(loadingCalls.getCurrentLoadingMessage());
        if (loadingCalls.isFinished()) loadingCalls.finishLoading();
    }

    public interface LoadingCalls {
        void doLoad();
        float reportProgress();
        boolean isFinished();
        void finishLoading();
        String getCurrentLoadingMessage();
    }

    @Override
    protected String getName() {
        return LOG;
    }
}
