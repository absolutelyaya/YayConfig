package absolutelyaya.yayconfig.test;

import absolutelyaya.yayconfig.YayConfig;
import absolutelyaya.yayconfig.config.*;

public class TestClientConfig extends ClientConfig
{
	public static TestClientConfig INSTANCE;
	
	public final EnumEntry<TestSetting> hivel = new EnumEntry<>("HiVelMode", TestSetting.FREE);
	public final EnumEntry<TestSetting> timestop = new EnumEntry<>("TimeStop", TestSetting.FORCE_OFF)
														   .setValidOptions(new TestSetting[] {TestSetting.FORCE_ON, TestSetting.FORCE_OFF});
	public final BooleanEntry disableHandswap = new BooleanEntry("DisableHandswap", false);
	public final BooleanEntry effectivelyViolent = new BooleanEntry("EffectivelyViolent", false);
	public final BooleanEntry explosionBlockBreaking = new BooleanEntry("Explosion-BlockBreaking", true);
	public final BooleanEntry tntPriming = new BooleanEntry("Explosion-TntPriming", true);
	public final BooleanEntry smSafeLedges = new BooleanEntry("Swordsmachine-SafeLedges", false);
	public final BooleanEntry parryChaining = new BooleanEntry("ParryChaining", false);
	public final BooleanEntry terminalProtection = new BooleanEntry("TerminalProtection", true);
	public final BooleanEntry flamethrowerGrief = new BooleanEntry("FlamethrowerGrief", false);
	public final IntegerEntry hellObserverInterval = new IntegerEntry("HellObserverInterval", 5);
	public final BooleanEntry bloodSaturation = new BooleanEntry("BloodSaturation", false);
	public final BooleanEntry dodgeableOverpump = new BooleanEntry("DodgeableOverpump", false);
	public final BooleanEntry customLevelsUnlocked = new BooleanEntry("UnlockAllCustomLevels", false);
	public final FloatEntry parryRange = (FloatEntry)new FloatEntry("ParryRange", 3f).setRange(0f, Float.MAX_VALUE);
	public final FloatEntry coinPunchRange = (FloatEntry)new FloatEntry("CoinPunchRange", 4f).setRange(0f, Float.MAX_VALUE);
	public final BooleanEntry disableModificationSuppression = new BooleanEntry("DisableModificationSuppression", false);
	public final BooleanEntry protectNature = new BooleanEntry("ProtectNature", false);
	//Weapon Damage
	public final FloatEntry feedbackerDamage = (FloatEntry)new FloatEntry("FeedbackerDamage", 1f).setRange(0f, Float.MAX_VALUE);
	public final FloatEntry knuckleblasterDamage = (FloatEntry)new FloatEntry("KnuckleblasterDamage", 2.5f).setRange(0f, Float.MAX_VALUE);
	public final FloatEntry revolverDamage = (FloatEntry)new FloatEntry("RevolverDamage", 1f).setRange(0f, Float.MAX_VALUE);
	public final FloatEntry shotgunDamage = (FloatEntry)new FloatEntry("ShotgunDamage", 1f).setRange(0f, Float.MAX_VALUE);
	public final FloatEntry nailgunDamage = (FloatEntry)new FloatEntry("NailgunDamage", 1f).setRange(0f, Float.MAX_VALUE);
	//Debug
	public final BooleanEntry disableFixedStructures = new BooleanEntry("DisableFixedStructures", false);
	public final IntegerEntry version = new IntegerEntry("ConfigVersion", 0);
	
	public TestClientConfig()
	{
		super(YayConfig.indentifier("client-test"));
		//I literally just kept most config values from ultracraft for testing lol
		addEntry(new Comment(" ## ############################# ##  #"));
		addEntry(new Comment("     Welcome to Config Zone"));
		addEntry(new Comment(" ## ############################# ##  #"));
		addEntry(hivel);
		addEntry(timestop);
		addEntry(disableHandswap);
		addEntry(effectivelyViolent);
		addEntry(explosionBlockBreaking);
		addEntry(tntPriming);
		addEntry(smSafeLedges);
		addEntry(parryChaining);
		addEntry(terminalProtection);
		addEntry(flamethrowerGrief);
		addEntry(hellObserverInterval);
		addEntry(bloodSaturation);
		addEntry(dodgeableOverpump);
		addEntry(customLevelsUnlocked);
		addEntry(parryRange);
		addEntry(coinPunchRange);
		addEntry(disableModificationSuppression);
		addEntry(protectNature);
		addEntry(new Comment(" ## ############################# ##  #"));
		addEntry(new Comment("      Weapon Damage Multipliers"));
		addEntry(new Comment(" ## ############################# ##  #"));
		addEntry(feedbackerDamage);
		addEntry(knuckleblasterDamage);
		addEntry(revolverDamage);
		addEntry(shotgunDamage);
		addEntry(nailgunDamage);
		addEntry(new Comment(" ## ############################# ##  #"));
		addEntry(new Comment("           Debug stuff"));
		addEntry(new Comment(" ## ############################# ##  #"));
		addEntry(disableFixedStructures);
		addEntry(version);
		
		INSTANCE = this;
	}
	
	@Override
	protected String getFileName()
	{
		return "client-test.properties";
	}
}
