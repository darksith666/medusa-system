package com.sarxos.gpwnotifier.observer;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.sarxos.gpwnotifier.data.DataProviderException;
import com.sarxos.gpwnotifier.data.RealTimeDataProvider;
import com.sarxos.gpwnotifier.entities.Symbol;

/**
 * Stock symbol observer. Default price check interval is 30s.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Observer implements Runnable {

	/**
	 * Possible observer states. Possible transitions are:
	 * <ul>
	 * <li>STOPPED =&gt; RUNNING</li>
	 * <li>RUNNING =&gt; PAUSED</li>
	 * <li>RUNNING =&gt; STOPPED</li>
	 * <li>PAUSED =&gt; STOPPED</li>
	 * <li>PAUSED =&gt; RUNNING</li>
	 * </ul>
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	public static enum State {
		RUNNIG,
		PAUSED,
		STOPPED;
	}

	/**
	 * Thread group for all observer runners. 
	 */
	private static ThreadGroup group = new ThreadGroup("Observers");
	
	/**
	 * Stock data provider.
	 */
	private RealTimeDataProvider provider = null;
	
	/**
	 * Runner for each observer;
	 */
	private Thread runner = null;

	/**
	 * Runner state.
	 */
	private State state = State.STOPPED;

	/**
	 * Observed symbol.
	 */
	private Symbol symbol = null;
	
	/**
	 * Default check interval (30s).
	 */
	private long interval = 30000;

	/**
	 * Last observed symbol price.
	 */
	private double price = -1.;

	private List<PriceListener> listeners = new LinkedList<PriceListener>();

	/**
	 * Create new observer. You have to specify observed symbol via the 
	 * {@link Observer#observe(Symbol)} method.
	 *  
	 * @param provider - real time data provider
	 */
	public Observer(RealTimeDataProvider provider) {
		this.provider = provider;
	}
	
	/**
	 * Create new data observer.
	 *  
	 * @param provider - real time data provider
	 * @param symbol - observed symbol
	 */
	public Observer(RealTimeDataProvider provider, Symbol symbol) {
		this.provider = provider;
		this.observe(symbol);
	}
	
	/**
	 * Observe symbol.
	 * 
	 * @param symbol - stock symbol
	 */
	public void observe(Symbol symbol) {
		if (provider.canServe(symbol)) {
			this.symbol = symbol;
		} else {
			throw new IllegalArgumentException(
					"Data provider " + provider.getClass().getName() + " " +
					"cannot serve " + symbol + " data"
			);
		}
	}
	
	/**
	 * @return Return check interval in seconds
	 */
	public long getInterval() {
		return interval / 1000;
	}

	/**
	 * Set check interval in seconds. 
	 * 
	 * @param interval
	 */
	public void setInterval(int interval) {
		if (interval < 1) {
			throw new IllegalArgumentException("Check interval in seconds must be positive");
		}
		this.interval = interval * 1000;
	}

	/**
	 * Stop observation. After calling this method observation will be
	 * stopped, but observer can be run once again in any moment.
	 */
	public void stop() {
		if (state != State.RUNNIG && state != State.PAUSED) {
			throw new IllegalStateException("Cannot stop not running or paused observer");
		}
		this.state = State.STOPPED;
	}
	
	/**
	 * Pause observation.
	 */
	public void pause() {
		if (state != State.RUNNIG) {
			throw new IllegalStateException("Cannot pause not running observer");
		}
		this.state = State.PAUSED;
		try {
			runner.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resume observation.
	 */
	public void resume() {
		if (state != State.PAUSED) {
			throw new IllegalStateException("Cannot resume not paused observer");
		}
		this.state = State.RUNNIG;
	}
	
	/**
	 * Start observation.
	 */
	public void start() {
		if (symbol == null) {
			throw new IllegalStateException("Cannot start observer when symbol is not set");
		}
		if (runner == null) {
			runner = createRunner();
		} else {
			throw new IllegalStateException("Observer is already started - cannot start it again");
		}
		state = State.RUNNIG;
		runner.start();
		try {
			runner.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Runner thread for this observer.
	 */
	protected Thread createRunner() {
		Thread thread = new Thread(group, this, symbol.toString());
		thread.setDaemon(true);
		return thread;
	}
	
	/**
	 * @return Return real time data provider.
	 */
	public RealTimeDataProvider getProvider() {
		return provider;
	}

	/**
	 * Set new real time data provider.
	 * 
	 * @param provider - data provider to set
	 */
	public void setProvider(RealTimeDataProvider provider) {
		this.provider = provider;
	}

	@Override
	public void run() {
		do {
			if (state == State.STOPPED) {
				break;
			}
			try {
				runOnce();
			} catch (DataProviderException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(true);
	}
	
	protected void runOnce() throws DataProviderException {
		double tmp = provider.getValue(symbol);
		if (tmp != price && price != -1) {
			notifyListeners(new PriceEvent(this, price, tmp));
		}
		price = tmp;
		System.out.println(interval + " " + tmp);
	}
	
	/**
	 * @return Thread group for observation runners.
	 */
	public static ThreadGroup getRunnersGroup() {
		return group;
	}

	/**
	 * @return Return last observed price or -1 if no price has been observed yet.
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * Notify all listeners about price change.
	 * 
	 * @param pe - price event
	 */
	protected void notifyListeners(PriceEvent pe) {
		
		PriceListener listener = null;
		ListIterator<PriceListener> i = listeners.listIterator();
		
		while (i.hasNext()) {
			listener = i.next();
			try {
				listener.priceChange(pe);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return Price listeners array.
	 */
	public PriceListener[] getPriceListeners() {
		return listeners.toArray(new PriceListener[listeners.size()]);
	}
	
	/**
	 * 
	 * @param listener
	 * @return true if listener was added or false if it is already on the list 
	 */
	public boolean addPriceListener(PriceListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
		return false;
	}
	
	/**
	 * Remove particular price listener.
	 * 
	 * @param listener - price listener to remove
	 * @return true if listener list contained specified element
	 */
	public boolean removePriceListener(PriceListener listener) {
		return listeners.remove(listener);
	}
	
//	public static void main(String[] args) {
//		Observer o = new Observer(new BizzoneDataProvider());
//		o.observe(Symbol.WIG20);
//		o.setInterval(5);
//		o.addPriceListener(new PriceListener() {
//			@Override
//			public void priceChange(PriceEvent event) {
//				System.out.println(event.getPrevious() - event.getCurrent());
//			}
//		});
//		o.start();
//	}
	
}
