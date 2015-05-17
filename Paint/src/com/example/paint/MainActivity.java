package com.example.paint;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	private float smallBrush, mediumBrush, largeBrush;
	private DrawingView drawView;
	private ImageButton currPaint, drawBtn,saveBtn;;
	private ImageView imgFoto;
	private int MEDIA_TYPE_IMAGE = 1;
	private int MEDIA_TYPE_VIDEO = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		drawView = (DrawingView)findViewById(R.id.drawingView1);
		LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
		drawView.setDrawingCacheEnabled(true);
		drawBtn = (ImageButton)findViewById(R.id.draw_btn);
		drawBtn.setOnClickListener(this);
		
		//Sİlgi boyutlarını atıyoruz...
		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);
		
		saveBtn = (ImageButton)findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(this);
		
		
	}
	
	@Override
	public void onClick(View view){
		if(view.getId()==R.id.draw_btn){
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Silgi Boyutu:");
			brushDialog.setContentView(R.layout.brush_chooser);
			ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setBrushSize(smallBrush);
			        drawView.setLastBrushSize(smallBrush);
			        brushDialog.dismiss();
			    }
			});
			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setBrushSize(mediumBrush);
			        drawView.setLastBrushSize(mediumBrush);
			        brushDialog.dismiss();
			    }
			});
			 
			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setBrushSize(largeBrush);
			        drawView.setLastBrushSize(largeBrush);
			        brushDialog.dismiss();
			    }
			});
			brushDialog.show();
			drawView.setBrushSize(mediumBrush);
		}   
		else if(view.getId()==R.id.save_btn){
            //save drawing
			
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Kaydet?");
			saveDialog.setMessage("Galeriye kayıt edilsin mi?");
			saveDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			    	
			    	byte[] data = convertBitmapToByteArray(drawView.getDrawingCache());
			    	onPictureTaken(data);
			    	imgFoto=(ImageView) findViewById(R.id.imgFoto);
			    	imgFoto.setImageBitmap(drawView.getDrawingCache());
			        //save drawing
//			    	drawView.setDrawingCacheEnabled(true);
//			    	String imgSaved = MediaStore.Images.Media.insertImage(
//			    		    getContentResolver(), drawView.getDrawingCache(),
//			    		    UUID.randomUUID().toString()+".png", "drawing");
//			    	if(imgSaved!=null){
//			    	    Toast savedToast = Toast.makeText(getApplicationContext(), 
//			    	        "Galeriye kayıt edildi!", Toast.LENGTH_SHORT);
//			    	    savedToast.show();
//			    	}
//			    	else{
//			    	    Toast unsavedToast = Toast.makeText(getApplicationContext(), 
//			    	        "Üzgünüz! Bir hata ile karşılaşıldı...", Toast.LENGTH_SHORT);
//			    	    unsavedToast.show();
//			    	}
//			    	drawView.destroyDrawingCache();
			    }
			});
			saveDialog.setNegativeButton("İptal", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        dialog.cancel();
			    }
			});
			saveDialog.show();		
}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void paintClicked(View view){
		if(view!=currPaint){
			ImageButton imgView = (ImageButton)view;
			String color = view.getTag().toString();
			drawView.setColor(color);
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint=(ImageButton)view;
			}
		
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public byte[] convertBitmapToByteArray(Bitmap bmp) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}
	// fotografý galeriye kaydet
		public void onPictureTaken(byte[] data) {
			// TODO Auto-generated method stub
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				Log.d("---", "error creating media file, check storage permissions");
				return;

			}
			try {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();

			} catch (FileNotFoundException e) {
				Log.d("", "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d("", "Error accessing file: " + e.getMessage());
			}

		}
		// fotografýn kaydedilecegi dosyayý oluþtur
		private File getOutputMediaFile(int type) {
			// To be safe, you should check that the SDCard is mounted
			// using Environment.getExternalStorageState() before doing this.

			File mediaStorageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					"MyCameraApp");
			// This location works best if you want the created images to be shared
			// between applications and persist after your app has been uninstalled.

			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Log.d("MyCameraApp", "failed to create directory");
					return null;
				}
			}

			// Create a media file name
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			File mediaFile;
			String myPath;
			if (type == MEDIA_TYPE_IMAGE) {
				myPath = mediaStorageDir.getPath() + File.separator + "IMG_"
						+ timeStamp + ".jpg";
				mediaFile = new File(myPath);
			} else if (type == MEDIA_TYPE_VIDEO) {
				myPath = mediaStorageDir.getPath() + File.separator + "VID_"
						+ timeStamp + ".mp4";
				mediaFile = new File(myPath);
			} else {
				return null;
			}
			// photopath to database
			System.out.println("---------"+myPath);
			return mediaFile;
		}
}
