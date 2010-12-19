package com.sarxos.gpwnotifier.generator;

import java.util.LinkedList;
import java.util.List;

import com.sarxos.gpwnotifier.market.Quote;
import com.sarxos.gpwnotifier.market.Signal;
import com.sarxos.gpwnotifier.market.SignalGenerator;
import com.sarxos.gpwnotifier.market.SignalType;
import com.sarxos.gpwnotifier.math.MA;

import static com.sarxos.gpwnotifier.market.SignalType.BUY;
import static com.sarxos.gpwnotifier.market.SignalType.DELAY;
import static com.sarxos.gpwnotifier.market.SignalType.SELL;
import static com.sarxos.gpwnotifier.market.SignalType.WAIT;


/**
 * <b>MAVD</b> = <b>M</b>oving <b>A</b>verages <b>V</b>ariation & <b>D</b>erivative.
 * 
 * <p>
 * This system is <b>NOT</b> good for chaotic trends (e.g. TVN), but it is
 * very good for stable market (e.g. BRE, KGHM). 
 * </p>
 * 
 * <p>
 * D(n) = EMA(Q(n), A) - SMA(Q(n), B)<br>
 * G(n) = d(EMA(Q(n), C))<br><br>
 * 
 * B &lt;=&gt; D(n) &gt; 0 && G(n) &gt; 0<br>
 * D &lt;=&gt; D(n) &gt; 0 && G(n) &lt; 0<br>
 * S &lt;=&gt; D(n) &lt; 0
 * </p>
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class MAVD implements SignalGenerator<Quote> {

	
	private int A = 5;
	private int B = 15;
	private int C = 30;
	
	public MAVD(int A, int B, int C) {
		
		if (A < 2) {
			throw new IllegalArgumentException("EMA period canot be less then 2");
		}
		if (B < 2) {
			throw new IllegalArgumentException("SMA period canot be less then 2");
		}
		if (C < 2) {
			throw new IllegalArgumentException("EMAD period canot be less then 2");
		}
		
		this.A = A;
		this.B = B;
		this.C = C;
	}

	@Override
	public List<Signal> generate(Quote[] data, int R) {
		
		List<Signal> signals = new LinkedList<Signal>();

		Quote[] quotes = new Quote[R];
		
		System.arraycopy(data, data.length - R - 1, quotes, 0, R);
		
		SignalType signal = null;
		Quote q = null;
		
		double[] ema = MA.ema(quotes, A);
		double[] sma = MA.sma(quotes, B);
		double[] emad = MA.emad(quotes, C);
		
		double delta = 0;
		
		boolean delay = false;
		
		for (int i = 0; i < R; i++) {
			q = quotes[i];
			delta = ema[i] - sma[i];
			if (delta > 0 && !delay) {
				if (emad[i] > 0) { 
					if (signal != BUY) {
						signal = BUY;
						signals.add(new Signal(q.getDate(), signal, q, emad[i]));
						delay = false;
					}
				} else {
					delay = true;
				}
			} else if (delta < 0) {
				if (signal != SELL) {
					signal = SELL;
					signals.add(new Signal(q.getDate(), signal, q, emad[i]));
					delay = false;
				}
			}
			
			if (delay) {
				if (emad[i] > 0) {
					if (signal != BUY) {
						signal = BUY;
						signals.add(new Signal(q.getDate(), signal, q, emad[i]));
						delay = false;
					}
				}
			}
		}
		
		return signals;
	}

	@Override
	public Signal generate(Quote q) {
		
		double ema = MA.ema(q, A);
		double sma = MA.sma(q, B);
		double emad = MA.emad(q, C);
	
		Signal signal = new Signal(q, WAIT);
		
		if (ema - sma > 0) {
			if (emad > 0) {
				signal = new Signal(q, BUY);
			} else {
				signal = new Signal(q, DELAY);
			}
		} else {
			signal = new Signal(q, SELL);
		}
		
		return signal;
	}
}
