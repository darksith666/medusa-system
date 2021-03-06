package com.sarxos.medusa.data;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


/**
 * Class used to simulate original quotes registry.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class FakeQuotesRegistry extends QuotesRegistry {

	/**
	 * Quotes map.
	 */
	private Map<Symbol, List<Quote>> quotes = new HashMap<Symbol, List<Quote>>();

	/**
	 * Registry instance.
	 */
	private static FakeQuotesRegistry instance = new FakeQuotesRegistry();

	/**
	 * Construct
	 */
	public FakeQuotesRegistry() {
		// setInstance(this);
	}

	/**
	 * @return Return registry instance
	 */
	public static FakeQuotesRegistry getInstance() {
		return instance;
	}

	@Override
	public List<Quote> getQuotes(Symbol symbol) {
		return quotes.get(symbol);
	}

	/**
	 * Add quote to the quotes register.
	 * 
	 * @param symbol - symbol to attach quote to
	 * @param q - quote to add
	 */
	public void addQuote(Symbol symbol, Quote q) {

		List<Quote> qs = quotes.get(symbol);
		if (qs == null) {
			qs = new LinkedList<Quote>();
			quotes.put(symbol, qs);
		}

		int n = qs.size();
		if (n > 0) {
			Quote p = qs.get(n - 1);

			long m = 1000 * 60 * 60 * 24;
			long pt = p.getDate().getTime();
			long qt = q.getDate().getTime();

			if (qt / m <= (pt - m) / m) {
				throw new RuntimeException(
					"Quotes registry can store only end-of-date quotes! " +
					"You are trying to add new quote for the previous date (" +
					p.getDate() + " vs " + q.getDate());
			}

			p.setNext(q);
			q.setPrev(p);
		}

		qs.add(q);
	}

	/**
	 * Load quotes from the real quotes registry. Only quotes before given date
	 * will be loaded.
	 * 
	 * @param symbol - symbol to load
	 * @param date - load quotes before this date
	 */
	public void load(Symbol symbol, Date date) {

		QuotesRegistry qr = QuotesRegistry.getInstance();
		List<Quote> quotes = qr.getQuotes(symbol);
		Quote head = quotes.get(0);

		long m = 1000 * 60 * 60 * 24;
		long ht = head.getDate().getTime();
		long dt = date.getTime();
		
		while (ht / m < (dt - m) / m) {
			addQuote(symbol, head);
			if ((head = head.next()) != null) {
				ht = head.getDate().getTime();
			} else {
				break;
			}
		}
	}
}
