package com.sarxos.medusa.sim;

import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.RealTimeProvider;
import com.sarxos.medusa.trader.FuturesTrader;
import com.sarxos.medusa.util.Configuration;


public class FuturesSimulator {

	public static void main(String[] args) {

		Configuration CFG = Configuration.getInstance();

		CFG.setProperty("data", "history", "com.sarxos.medusa.plugin.bossa.BossaProvider");
		CFG.setProperty("messaging", "driver", "com.sarxos.medusa.sim.FakeMessagesDriver");

		Symbol symbol = Symbol.FW20M11;
		String name = symbol + "Trader";
		SignalGenerator<Quote> siggen = new MAVD(5, 10, 20);
		Paper paper = new Paper(symbol);
		RealTimeProvider provider = new FuturesSimulatorProvider(symbol, 1, 2);

		FuturesTrader trader = new FuturesTrader(name, siggen, paper, provider);

		trader.getObserver().setInterval(0);
		trader.trade();

		System.out.println(trader.getObserver());
	}
}