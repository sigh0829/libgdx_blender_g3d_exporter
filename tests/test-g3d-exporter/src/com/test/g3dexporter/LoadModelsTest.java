/*
#################################################################################
# Copyright 2014 See AUTHORS file.
#
# Licensed under the GNU General Public License Version 3.0 (the "LICENSE");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.gnu.org/licenses/gpl-3.0.txt
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#################################################################################
 */

package com.test.g3dexporter;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.JsonReader;

/** Loads a model generated by the Blender G3DJ Exporter addon.
 * 
 * @author Dancovich */
public class LoadModelsTest implements ApplicationListener {
	public Environment environment;
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public ModelBatch modelBatch;
	public Model model;
	public ModelInstance instance;

	public AnimationController animationController;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		modelBatch = new ModelBatch();

		// creates some basic lighting
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		// Camera uses Y-Up and positions itself at (0, 0, 5) coordinate, looking at origin
		cam = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f, 0f, 5f);
		cam.up.set(0f, 1f, 0f);
		cam.lookAt(0, 0, 0);
		cam.near = 0.1f;
		cam.far = 300f;
		cam.update();

		// Here we load our model
		G3dModelLoader loader = new G3dModelLoader(new JsonReader());
		model = loader.loadModel(Gdx.files.internal("data/soldier.g3dj"));
		instance = new ModelInstance(model);

		/*
		 * IntAttribute noCullFace = IntAttribute.createCullFace(GL20.GL_NONE); for (NodePart part :
		 * instance.getNode("Cabelo").parts){ part.material.set(noCullFace); } for (NodePart part : instance.getNode("Saia").parts){
		 * part.material.set(noCullFace); }
		 */

		// Used to control the camera
		camController = new CameraInputController(cam);

		// Used to run the exported animation
		animationController = new AnimationController(instance);
		animationController.setAnimation("Run", -1);

		InputMultiplexer mx = new InputMultiplexer();
		mx.addProcessor(camController);
		Gdx.input.setInputProcessor(mx);
	}

	@Override
	public void render () {
		// Update out camera and animation
		camController.update();
		animationController.update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Render the model
		modelBatch.begin(cam);
		modelBatch.render(instance, environment);
		modelBatch.end();
	}

	@Override
	public void dispose () {
		modelBatch.dispose();
		model.dispose();
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

}
