package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame implements ApplicationListener {

	public Environment environment;

	public CameraInputController camController;

	public PerspectiveCamera cam;

	public ModelBatch modelBatch;
	public Model model;


	public AssetManager assets;
	public Array<ModelInstance> instances = new Array<ModelInstance>();

	public boolean loading;

	public Array<ModelInstance> blocks = new Array<ModelInstance>();
	public Array<ModelInstance> invaders = new Array<ModelInstance>();
	public ModelInstance ship;
	public ModelInstance space;


	@Override
	public void create () {
		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));



		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f, 7f, 10f);
		cam.lookAt(0,0,0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		/*
		ModelLoader loader = new ObjLoader();
		model = loader.loadModel(Gdx.files.internal("loadmodels/data/ship.obj"));
		instance = new ModelInstance(model);
*/

		/*
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createBox(5f, 5f, 5f,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		instance = new ModelInstance(model);
		*/



		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		assets = new AssetManager();

		assets.load("loadmodels/data/ship.obj", Model.class);
		assets.load("loadmodels/data/block.obj", Model.class);
		assets.load("loadmodels/data/invader.obj", Model.class);
		assets.load("loadmodels/data/spacesphere.obj", Model.class);

		loading = true;

	}


	@Override
	public void resize(int width, int height) {

	}

	private void doneLoading() {
		ship = new ModelInstance(assets.get("loadmodels/data/ship.obj", Model.class));
		ship.transform.setToRotation(Vector3.Y, 180).trn(0, 0, 6f);
		instances.add(ship);

		Model blockModel = assets.get("loadmodels/data/block.obj", Model.class);
		for (float x = -5f; x <= 5f; x += 2f) {
			ModelInstance block = new ModelInstance(blockModel);
			block.transform.setToTranslation(x, 0, 3f);
			instances.add(block);
			blocks.add(block);
		}

		Model invaderModel = assets.get("loadmodels/data/invader.obj", Model.class);
		for (float x = -5f; x <= 5f; x += 2f) {
			for (float z = -8f; z <= 0f; z += 2f) {
				ModelInstance invader = new ModelInstance(invaderModel);
				invader.transform.setToTranslation(x, 0, z);
				instances.add(invader);
				invaders.add(invader);
			}
		}

		space = new ModelInstance(assets.get("loadmodels/data/spacesphere.obj", Model.class));


		loading = false;
	}

	@Override
	public void render() {
		if (loading && assets.update())
			doneLoading();

		camController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(instances,environment);

		if (space != null)
			modelBatch.render(space);

		modelBatch.end();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		instances.clear();
		assets.dispose();
	}

}
