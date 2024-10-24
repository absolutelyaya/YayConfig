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
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen
{
	static final Identifier SIMPLE_BG_TEX = YayConfig.indentifier("textures/gui/simplistic_bg.png");
	public static ConfigScreen INSTANCE;
	List<ConfigWidget<?>> ruleWidgets = new ArrayList<>();
	float curScroll, desiredScroll;
	CheckboxWidget simplistic;
	Vector2i nextButtonPos;
	Config config;
	
	public ConfigScreen(Config config)
	{
		super(Text.translatable("screen.yayconfig.config-screen.title"));
		INSTANCE = this;
		this.config = config;
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
				Text.translatable("screen.yayconfig.simplistic"), textRenderer)
											  .pos(width / 2 + 130, height - 30).maxWidth(60).checked(b).build());
	}
	
	<K extends ConfigEntry<?>> void addRule(K key)
	{
		if(key instanceof Comment)
			return;
		if(key instanceof EnumEntry<?> entry)
			ruleWidgets.add(addDrawableChild(new ConfigWidget<>(entry.getValue().toString(), nextButtonPos, key,
					entry.getValidOptions(), entry.getIcon(), config.getId())));
		else
			ruleWidgets.add(addDrawableChild(new ConfigWidget<>(key.getValue().toString(), nextButtonPos, key,
					key.getType(), key.getIcon(), config.getId())));
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
		});
		simplistic.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(textRenderer, config.getTitle(), width / 2, 20, 0xffffffff);
	}
	
	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.renderBackground(context, mouseX, mouseY, delta);
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);
		context.drawTexture(simplistic.isChecked() ? SIMPLE_BG_TEX : getBackgroundTexture(),
				width /2 - 125, 0, 0, 0.0f, 0.0f, 250, height, 32, 32);
		context.fill(width / 2 - 125, -1, width / 2 - 124, height + 1, 0xaaffffff);
		context.fill(width / 2 + 125, -1, width / 2 + 124, height + 1, 0xaa000000);
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
	
	public <T extends ConfigEntry<?>> void onExternalRuleUpdate(T rule, String value)
	{
		for (ConfigWidget<?> widget : ruleWidgets)
			if(widget.isRule(rule))
				widget.stateUpdate();
	}
	
	@Override
	public boolean shouldPause()
	{
		return false;
	}
	
	@Override
	public void close()
	{
		super.close();
		INSTANCE = null;
	}
	
	public Identifier getBackgroundTexture()
	{
		return config.getBackgroundTexture();
	}
}
