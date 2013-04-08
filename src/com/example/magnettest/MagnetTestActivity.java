package com.example.magnettest;

import java.util.ArrayList;
import java.util.List;

import com.samsung.magnet.IMagnetChannel;
import com.samsung.magnet.IMagnetChannelListener;
import com.samsung.magnet.IMagnetManagerListener;
import com.samsung.magnet.MagnetManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MagnetTestActivity extends Activity {
	// #1. get instance
	MagnetManager mMagnet = MagnetManager.getInstance(this);
	IMagnetChannel channelInst;
	ArrayAdapter<String> adapter2;
	EditText searchEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magnet_test);

		String mMyTempDirectory = "/data/anr/";
		// #2. set some values before start
		mMagnet.setTempDirectory(mMyTempDirectory);
		mMagnet.setHandleEventLooper(getMainLooper());
		// #3. select network interface
		List<Integer> interfaces = mMagnet.getAvailableInterfaceTypes();
		final Integer interfaceType = interfaces.get(0);
		// #4. Start magnet
		mMagnet.start(interfaceType, new IMagnetManagerListener() {

			@Override
			public void onError(int arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Error",
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNetworkDisconnected() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"onNetworkDisconnected", Toast.LENGTH_LONG).show();

			}

			@Override
			public void onStarted(String arg0, int arg1) {
				// TODO Auto-generated method stub
				final int a = interfaceType;
				Toast.makeText(getApplicationContext(), "onStarted" + a + " ",
						Toast.LENGTH_LONG).show();

			}

		});

		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				channelInst = mMagnet.joinChannel(
						"com.samsung.test.TEST_CHANNEL",
						new IMagnetChannelListener() {

							@Override
							public void onNodeLeft(String arg0, String arg1) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onNodeJoined(String arg0, String arg1) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onFileWillReceive(String arg0,
									String arg1, String arg2, String arg3,
									String arg4, String arg5, long arg6) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onFileSent(String arg0, String arg1,
									String arg2, String arg3, String arg4,
									String arg5) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onFileReceived(String arg0,
									String arg1, String arg2, String arg3,
									String arg4, String arg5, long arg6,
									String arg7) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onFileFailed(String arg0, String arg1,
									String arg2, String arg3, String arg4,
									int arg5) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onFileChunkSent(String arg0,
									String arg1, String arg2, String arg3,
									String arg4, String arg5, long arg6,
									long arg7, long arg8) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onFileChunkReceived(String arg0,
									String arg1, String arg2, String arg3,
									String arg4, String arg5, long arg6,
									long arg7) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onDataReceived(String arg0,
									String arg1, String arg2, byte[][] data) {
								if(data!=null&&data.length==2)
								{
									String name=new String(data[0]);
									String string = new String(data[1]);
									adapter2.add(name+":"+string);
								}

								// TODO Auto-generated method stub

							}
						});

			}
		});

		Button sendButton = (Button) findViewById(R.id.button2);
		sendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (channelInst != null) {

					List<String> nodeList = channelInst.getJoinedNodeList();
					if (nodeList != null) {

						// #1. SEND QUIZ
						EditText editText = (EditText) findViewById(R.id.editText1);
						byte[][] payload = new byte[2][1];
						payload[0] = "User".getBytes(); // Sender’s friendly
														// name
						payload[1] = editText.getText().toString().getBytes(); // Quiz
						channelInst.sendDataToAll(
								"com.samsung.test.TEST_CHANNEL", payload);
						editText.setText("");
						
						//setEditTextFocus(false);

					} else {
						Toast.makeText(getApplicationContext(),
								"nodeList==null  ", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"channelInst==null  ", Toast.LENGTH_LONG).show();
				}

				// TODO Auto-generated method stub

			}
		});

		ListView listView = (ListView) findViewById(R.id.listview);
		ArrayList<String> list = new ArrayList<String>();

		adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		listView.setAdapter(adapter2);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listView.setStackFromBottom(true);
		/*
		 searchEditText=(EditText)findViewById(R.id.editText1);
		 searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
	        {
	            @Override
	            public void onFocusChange(View v, boolean hasFocus)
	            {
	                if (v == searchEditText)
	                {
	                    if (hasFocus)
	                    {
	                        //open keyboard
	                        ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchEditText,
	                                InputMethodManager.SHOW_FORCED);

	                    }
	                    else
	                    { //close keyboard
	                        ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
	                                searchEditText.getWindowToken(), 0);
	                    }
	                }
	            }
	        });*/
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.magnet_test, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		// #2. leave channel
		mMagnet.leaveChannel("com.samsung.test.TEST_CHANNEL");
		// #6. Stop magnet
		mMagnet.stop();
		super.onDestroy();
	}

	public void setEditTextFocus(boolean isFocused)
	{
		EditText searchEditText=(EditText)findViewById(R.id.editText1);
	    searchEditText.setCursorVisible(isFocused);
	    searchEditText.setFocusable(isFocused);
	    searchEditText.setFocusableInTouchMode(isFocused);

	    if (isFocused)
	    {
	        searchEditText.requestFocus();
	    }
	}
}
