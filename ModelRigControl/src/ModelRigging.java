package com.biomium.cuppy;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.Array;

public class ModelRigging implements ApplicationListener {
	PerspectiveCamera cam;
	CameraInputController camController;
	ModelBatch modelBatch;
	AssetManager assets;
	Array <ModelInstance> modelInstances = new Array <ModelInstance>( );
	ModelInstance cuppyInstance;	//  Cuppy is part cube, part guppy.
	float tailFrameTime = 0;
	float tailMotion = 0;
	float tailDirection = 1;
	Environment environment;
	boolean loading;
	
	@Override
	public void create() {
		modelBatch = new ModelBatch( );
		environment = new Environment( );
		environment.set( new ColorAttribute( ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1 ) );
		environment.add( new DirectionalLight( ).set( 0.8f, 1.0f, 1.0f, -1, -0.8f, -0.2f ) );
		
		cam = new PerspectiveCamera( 35, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
		cam.position.set( 10f, 10f, 10f );
		cam.lookAt( 0, 0, 0 );
		cam.near = 0.1f;
		cam.far = 300f;
		cam.update( );
		
		camController = new CameraInputController( cam );
		Gdx.input.setInputProcessor( camController );
		
		assets = new AssetManager( );
		assets.load("data/cuppy.g3dj", Model.class );
		loading = true;
	}
	
	private void doneLoading( ) {
		Model cuppy = assets.get( "data/cuppy.g3dj" );
		cuppyInstance = new ModelInstance( cuppy );
		modelInstances.add( cuppyInstance );
		loading = false;
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		modelInstances.clear();
		assets.dispose();
	}

	@Override
	public void render() {	
		if( loading && assets.update() ) {
			doneLoading( );
		}
		camController.update( );
		
		Gdx.gl.glViewport( 0, 0, Gdx.graphics.getWidth( ), Gdx.graphics.getHeight( ) );
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );

		cuppyUpdate( );
		
		modelBatch.begin( cam );
		modelBatch.render( modelInstances, environment );
		modelBatch.end( );
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	void cuppyUpdate( ) {
		if( loading ) {
			return;
		}
		tailFrameTime += Gdx.graphics.getDeltaTime( );
		if( tailFrameTime < 0.05f ) {
			return;
		}
		tailMotion += ( tailFrameTime * tailDirection );
		tailFrameTime = 0;
		if( tailMotion < 0 ) {
			tailMotion = 0;
			tailDirection = 1;
		}
		if( tailMotion > 0.5f ) {
			tailMotion = 0.5f;
			tailDirection = -1;
		}
		float tailPosition = ( tailMotion - 0.25f ) * 0.5f;		//  center and scale the motion

		Node tailBoneNode = cuppyInstance.getNode("tail_bone");
		Quaternion rotation = tailBoneNode.rotation;
		rotation.y = tailPosition;
		cuppyInstance.calculateTransforms();
	}
}
