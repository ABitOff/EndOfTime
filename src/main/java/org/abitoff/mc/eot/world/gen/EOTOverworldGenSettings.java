package org.abitoff.mc.eot.world.gen;

import net.minecraft.world.gen.OverworldGenSettings;

public class EOTOverworldGenSettings extends OverworldGenSettings
{
	private int villageDistance;
	private int villageSeparation;
	private int oceanMonumentSpacing;
	private int oceanMonumentSeparation;
	private int strongholdDistance;
	private int strongholdCount;
	private int strongholdSpread;
	private int biomeFeatureDistance;
	private int biomeFeatureSeparation;
	private int shipwreckDistance;
	private int shipwreckSeparation;
	private int oceanRuinDistance;
	private int oceanRuinSeparation;
	private int endCityDistance;
	private int endCitySeparation;
	private int mansionDistance;
	private int mansionSeparation;
	private int bedrockRoofHeight;
	private int bedrockFloorHeight;
	private int biomeSize;
	private int riverSize;
	private int biomeId;

	public EOTOverworldGenSettings()
	{
	}

	public EOTOverworldGenSettings(OverworldGenSettings settings)
	{
		this.villageDistance = settings.getVillageDistance();
		this.villageSeparation = settings.getVillageSeparation();
		this.oceanMonumentSpacing = settings.getOceanMonumentSpacing();
		this.oceanMonumentSeparation = settings.getOceanMonumentSeparation();
		this.strongholdDistance = settings.getStrongholdDistance();
		this.strongholdCount = settings.getStrongholdCount();
		this.strongholdSpread = settings.getStrongholdSpread();
		this.biomeFeatureDistance = settings.getBiomeFeatureDistance();
		this.biomeFeatureSeparation = settings.getBiomeFeatureSeparation();
		this.shipwreckDistance = settings.getShipwreckDistance();
		this.shipwreckSeparation = settings.getShipwreckSeparation();
		this.oceanRuinDistance = settings.getOceanRuinDistance();
		this.oceanRuinSeparation = settings.getOceanRuinSeparation();
		this.endCityDistance = settings.getEndCityDistance();
		this.endCitySeparation = settings.getEndCitySeparation();
		this.mansionDistance = settings.getMansionDistance();
		this.mansionSeparation = settings.getMansionSeparation();
		this.bedrockRoofHeight = settings.getBedrockRoofHeight();
		this.bedrockFloorHeight = settings.getBedrockFloorHeight();
		this.biomeSize = settings.getBiomeSize();
		this.riverSize = settings.getRiverSize();
		this.biomeId = settings.getBiomeId();
	}

	public int getVillageDistance()
	{
		return villageDistance;
	}

	public void setVillageDistance(int villageDistance)
	{
		this.villageDistance = villageDistance;
	}

	public int getVillageSeparation()
	{
		return villageSeparation;
	}

	public void setVillageSeparation(int villageSeparation)
	{
		this.villageSeparation = villageSeparation;
	}

	public int getOceanMonumentSpacing()
	{
		return oceanMonumentSpacing;
	}

	public void setOceanMonumentSpacing(int oceanMonumentSpacing)
	{
		this.oceanMonumentSpacing = oceanMonumentSpacing;
	}

	public int getOceanMonumentSeparation()
	{
		return oceanMonumentSeparation;
	}

	public void setOceanMonumentSeparation(int oceanMonumentSeparation)
	{
		this.oceanMonumentSeparation = oceanMonumentSeparation;
	}

	public int getStrongholdDistance()
	{
		return strongholdDistance;
	}

	public void setStrongholdDistance(int strongholdDistance)
	{
		this.strongholdDistance = strongholdDistance;
	}

	public int getStrongholdCount()
	{
		return strongholdCount;
	}

	public void setStrongholdCount(int strongholdCount)
	{
		this.strongholdCount = strongholdCount;
	}

	public int getStrongholdSpread()
	{
		return strongholdSpread;
	}

	public void setStrongholdSpread(int strongholdSpread)
	{
		this.strongholdSpread = strongholdSpread;
	}

	public int getBiomeFeatureDistance()
	{
		return biomeFeatureDistance;
	}

	public void setBiomeFeatureDistance(int biomeFeatureDistance)
	{
		this.biomeFeatureDistance = biomeFeatureDistance;
	}

	public int getBiomeFeatureSeparation()
	{
		return biomeFeatureSeparation;
	}

	public void setBiomeFeatureSeparation(int biomeFeatureSeparation)
	{
		this.biomeFeatureSeparation = biomeFeatureSeparation;
	}

	public int getShipwreckDistance()
	{
		return shipwreckDistance;
	}

	public void setShipwreckDistance(int shipwreckDistance)
	{
		this.shipwreckDistance = shipwreckDistance;
	}

	public int getShipwreckSeparation()
	{
		return shipwreckSeparation;
	}

	public void setShipwreckSeparation(int shipwreckSeparation)
	{
		this.shipwreckSeparation = shipwreckSeparation;
	}

	public int getOceanRuinDistance()
	{
		return oceanRuinDistance;
	}

	public void setOceanRuinDistance(int oceanRuinDistance)
	{
		this.oceanRuinDistance = oceanRuinDistance;
	}

	public int getOceanRuinSeparation()
	{
		return oceanRuinSeparation;
	}

	public void setOceanRuinSeparation(int oceanRuinSeparation)
	{
		this.oceanRuinSeparation = oceanRuinSeparation;
	}

	public int getEndCityDistance()
	{
		return endCityDistance;
	}

	public void setEndCityDistance(int endCityDistance)
	{
		this.endCityDistance = endCityDistance;
	}

	public int getEndCitySeparation()
	{
		return endCitySeparation;
	}

	public void setEndCitySeparation(int endCitySeparation)
	{
		this.endCitySeparation = endCitySeparation;
	}

	public int getMansionDistance()
	{
		return mansionDistance;
	}

	public void setMansionDistance(int mansionDistance)
	{
		this.mansionDistance = mansionDistance;
	}

	public int getMansionSeparation()
	{
		return mansionSeparation;
	}

	public void setMansionSeparation(int mansionSeparation)
	{
		this.mansionSeparation = mansionSeparation;
	}

	public int getBedrockRoofHeight()
	{
		return bedrockRoofHeight;
	}

	public void setBedrockRoofHeight(int bedrockRoofHeight)
	{
		this.bedrockRoofHeight = bedrockRoofHeight;
	}

	public int getBedrockFloorHeight()
	{
		return bedrockFloorHeight;
	}

	public void setBedrockFloorHeight(int bedrockFloorHeight)
	{
		this.bedrockFloorHeight = bedrockFloorHeight;
	}

	public int getBiomeSize()
	{
		return biomeSize;
	}

	public void setBiomeSize(int biomeSize)
	{
		this.biomeSize = biomeSize;
	}

	public int getRiverSize()
	{
		return riverSize;
	}

	public void setRiverSize(int riverSize)
	{
		this.riverSize = riverSize;
	}

	public int getBiomeId()
	{
		return biomeId;
	}

	public void setBiomeId(int biomeId)
	{
		this.biomeId = biomeId;
	}
}
