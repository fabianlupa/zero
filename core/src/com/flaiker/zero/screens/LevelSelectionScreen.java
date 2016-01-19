package com.flaiker.zero.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.flaiker.zero.Zero;
import com.flaiker.zero.helper.DefaultActorListener;
import com.flaiker.zero.services.rmtasks.LoadIngameAssetsTask;

/**
 * Screen for level-selection
 */
public class LevelSelectionScreen extends AbstractScreen {
    public static final String LOG = LevelSelectionScreen.class.getSimpleName();

    private static final String DEFAULT_LEVEL_FOLDER = "maps";
    private static final String USER_LEVEL_FOLDER    = "userlevels";
    private static final String MAP_LIST_FILENAME    = "levellist";

    private Array<LevelHolder> levelArray;

    public LevelSelectionScreen(Zero zero) {
        super(zero);
        fillLevelArray();
    }

    private void fillLevelArray() {
        levelArray = new Array<>();
        int defaultMapCount, userLevelCount = 0;

        // Load internal levels
        FileHandle maplist = Gdx.files.internal(DEFAULT_LEVEL_FOLDER + "/" + MAP_LIST_FILENAME);
        String fileContent = maplist.readString();
        String[] entries = fileContent.split("\\r?\\n");
        defaultMapCount = entries.length;

        for (String entry : entries) {
            String[] entryParts = entry.split("\\r?\\|");
            levelArray.add(new LevelHolder(entryParts[0], entryParts[1], true));
        }

        // TODO: think about loading levels from outside the jar
        // Load external levels
        FileHandle externalMapsFolder = Gdx.files.local(USER_LEVEL_FOLDER);
        if (!externalMapsFolder.exists()) {
            externalMapsFolder.mkdirs();
        }

        FileHandle[] externalMaps = externalMapsFolder.list();
        for (FileHandle map : externalMaps) {
            if (map.extension().equals("tmx")) {
                levelArray.add(new LevelHolder(map.nameWithoutExtension(), map.path(), false));
                userLevelCount++;
            }
        }

        Gdx.app.log(LOG, "Found " + defaultMapCount + " defaultlevels and " + userLevelCount + " userlevels");
    }

    @Override
    public void show() {
        super.show();

        Table table = new Table(skin);
        table.setFillParent(true);
        uiStage.addActor(table);

        table.add().padBottom(50).row();

        // Title
        Label titleLabel = new Label("ZERO", skin, "digital7-92", Color.WHITE);
        table.add(titleLabel).spaceBottom(5).align(1);
        table.row();

        // Subtitle
        table.add("Level Selection").align(1).spaceBottom(20);
        table.row();

        // Level list
        final List levelList = new List(skin);
        levelList.setItems(levelArray);
        levelList.addListener(new DefaultActorListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                zero.setScreen(new LoadingScreen(zero, new LoadingScreen.LoadingCalls() {
                    @Override
                    public void doLoad() {
                        // Disable ingame asset regeneration task for now as it is replaced by the AssetBuilder
                        //if (zero.isDebugMode()) zero.getResourceManager().addTaskToQueue(new RefreshAtlasesTask());
                        zero.getResourceManager().addTaskToQueue(new LoadIngameAssetsTask(zero.getResourceManager()
                                                                                              .getAssetManager()));
                        zero.getResourceManager().runThroughTaskQueue();
                    }

                    @Override
                    public float reportProgress() {
                        if (!zero.getResourceManager().isDoneLoading())
                            return zero.getResourceManager().getLoadingPercent();
                        else return 1f;
                    }

                    @Override
                    public boolean isFinished() {
                        return zero.getResourceManager().isDoneLoading();
                    }

                    @Override
                    public void finishLoading() {
                        zero.setScreen(new GameScreen(zero, ((LevelHolder) levelList.getSelected()).getFileHandle()));
                    }

                    @Override
                    public String getCurrentLoadingMessage() {
                        return zero.getResourceManager().getCurrentTaskDescription();
                    }
                }));
            }
        });

        final ScrollPane scrollPane = new ScrollPane(levelList, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setColor(1, 1, 1, 0.9f);
        table.add(scrollPane).expand().fill().pad(0, 150, 25, 150);
        table.row();

        // Button "Back"
        TextButton backButton = new TextButton("BACK", skin);
        backButton.setColor(1, 1, 1, 0.9f);
        backButton.addListener(new DefaultActorListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                zero.setScreen(new MenuScreen(zero));
            }
        });
        table.add(backButton).fill().pad(0, 150, 25, 150);
        table.row();

        // Footer
        table.add(new Label("www.flaiker.com", skin)).row();
        table.add().padBottom(25).row();

        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    protected String getName() {
        return LOG;
    }

    /**
     * Class representing an unloaded level
     */
    private class LevelHolder {
        private String  name;
        private String  file;
        private boolean defaultLevel;

        public LevelHolder(String name, String file, boolean defaultLevel) {
            this.name = name;
            this.file = file;
            this.defaultLevel = defaultLevel;
        }

        public String getName() {
            return name;
        }

        public String getFile() {
            return file;
        }

        public boolean isDefaultLevel() {
            return defaultLevel;
        }

        public FileHandle getFileHandle() {
            if (isDefaultLevel()) return Gdx.files.internal(DEFAULT_LEVEL_FOLDER + "/" + file);
            else return Gdx.files.local(USER_LEVEL_FOLDER + "/" + file);
        }

        @Override
        public String toString() {
            return name + " (" + file + ")";
        }
    }
}
