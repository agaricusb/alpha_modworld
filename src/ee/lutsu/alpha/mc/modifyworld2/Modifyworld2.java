package ee.lutsu.alpha.mc.modifyworld2;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ee.lutsu.alpha.mc.modifyworld2.handlers.BlockListener;
import ee.lutsu.alpha.mc.modifyworld2.handlers.EntityListener;
import ee.lutsu.alpha.mc.modifyworld2.handlers.PlayerListener;
import ee.lutsu.alpha.mc.modifyworld2.handlers.VehicleListener;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

@Mod(
		modid="Modifyworld2", 
		name="Modifyworld2", 
		version="1.4.7.1"
	)
@NetworkMod(clientSideRequired=false, serverSideRequired=true)
public class Modifyworld2 
{
	@Mod.Instance("Modifyworld2")
	public static Modifyworld2 instance;
	
	public static String MOD_NAME = "Modifyworld2";
	protected BaseListener[] listeners = new BaseListener[]
	{
		new PlayerListener(),
		new EntityListener(),
		new BlockListener(),
		new VehicleListener()
	};
	public PlayerInformer informer;
	public File configFile;
	protected Configuration config;

	@Mod.PreInit
	public void onLoad(FMLPreInitializationEvent ev)
	{
		configFile = ev.getSuggestedConfigurationFile();
		config = new Configuration(configFile);
		informer = new PlayerInformer(config);
	}
	
	@Mod.ServerStarted
	public void modsLoaded(FMLServerStartedEvent var1)
	{
		for (BaseListener l : listeners)
			l.load();
		
		onEnable();
	}
	
	public void reload()
	{
		onDisable();
		onEnable();
	}

	public void onEnable() 
	{
		reloadConfig();

		for (BaseListener l : listeners)
			l.enabled = true;
		
		Log.info("§2enabled");

		this.saveConfig();
	}

	public void onDisable()
	{
		for (BaseListener l : listeners)
			l.enabled = false;

		Log.info("§2 disabled");
	}

	public Configuration getConfig()
	{
		if (this.config == null) {
			this.reloadConfig();
		}

		return this.config;
	}

	public void saveConfig() 
	{
		this.config.save();
	}

	public void reloadConfig() 
	{
		this.config.load();
	}
}
