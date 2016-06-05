package com.sample;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @brief 所定時間経過ごとに定期的な処理を行う。
 */
public class PeriodicWorker implements Runnable {

	/**
	 * @brief 所定時間の最低値 100msec
	 */
	private static final long MIN_INTERVAL = 100;

	/**
	 * @brief 所定時間
	 */
	private long mInterval = MIN_INTERVAL;
	
	/**
	 * @brief IPeriodicWorkの参照
	 */
	private IPeriodicWork mWork = null;
	
	/**
	 * @brief 動作中か否かを示すフラグ
	 */
	private boolean mIsRunning = false;
	
	/**
	 * @brief ループするか否かを示すフラグ
	 */
	private boolean mIsLoop = false;
	
	/**
	 * @brief ロックオブジェクト
	 */
	private Lock mLock = new ReentrantLock();
	
	/**
	 * @brief スレッド
	 */
	private Thread mThread = null;
	
	/**
	 * @brief コンストラクタ
	 * @param[in] nInterval 所定時間 100msec以上を指定する
	 */
	public PeriodicWorker(long nInterval)
	{
		this(nInterval, null);
	}
	
	/**
	 * @brief コンストラクタ
	 * @param[in] nInterval 所定時間 100msec以上を指定する
	 * @param[in] nWork 定期的に行う処理
	 */
	public PeriodicWorker(long nInterval, IPeriodicWork nWork)
	{
		if (nInterval > MIN_INTERVAL)
		{
			mInterval = nInterval;
		}
		this.mWork = nWork;
	}
	
	/**
	 * @brief IPeriodicWorkを登録する
	 * @param nWork IPeriodicWorkの参照
	 * @return true：登録成功　false：登録失敗
	 */
	public boolean setPeriodicWork(IPeriodicWork nWork)
	{
		boolean lRet = false;
		
		mLock.lock();
		try {
			if ((false == mIsRunning) && (null != nWork))
			{
				lRet = true;
			}
		} finally {
			mLock.unlock();
		}
		
		if (lRet)
		{
			mWork = nWork;
		}
		
		return lRet;
	}

	/**
	 * @brief 定期的な処理を開始する
	 * @return true：開始成功　false：開始失敗
	 */
	public boolean start()
	{
		boolean lRet = false;
		
		mLock.lock();
		try {
			if ((false == mIsRunning) && (null != mWork) )
			{
				lRet = true;
				mIsRunning = true;
				mIsLoop = true;
			}
		} finally {
			mLock.unlock();
		}
		
		if (lRet)
		{
			mThread = new Thread(this);
			mThread.start();
		}
		
		return lRet;
	}

	/**
	 * @brief 定期的な処理を停止する
	 * @return true：停止成功　false：停止失敗
	 */
	public boolean stop()
	{
		boolean lRet = false;
		
		mLock.lock();
		try {
			if (mIsRunning)
			{
				lRet = true;
				mIsLoop = false;
			}
		} finally {
			mLock.unlock();
		}
		
		if (lRet)
		{
			try {
				mThread.join();
				mThread = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return lRet;
	}

	/**
	 * @brief 定期的な処理を呼び出す
	 */
	@Override
	public void run()
	{
		while(mIsLoop)
		{
			mWork.runPeriodic();
			try {
				Thread.sleep(mInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mIsRunning = false;
	}
	
}
