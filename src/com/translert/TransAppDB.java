package com.translert;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.content.Intent;
import com.translert.train.WatchActivity;

/*
 * Upper layer of TransDB for specific usage
 * The class will be used by at least two threads/tasks
 * So, semaphore is used and double lock in DB to avoid CRASH (ref. MUTEX) 
 * @
 */
public class TransAppDB {
	
	//ROW assignment
	public static final int ROW_FOR_TYPE                  = 1;
	public static final int ROW_FOR_RESERVED              = 2; //For BUS alarm
	public static final int ROW_FOR_ALARM_FLAG_AND_OFFSET = 3; //AFAO
	public static final int ROW_FOR_TRAIN                 = 4;
	public static final int ROW_FOR_TRAIN_INT             = 5; //offset up to SUPPORTED_INT_LINE_CNT
	
	public static final int ROW_FOR_TYPE_C1   = 1;
	public static final int ROW_FOR_TYPE_C2   = 2;
	public static final int ROW_FOR_AFAO_C1   = 1;
	public static final int ROW_FOR_AFAO_C2   = 2;
	public static final int ROW_FOR_TRAIN_C1  = 1;      //cur legTotal
	public static final int ROW_FOR_TRAIN_C2  = 2;      //cur legNum
	public static final int ROW_FOR_TRAIN_INT_C1  = 1;  //curDueTime
	public static final int ROW_FOR_TRAIN_INT_C2  = 2;  //curDestStation

	//Types of alert
	public static final String NO_ACTIVITY    = "NONE";
	public static final String TRAIN_ACTIVITY = "TRAIN";
	public static final String BUS_ACTIVITY   = "BUS";
	
	//interchange, LEG
	public static final int LEG_TOTAL = 0;
	public static final int LEG_NUM = 1;
	
	//interchange count supported
	private static final int SUPPORTED_INT_LINE_CNT = 5;
	
	private static final Semaphore available = new Semaphore(1, true);
	
	public TransAppDB(){
		
	}
	
	/* Wrapper for TransDB
	 * Designed only to manage multiple Activities (and/or Different contexts)
	 * and Retrieve Data from Non-Volatile Storage
	 * @Param1: Activity/Service, Not only dedicated on BusTrainActivity
	 * @retVal: true  - launch train/bus activity,
	 * 			false - As is
	 */
	public static synchronized boolean checkAndViewAlert(Context c){
		
		int Sz = 0;
		
		//Manager instance to link in DB
		TransDB transDb = new TransDB(c);

		ArrayList<Object> tempArr;
		tempArr = transDb.getRowAsArray(1);
		Sz = tempArr.size();
		if(Sz == 0){
			//ROW_FOR_TYPE
			// Column1 is used to specify the created alert, ignore Col2
			transDb.addRow(NO_ACTIVITY,"0");
			//ROW_FOR_RESERVED
			transDb.addRow("0","0");
			//ROW_FOR_AFAO
			transDb.addRow("0","0");
			//ROW_FOR_TRAIN
			//c1 as current Interchange count, c2 is reserved
			transDb.addRow("0","1");
			//ROW_FOR_TRAIN_INT
			//c1 as time, c2 as destination station
			for(int i = 0; i < SUPPORTED_INT_LINE_CNT; i++){				
				transDb.addRow("0","0");
			}
		}
		else{
			String s1 = (String)tempArr.get(ROW_FOR_TYPE_C1);
			if(s1 != null){
				if(s1.equals(TRAIN_ACTIVITY)){
					//resume single instance activity
					Intent watchIntent = new Intent(c, WatchActivity.class);					
					c.startActivity(watchIntent);
				}else if(s1.equals(BUS_ACTIVITY)){
					//resume single instance activity					
				}else{
					//May have a BUG
					transDb.db.close();
					return false;
				}
				transDb.db.close();
				return true;
			}
		}
		transDb.db.close();
		return false;
	}
	
	//
	private static void lock(){
		try {
			available.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//
	private static void unlock(){
		available.release();
	}
	
	/* Wrapper to save Activity Type at ROW1 of TransDB
	 * @req: checkAndViewAlert() was used before this method
	 * @Param1: Either Bus or Train context
	 * @Param2: Type Name
	 */
	public static synchronized void saveCreatedAlertType(Context c, String type){
		//Manager instance to link in DB
		lock();
		TransDB transDb = new TransDB(c);
		transDb.db.acquireReference();
		transDb.db.beginTransaction();
		try {
			transDb.updateRow(ROW_FOR_TYPE,type,"0"); //col2 is reserved
			transDb.db.setTransactionSuccessful();
		} finally {
			transDb.db.endTransaction();
		}
		transDb.db.releaseReference();
		transDb.db.close();
		unlock();
	}
	
	/* Wrapper to save Activity Type at ROW1
	 * @req: checkAndViewAlert() was used before this method, legTotal < SUPPORTED_INT_LINE_CNT
	 * @Param1: Either Bus or Train context
	 * @Param2: totalTime - offset set by user
	 * @
	 * @
	 */
	public static synchronized void saveAlarmDueTime(Context c, long offset, long[] dureTimeArr, String[] stationsArr, int legTotal){
		lock();
		//Manager instance to link in DB
		TransDB transDb = new TransDB(c);
		transDb.db.acquireReference();
		transDb.db.beginTransaction();
		try
		{
			//ROW_FOR_AFAO_C2 as offset
			transDb.updateRow(ROW_FOR_ALARM_FLAG_AND_OFFSET, "0", Long.toString(offset));
			//comes first in Wr/Rd
			transDb.updateRow(ROW_FOR_TRAIN, Integer.toString(legTotal), "1"); // "1" init legNum 
			for(int i = 0; i < legTotal; i++){
				transDb.updateRow(ROW_FOR_TRAIN_INT + i,
								  Long.toString(dureTimeArr[i]), //curDueTime
								  stationsArr[i]);				 //curStation
			}
			transDb.db.setTransactionSuccessful();
		} finally {
			transDb.db.endTransaction();
		}
		transDb.db.releaseReference();
		transDb.db.close();
		unlock();
	}
	
	/* Use in broadcasting, get internally legNum
	 * @req: checkAndViewAlert() was used before this method
	 * @param1: context
	 * @param2 as output: 1 element
	 * @ret: dueTime for the destStation
	 */
	 public static synchronized long getAlarmDueTime(Context c, String[] outPutArr){
		 lock();
		//we keep creating new instance for safer use 
		 int legNum = 0;
		TransDB transDb = new TransDB(c);
		transDb.db.acquireReference();
		
		ArrayList<Object> tempArr = null;
		int Sz = 0;
		
		tempArr = transDb.getRowAsArray(ROW_FOR_TRAIN); //
		Sz = tempArr.size();
		if(Sz != 0){
			legNum = Integer.parseInt((String)tempArr.get(ROW_FOR_TRAIN_C2)); // c2 is the cur legNum
			tempArr = transDb.getRowAsArray(ROW_FOR_TRAIN_INT + legNum-1); //
			Sz = tempArr.size();
		}			
		transDb.db.releaseReference();
		transDb.db.close();
		unlock();		
		if(Sz != 0){			
			outPutArr[0] = (String)tempArr.get(ROW_FOR_TRAIN_INT_C2);
			return Long.parseLong((String)tempArr.get(ROW_FOR_TRAIN_INT_C1));
		}
		return 0L; //shouldn't be zero, must be handled by api user
	}
	 
	/*
	 * 
	 */
	 public static void updateAlarmDueTime(Context c, long curDueTime){
		 lock();
		 ArrayList<Object> tempArr = null;
		 int legNum = 0;
		//Manager instance to link in DB
		TransDB transDb = new TransDB(c);
		transDb.db.acquireReference();
		transDb.db.beginTransaction();
		try
		{
			//comes first in Wr/Rd
			tempArr = transDb.getRowAsArray(ROW_FOR_TRAIN); //
			int Sz = tempArr.size();
			if(Sz != 0){				
				legNum = Integer.parseInt((String)tempArr.get(ROW_FOR_TRAIN_C2)); // c2 is the cur legNum
				transDb.updateRow(ROW_FOR_TRAIN_INT + legNum-1, Long.toString(curDueTime), null); // null - as is
			}
			transDb.db.setTransactionSuccessful();
		} finally {
			transDb.db.endTransaction();
		}
		transDb.db.releaseReference();
		transDb.db.close();
		unlock(); 		 
	 }
	
	 /*
	 * 
	 */
	 public static void updatecurLegNum(Context c, int curLegNum){
		lock();
		//Manager instance to link in DB
		TransDB transDb = new TransDB(c);
		transDb.db.acquireReference();
		transDb.db.beginTransaction();
		try
		{
			transDb.updateRow(ROW_FOR_TRAIN, null, Integer.toString(curLegNum)); // null - as is
			transDb.db.setTransactionSuccessful();
		} finally {
			transDb.db.endTransaction();
		}
		transDb.db.releaseReference();
		transDb.db.close();
		unlock(); 		 
	 }
	 
	 /* Use in broadcasting
	 * @req: checkAndViewAlert() was used before this method
	 * @ret:
	 */
	 public static synchronized int getLeg(Context c, int type){
		 lock();
		//we keep creating new instance for safer use 
		TransDB transDb = new TransDB(c);
		transDb.db.acquireReference();
		
		ArrayList<Object> tempArr = null;
		int Sz = 0;		
		tempArr = transDb.getRowAsArray(ROW_FOR_TRAIN); //
		Sz = tempArr.size();		
		transDb.db.releaseReference();
		transDb.db.close();
		unlock();
		
		if(Sz != 0){
			if(type == LEG_TOTAL){
				return Integer.parseInt((String)tempArr.get(ROW_FOR_TRAIN_C1));
			}else{
				return Integer.parseInt((String)tempArr.get(ROW_FOR_TRAIN_C2)); // current
			}
		}
		return 0; //Rare to be zero, must be handled by api user
	}
	 
	/* Wrapper for confirming active alert (bus/train)
	 * @req: checkAndViewAlert() was used before this method
	 */
	 public static synchronized boolean isNoActiveAlert(Context c){
		 lock();
		//we keep creating new instance for safer use 
		TransDB transDb = new TransDB(c);
		transDb.db.acquireReference();
		ArrayList<Object> tempArr;
		tempArr = transDb.getRowAsArray(ROW_FOR_TYPE);
		transDb.db.releaseReference();
		transDb.db.close();
		unlock();
		int Sz = tempArr.size();
		if(Sz != 0 && ((String)tempArr.get(ROW_FOR_TYPE_C1)).equals(NO_ACTIVITY)){
			return true;
		}		
		return false;
	}
	
	 /* ROW_FOR_AFAO_C1 for Alarm flag to avoid multiple alarm
	 *  @req: checkAndViewAlert() was used before this method
	 */
	 public static synchronized int getTrainAlarmFlag(Context c){
		 lock();
		//we keep creating new instance for safer use 
		TransDB transDb = new TransDB(c);
		transDb.db.acquireReference();
		ArrayList<Object> tempArr;
		tempArr = transDb.getRowAsArray(ROW_FOR_ALARM_FLAG_AND_OFFSET);
		transDb.db.releaseReference();
		transDb.db.close();
		unlock();
		int Sz = tempArr.size();
		if(Sz != 0){
			return Integer.parseInt((String)tempArr.get(ROW_FOR_AFAO_C1));
		}
		return 0;
	}
	 
	 /*
	 * @ 
	 */
	 public static void updateTrainAlarmFlag(Context c, int alarmFlag){
		lock();
		//Manager instance to link in DB
		TransDB transDb = new TransDB(c);
		transDb.db.acquireReference();
		transDb.db.beginTransaction();
		try
		{
			transDb.updateRow(ROW_FOR_ALARM_FLAG_AND_OFFSET, Integer.toString(alarmFlag), null); // null - as is
			transDb.db.setTransactionSuccessful();
		} finally {
			transDb.db.endTransaction();
		}
		transDb.db.releaseReference();
		transDb.db.close();
		unlock(); 		 
	 }
		 
	 /* ROW_FOR_AFAO_C2 for offset
	 *  @req: checkAndViewAlert() was used before this method
	 */
	 public static synchronized long getTrainAlarmOffset(Context c){
		 lock();
		//we keep creating new instance for safer use 
		TransDB transDb = new TransDB(c);
		transDb.db.acquireReference();
		ArrayList<Object> tempArr;
		tempArr = transDb.getRowAsArray(ROW_FOR_ALARM_FLAG_AND_OFFSET);
		transDb.db.releaseReference();
		transDb.db.close();
		unlock();
		int Sz = tempArr.size();
		if(Sz != 0){
			return Long.parseLong((String)tempArr.get(ROW_FOR_AFAO_C2));
		}
		return 0;
	}
}
