package me.lucko.luckperms.fabric;

import me.lucko.luckperms.common.plugin.logging.PluginLogger;
import org.apache.logging.log4j.Logger;

public class FabricPluginLogger implements PluginLogger {
	private final Logger logger;

	public FabricPluginLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void info(String s) {
		logger.info(s);
	}

	@Override
	public void warn(String s) {
		logger.warn(s);
	}

	@Override
	public void severe(String s) {
		logger.error(s);
	}
}
