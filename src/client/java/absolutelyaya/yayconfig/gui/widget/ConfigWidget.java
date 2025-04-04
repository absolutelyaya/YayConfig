package absolutelyaya.yayconfig.gui.widget;

import absolutelyaya.yayconfig.YayConfig;
import absolutelyaya.yayconfig.accessor.WidgetAccessor;
import absolutelyaya.yayconfig.config.ConfigEntry;
import absolutelyaya.yayconfig.config.Constants;
import absolutelyaya.yayconfig.networking.SyncConfigC2SPayload;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2i;

import java.text.DecimalFormat;
import java.util.List;

public abstract class ConfigWidget<T extends ConfigEntry<V>, V, W extends Drawable> extends ClickableWidget implements Element, Drawable, Selectable, Constants
{
	static final Identifier SIMPLE_BG_TEXTURE = YayConfig.id("textures/gui/simplistic_bg.png");
	static final Identifier BG_TEXTURE = Identifier.of("textures/block/stone.png");
	static final Identifier PLACEHOLDER_ICON_TEXTURE = YayConfig.id("textures/gui/placeholder_icon.png");
	
	final Identifier parentId;
	final T rule;
	final Identifier icon;
	TextRenderer renderer;
	protected W controlWidget;
	boolean client;
	
	public ConfigWidget(Vector2i pos, T rule, Identifier parentId)
	{
		super(pos.x, pos.y, 200, 36, Text.empty());
		this.parentId = parentId;
		this.rule = rule;
		renderer = MinecraftClient.getInstance().textRenderer;
		if(rule.getIcon() != null)
			this.icon = rule.getIcon();
		else
			this.icon = PLACEHOLDER_ICON_TEXTURE;
	}
	
	@SuppressWarnings("unchecked")
	public static ConfigWidget<?, ?, ?> createGeneric(Vector2i pos, ConfigEntry<?> rule, Identifier parentId)
	{
		return switch(rule.getType())
		{
			case BOOLEAN_TYPE -> ConfigWidget.createB(pos, (ConfigEntry<Boolean>)rule, parentId);
			case INT_TYPE -> createI(pos, (ConfigEntry<Integer>)rule, parentId);
			case FLOAT_TYPE -> createF(pos, (ConfigEntry<Float>)rule, parentId);
			default -> null;
		};
	}
	
	public static BooleanWidget createB(Vector2i pos, ConfigEntry<Boolean> rule, Identifier parentId)
	{
		return new BooleanWidget(pos, rule, parentId);
	}
	
	public static IntWidget createI(Vector2i pos, ConfigEntry<Integer> rule, Identifier parentId)
	{
		return new IntWidget(pos, rule, parentId);
	}
	
	public static FloatWidget createF(Vector2i pos, ConfigEntry<Float> rule, Identifier parentId)
	{
		return new FloatWidget(pos, rule, parentId);
	}
	
	public static EnumWidget create(Vector2i pos, ConfigEntry<Enum<?>> rule, Enum<?>[] cycleValues, Identifier parentId)
	{
		return new EnumWidget(pos, rule, parentId, cycleValues);
	}
	
	public void render(DrawContext context, int mouseX, int mouseY, float delta, boolean simplistic)
	{
		alpha = MathHelper.clamp((getY() - 30) / 10f, 0f, 1f);
		if(alpha == 0f)
			return;
		RenderSystem.setShaderColor(0.69f, 0.69f, 0.69f, alpha);
		context.drawTexture(RenderLayer::getGuiTextured, simplistic ? SIMPLE_BG_TEXTURE : BG_TEXTURE, getX(), getY(), 0, 0,
				200, 36, 16, 16);
		RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
		context.fill(getX() + 1, getY(), getX() + 200, getY() + 1, 0xaaffffff);
		context.fill(getX(), getY(), getX() + 1, getY() + 36, 0xaaffffff);
		context.fill(getX(), getY() + 35, getX() + 200, getY() + 36, 0xaa000000);
		context.fill(getX() + 199, getY(), getX() + 200, getY() + 36, 0xaa000000);
		//context.drawBorder(getX() + 2, getY() + 2, 32, 32, 0xffffffff);
		context.drawTexture(RenderLayer::getGuiTextured, icon, getX() + 2, getY() + 2, 0, 0,
				32, 32, 32, 32);
		context.drawTextWithShadow(renderer, Text.translatable(rule.getTranslationKey(parentId.getNamespace())).getWithStyle(Style.EMPTY.withUnderline(true)).getFirst(),
				getX() + 36, getY() + 2, 0xffffffff);
		Text fullText = Text.translatable(rule.getTranslationKey(parentId.getNamespace()) + ".description");
		List<OrderedText> lines = renderer.wrapLines(fullText, 200 - 36 - getControlWidth());
		for (int i = 0; i < Math.min(lines.size(), 2); i++)
		{
			OrderedText t;
			if(i == 0 || lines.size() < 3)
				t = lines.get(i);
			else
			{
				StringBuilder sb = new StringBuilder();
				lines.get(i).accept((a, b, c) -> {
					if(a == 0 && b.getColor() != null)
						sb.append(Formatting.byName(b.getColor().getName()));
					sb.appendCodePoint(c);
					return c > 0;
				});
				t = Text.of(sb + "...").asOrderedText();
				setTooltip(Tooltip.of(fullText));
			}
			drawOutlinedText(context, renderer, t, getX() + 36, getY() + 13 + renderer.fontHeight * i, alpha);
		}
		((WidgetAccessor)controlWidget).setOffset(((WidgetAccessor)this).getOffset());
		((WidgetAccessor)controlWidget).setAlpha(Math.max(alpha, 0.02f));
		controlWidget.render(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);
	}
	
	abstract int getControlWidth();
	
	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta)
	{
	
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		boolean b = false;
		if(((Element) controlWidget).isMouseOver(mouseX, mouseY))
		{
			b = ((Element) controlWidget).mouseClicked(mouseX, mouseY, button);
			if(controlWidget instanceof TextFieldWidget textField)
				textField.setFocused(!textField.isFocused());
		}
		if(b)
			onControlUpdated();
		return b;
	}
	
	abstract void onControlUpdated();
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		boolean b = ((Element) controlWidget).keyPressed(keyCode, scanCode, modifiers);
		if(keyCode == 259)
			onControlUpdated();
		return b;
	}
	
	@Override
	public boolean charTyped(char chr, int modifiers)
	{
		if(!(controlWidget instanceof Numberfield number))
			return false;
		boolean b = number.charTyped(chr, modifiers);
		if(b)
			onControlUpdated();
		return b;
	}
	
	protected String changeNumber(String val)
	{
		val = val.replace(",", ".");
		if(val.isEmpty() || val.equals("."))
			val = "0";
		return val;
	}
	
	void setRuleClient(V value)
	{
		rule.setValue(value);
		if(!isClient())
			ClientPlayNetworking.send(new SyncConfigC2SPayload(parentId, rule.getAsNBT()));
	}
	
	/**
	 * @return whether this config entry belongs to clientside config
	 */
	boolean isClient()
	{
		return client;
	}
	
	public void setClient()
	{
		client = true;
	}
	
	public abstract void onExternalUpdate();
	
	@Override
	public void setFocused(boolean focused)
	{
	
	}
	
	@Override
	public boolean isFocused()
	{
		return false;
	}
	
	@Override
	public SelectionType getType()
	{
		return SelectionType.NONE;
	}
	
	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder)
	{
	
	}
	
	void drawOutlinedText(DrawContext context, TextRenderer textRenderer, OrderedText text, int x, int y, float alpha)
	{
		RenderSystem.setShaderColor(0.1f, 0.1f, 0.1f, alpha);
		context.drawText(textRenderer, text, x - 1, y, 0xff333333, false);
		context.drawText(textRenderer, text, x + 1, y, 0xff333333, false);
		context.drawText(textRenderer, text, x, y + 1, 0xff333333, false);
		context.drawText(textRenderer, text, x, y - 1, 0xff333333, false);
		RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
		
		context.drawText(textRenderer, text, x, y, 0xffdddddd, false);
	}
	
	public String getRuleId()
	{
		return rule.getId();
	}
	
	public static class BooleanWidget extends ConfigWidget<ConfigEntry<Boolean>, Boolean, CheckboxWidget>
	{
		public BooleanWidget(Vector2i pos, ConfigEntry<Boolean> rule, Identifier parentId)
		{
			super(pos, rule, parentId);
			controlWidget = CheckboxWidget.builder(Text.empty(), renderer).pos( getX() + 178, getY() + 14).maxWidth(20).checked(rule.getValue()).build();
		}
		
		@Override
		public void onExternalUpdate()
		{
			if(controlWidget.isChecked() != rule.getValue())
				controlWidget.onPress();
		}
		
		@Override
		void onControlUpdated()
		{
			setRuleClient(controlWidget.isChecked());
		}
		
		@Override
		int getControlWidth()
		{
			return 24;
		}
	}
	
	public static class IntWidget extends ConfigWidget<ConfigEntry<Integer>, Integer, Numberfield>
	{
		public IntWidget(Vector2i pos, ConfigEntry<Integer> rule, Identifier parentId)
		{
			super(pos, rule, parentId);
			controlWidget = new Numberfield(renderer, getX() + 151, getY() + 15, 46, 18, Text.empty());
			controlWidget.setText(String.valueOf(rule.getValue()));
		}
		
		@Override
		public void onExternalUpdate()
		{
			if(controlWidget.isFocused())
				return;
			controlWidget.setTextPredicate(s -> {
				for (char c : s.toCharArray())
				{
					if(!Character.isDigit(c))
						return false;
				}
				return true;
			});
			controlWidget.setText(rule.getValue().toString());
		}
		
		@Override
		void onControlUpdated()
		{
			changeNumber(controlWidget.getText());
		}
		
		@Override
		protected String changeNumber(String val)
		{
			val = super.changeNumber(val);
			if(val.contains("."))
				val = val.split("\\.")[0];
			int i = Integer.parseInt(val);
			if(i >= 0)
				setRuleClient(Integer.parseInt(val));
			return val;
		}
		
		@Override
		int getControlWidth()
		{
			return 52;
		}
		
		@Override
		public boolean charTyped(char chr, int modifiers)
		{
			if(chr == '.')
				return false;
			return super.charTyped(chr, modifiers);
		}
	}
	
	public static class FloatWidget extends ConfigWidget<ConfigEntry<Float>, Float, Numberfield>
	{
		public FloatWidget(Vector2i pos, ConfigEntry<Float> rule, Identifier parentId)
		{
			super(pos, rule, parentId);
			controlWidget = new Numberfield(renderer, getX() + 151, getY() + 15, 46, 18, Text.empty());
			controlWidget.setTextPredicate(s -> {
				for (char c : s.toCharArray())
				{
					if(!(Character.isDigit(c) && c != '.'))
						return false;
				}
				return !s.startsWith(".");
			});
			DecimalFormat format = new DecimalFormat("#.#");
			format.setMaximumFractionDigits(6);
			controlWidget.setText(format.format(rule.getValue()).replace(",", "."));
		}
		
		@Override
		public void onExternalUpdate()
		{
			if(controlWidget.isFocused())
				return;
			DecimalFormat format = new DecimalFormat("#.#");
			format.setMaximumFractionDigits(10);
			controlWidget.setText(format.format(rule.getValue()).replace(",", "."));
		}
		
		@Override
		void onControlUpdated()
		{
			changeNumber(controlWidget.getText());
		}
		
		@Override
		protected String changeNumber(String val)
		{
			val = super.changeNumber(val);
			if(val.startsWith("."))
				val = "0" + val;
			if(val.endsWith("."))
				val = val + "0";
			float f = Float.parseFloat(val);
			if(f >= 0)
				setRuleClient(Float.parseFloat(val));
			return val;
		}
		
		@Override
		int getControlWidth()
		{
			return 52;
		}
	}
	
	public static class EnumWidget extends ConfigWidget<ConfigEntry<Enum<?>>, Enum<?>, CyclingButtonWidget<Enum<?>>>
	{
		final Enum<?>[] cycleValues;
		
		public EnumWidget(Vector2i pos, ConfigEntry<Enum<?>> rule, Identifier parentId, Enum<?>[] cycleValues)
		{
			super(pos, rule, parentId);
			this.cycleValues = cycleValues;
			controlWidget = CyclingButtonWidget.builder((Enum<?> e) -> Text.of(e.name())).values(cycleValues).initially(rule.getValue())
								  .omitKeyText().build(getX() + 130, getY() + 14, 68, 20, Text.empty());
		}
		
		@Override
		public void onExternalUpdate()
		{
			controlWidget.setValue(rule.getValue());
		}
		
		@Override
		int getControlWidth()
		{
			return 72;
		}
		
		@Override
		void onControlUpdated()
		{
			setRuleClient(controlWidget.getValue());
		}
	}
}
