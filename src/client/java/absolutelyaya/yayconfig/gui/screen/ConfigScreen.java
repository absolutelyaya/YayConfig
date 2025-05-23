package absolutelyaya.yayconfig.gui.screen;

import absolutelyaya.yayconfig.YayConfig;
import absolutelyaya.yayconfig.accessor.WidgetAccessor;
import absolutelyaya.yayconfig.config.*;
import absolutelyaya.yayconfig.gui.widget.ConfigWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen
{
	static final Identifier SIMPLE_BG_TEX = YayConfig.id("textures/gui/simplistic_bg.png");
	public static ConfigScreen INSTANCE;
	List<ConfigWidget<?, ?, ?>> ruleWidgets = new ArrayList<>();
	float curScroll, desiredScroll;
	CheckboxWidget simplistic;
	Vector2i nextButtonPos;
	AbstractConfig config;
	Screen parent;
	
	public ConfigScreen(AbstractConfig config, Screen parent)
	{
		super(Text.translatable("screen.yayconfig.config-screen.title"));
		INSTANCE = this;
		this.config = config;
		this.parent = parent;
	}
	
	public ConfigScreen(AbstractConfig config)
	{
		this(config, null);
	}
	
	@Override
	protected void init()
	{
		super.init();
		ruleWidgets.forEach(this::remove);
		ruleWidgets.clear();
		nextButtonPos = new Vector2i(width / 2 - 100, 40);
		config.entries.values().forEach(this::addRule);
		boolean b = false;
		if(simplistic != null)
			b = simplistic.isChecked();
		simplistic = addDrawableChild(CheckboxWidget.builder(
				Text.translatable("screen.yayconfig.simplistic"), textRenderer).pos(width / 2 + 130, height - 30).maxWidth(60).checked(b).build());
	}
	
	<K extends ConfigEntry<?>> void addRule(K key)
	{
		if(key instanceof Comment)
			return;
		ConfigWidget<?, ?, ?> w;
		if(key instanceof EnumEntry<?> entry)
			w = ConfigWidget.create(nextButtonPos, (ConfigEntry<Enum<?>>)key, entry.getValidOptions(), config.getId());
		else
			w = ConfigWidget.createGeneric(nextButtonPos, key, config.getId());
		if(config instanceof ClientConfig)
			w.setClient();
		ruleWidgets.add(addDrawableChild(w));
		nextButtonPos.add(0, 38);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		renderBackground(context, mouseX, mouseY, delta);
		curScroll = MathHelper.lerp(delta / 2f, curScroll, desiredScroll);
		ruleWidgets.forEach(w -> {
			((WidgetAccessor)w).setOffset(new Vector2i(0, Math.round(curScroll)));
			w.render(context, mouseX, mouseY, delta, simplistic.isChecked());
			context.draw();
		});
		simplistic.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(textRenderer, config.getTitle(), width / 2, 20, 0xffffffff);
	}
	
	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.renderBackground(context, mouseX, mouseY, delta);
		RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);
		context.drawTexture(RenderLayer::getGuiTextured, simplistic.isChecked() ? SIMPLE_BG_TEX : getBackgroundTexture(),
				width /2 - 125, 0, 0, 0, 250, height, 32, 32);
		context.fill(width / 2 - 125, -1, width / 2 - 124, height + 1, 0x88ffffff);
		context.fill(width / 2 + 125, -1, width / 2 + 124, height + 1, 0x88000000);
		context.draw();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		Element clicked = null;
		for(Element i : children())
			if(i.mouseClicked(mouseX, mouseY, button))
				clicked = i;
		if(clicked == null)
			return false;
		setFocused(clicked);
		if (button == 0)
			setDragging(true);
		return true;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
	{
		desiredScroll = MathHelper.clamp(desiredScroll + (float)verticalAmount * 15f, -36 * (ruleWidgets.size() - 3), 0);
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}
	
	public void onExternalRuleUpdate(String id)
	{
		for (ConfigWidget<?, ?, ?> widget : ruleWidgets)
			if(widget.getRuleId().equals(id))
				widget.onExternalUpdate();
	}
	
	@Override
	public boolean shouldPause()
	{
		return false;
	}
	
	@Override
	public void close()
	{
		if(config instanceof ClientConfig cconfig)
			cconfig.save(client);
		if(parent != null && client != null)
			client.setScreen(parent);
		else
			super.close();
		INSTANCE = null;
	}
	
	public Identifier getBackgroundTexture()
	{
		return config.getBackgroundTexture();
	}
}
