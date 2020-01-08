package org.abitoff.mc.eot;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

import org.abitoff.mc.eot.world.WorldTypeEOT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("eot")
public class EndOfTime
{
	private static final Logger LOGGER = LogManager.getLogger(EndOfTime.class);
	private static final Map<String, Object> buildData = getBuildData();
	private static final WorldType worldType = WorldTypeEOT.get();

	public EndOfTime()
	{
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		LOGGER.info("Loading " + buildData.getOrDefault("mod-name", "End of Time") + " version "
				+ buildData.getOrDefault("mod-version", "?"));
	}

	private static Map<String, Object> getBuildData()
	{
		Type mapType = new TypeToken<Map<String, Object>>()
		{
		}.getType();
		try (Reader r = new InputStreamReader(EndOfTime.class.getResourceAsStream("/build-data.json")))
		{
			return new Gson().fromJson(r, mapType);
		} catch (Exception e)
		{
			LOGGER.error("Failed to get End of Time build data!", e);
			throw new RuntimeException(e);
		}
	}
}
