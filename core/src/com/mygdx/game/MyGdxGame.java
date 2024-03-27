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
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame implements ApplicationListener {

	public Environment environment;

	public CameraInputController camController;

	public PerspectiveCamera cam;

	public ModelBatch modelBatch;
	public Model model;

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
		cam.position.set(0f, 7f, -10f);
		cam.lookAt(0,0,0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		ModelLoader modelLoader = new G3dModelLoader(new JsonReader());
		ModelData modelData = modelLoader.loadModelData(Gdx.files.internal("loadmodels/data/invaders.g3dj"));
		model = new Model(modelData, new TextureProvider.FileTextureProvider());
		doneLoading();
	}


	@Override
	public void resize(int width, int height) {

	}

	private void doneLoading() {
		for (int i = 0; i < model.nodes.size; i++) {
			String id = model.nodes.get(i).id;
			ModelInstance instance = new ModelInstance(model, id);
			Node node = instance.getNode(id);

			instance.transform.set(node.globalTransform);



			node.translation.set(0,0,0);
			node.scale.set(1,1,1);
			node.rotation.idt();
			instance.calculateTransforms();

			if (id.equals("space")) {
				space = instance;
				continue;
			}

			instances.add(instance);

			if (id.equals("ship")) {
				ship = instance;
			}else if (id.startsWith("block")) {
				blocks.add(instance);
			}else if (id.startsWith("invader")) {
				invaders.add(instance);
			}
		}

		for (ModelInstance block : blocks) {
			float r = 0.5f + 0.5f * (float)Math.random();
			float g = 0.5f + 0.5f * (float)Math.random();
			float b = 0.5f + 0.5f * (float)Math.random();
			block.materials.get(0).set(ColorAttribute.createDiffuse(r, g, b, 1));
		}
	}

	@Override
	public void render() {

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
		model.dispose();
	}

}
