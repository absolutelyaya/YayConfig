package absolutelyaya.yayconfig.gui.widget;

import absolutelyaya.yayconfig.YayConfig;
import absolutelyaya.yayconfig.accessor.WidgetAccessor;
import absolutelyaya.yayconfig.config.ConfigEntry;
import absolutelyaya.yayconfig.config.Constants;
import absolutelyaya.yayconfig.networking.SyncConfigC2SPayload;
import absolutelyaya.yayconfig.util.RenderingUtil;
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
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.Arrays;
import java.util.List;

public class ConfigWidget<T extends ConfigEntry<?>> extends ClickableWidget implements Element, Drawable, Selectable, Constants
{
	static final Identifier SIMPLE_BG_TEXTURE = YayConfig.indentifier("textures/gui/simplistic_bg.png");
	static final Identifier BG_TEXTURE = Identifier.of("textures/block/stone.png");
	static final Identifier PLACEHOLDER_ICON_TEXTURE = YayConfig.indentifier("textures/gui/placeholder_icon.png");
	
	final Identifier parentId;
	final T rule;
	final byte type;
	final Identifier icon;
	TextRenderer renderer;
	String[] cycleValues;
	Drawable valueWidget;
	
	public ConfigWidget(String value, Vector2i pos, T rule, byte type, Identifier icon, Identifier parentId)
	{
		super(pos.x, pos.y, 200, 36, Text.empty());
		this.parentId = parentId;
		this.rule = rule;
		this.type = type;
		renderer = MinecraftClient.getInstance().textRenderer;
		switch(type)
		{
			case BOOLEAN_TYPE -> valueWidget = CheckboxWidget.builder(Text.empty(), renderer).pos( getX() + 178, getY() + 14).maxWidth(20)
											   .checked(Boolean.parseBoolean(value)).build();
			case INT_TYPE, FLOAT_TYPE -> {
				valueWidget = new Numberfield(renderer, getX() + 151, getY() + 15, 46, 18, Text.empty());
				((TextFieldWidget)valueWidget).setText(value);
			}
		}
		if(icon != null)
			this.icon = icon;
		else
			this.icon = PLACEHOLDER_ICON_TEXTURE;
	}
	
	public ConfigWidget(String value, Vector2i pos, T rule, Enum<?>[] values, Identifier icon, Identifier parentId)
	{
		super(pos.x, pos.y, 200, 36, Text.empty());
		this.parentId = parentId;
		this.rule = rule;
		this.cycleValues = Arrays.stream(values).map(Enum::name).toArray(String[]::new);
		this.type = ENUM_TYPE;
		renderer = MinecraftClient.getInstance().textRenderer;
		valueWidget = CyclingButtonWidget.builder(o -> Text.of((String)o)).values(cycleValues).initially(value)
							  .omitKeyText().build(getX() + 130, getY() + 14, 68, 20, Text.empty());
		if(icon != null)
			this.icon = icon;
		else
			this.icon = PLACEHOLDER_ICON_TEXTURE;
	}
	
	public void render(DrawContext context, int mouseX, int mouseY, float delta, boolean simplistic)
	{
		alpha = MathHelper.clamp((getY() - 30) / 10f, 0f, 1f);
		RenderSystem.setShaderColor(0.69f, 0.69f, 0.69f, alpha);
		RenderSystem.setShaderTexture(0, simplistic ? SIMPLE_BG_TEXTURE : BG_TEXTURE);
		MatrixStack matrices = context.getMatrices();
		RenderingUtil.drawTexture(matrices.peek().getPositionMatrix(), new Vector4f(getX(), getY(), 200, 36), 0,
				new Vec2f(16, 16), new Vector4f(0f, 0, 100, 16), alpha);
		RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
		context.fill(getX(), getY(), getX() + 200, getY() + 1, 0xaaffffff);
		context.fill(getX(), getY(), getX() + 1, getY() + 36, 0xaaffffff);
		context.fill(getX(), getY() + 35, getX() + 200, getY() + 36, 0xaa000000);
		context.fill(getX() + 199, getY(), getX() + 200, getY() + 36, 0xaa000000);
		context.drawBorder(getX() + 2, getY() + 2, 32, 32, 0xffffffff);
		RenderSystem.setShaderTexture(0, icon);
		RenderingUtil.drawTexture(matrices.peek().getPositionMatrix(), new Vector4f(getX() + 2, getY() + 2, 32, 32), 1,
				new Vec2f(32, 32), new Vector4f(0, 0, 32, -32), alpha);
		context.drawTextWithShadow(renderer, Text.translatable(rule.getTranslationKey()).getWithStyle(Style.EMPTY.withUnderline(true)).getFirst(),
				getX() + 36, getY() + 2, 0xffffffff);
		int controlWidth = switch(type)
		{
			case BOOLEAN_TYPE -> 24;
			default -> 52;
			case ENUM_TYPE -> 72;
		};
		Text fullText = Text.translatable(rule.getTranslationKey() + ".description");
		List<OrderedText> lines = renderer.wrapLines(fullText.getWithStyle(Style.EMPTY.withColor(Formatting.GRAY)).getFirst(),
				200 - 36 - controlWidth);
		for (int i = 0; i < Math.min(lines.size(), 2); i++)
		{
			OrderedText t;
			if(i == 0 || lines.size() < 3)
				t = lines.get(i);
			else
			{
				StringBuilder sb = new StringBuilder();
				lines.get(i).accept((a, b, c) -> {
					if(a == 0)
						sb.append(Formatting.byName(b.getColor().getName()));
					sb.appendCodePoint(c);
					return c > 0;
				});
				t = Text.of(sb + "...").asOrderedText();
				setTooltip(Tooltip.of(fullText));
			}
			drawOutlinedText(context, renderer, t, getX() + 36, getY() + 13 + renderer.fontHeight * i, alpha);
		}
		((WidgetAccessor)valueWidget).setOffset(((WidgetAccessor)this).getOffset());
		((WidgetAccessor)valueWidget).setAlpha(Math.max(alpha, 0.02f));
		valueWidget.render(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);
	}
	
	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta)
	{
	
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		boolean b = false;
		if(((Element)valueWidget).isMouseOver(mouseX, mouseY))
		{
			b = ((Element)valueWidget).mouseClicked(mouseX, mouseY, button);
			if(valueWidget instanceof TextFieldWidget textField)
				textField.setFocused(true);
		}
		else if(valueWidget instanceof Numberfield numberField && numberField.isFocused())
			numberField.setFocused(false);
		if(b)
		{
			if(valueWidget instanceof CheckboxWidget checkbox)
				setRuleClient(checkbox.isChecked());
			else if(valueWidget instanceof CyclingButtonWidget<?> cycler)
				setRuleClient(cycler.getValue());
		}
		return b;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		boolean b = ((Element)valueWidget).keyPressed(keyCode, scanCode, modifiers);
		if(valueWidget instanceof Numberfield field && keyCode == 259)
			changeNumber(field.getText());
		return b;
	}
	
	@Override
	public boolean charTyped(char chr, int modifiers)
	{
		if(type == INT_TYPE && chr == '.')
			return false;
		boolean b = ((Element)valueWidget).charTyped(chr, modifiers);
		if(b)
			changeNumber(((TextFieldWidget)valueWidget).getText());
		return b;
	}
	
	void changeNumber(String val)
	{
		if(val.isEmpty() || val.equals("."))
			val = "0";
		if(type == INT_TYPE)
		{
			if(val.contains("."))
				val = val.split("\\.")[0];
			int i = Integer.parseInt(val);
			if(i >= 0)
				setRuleClient(Integer.parseInt(val));
		}
		else if(type == FLOAT_TYPE)
		{
			if(val.startsWith("."))
				val = "0" + val;
			if(val.endsWith("."))
				val = val + "0";
			float f = Float.parseFloat(val);
			if(f >= 0)
				setRuleClient(Float.parseFloat(val));
		}
	}
	
	void setRuleClient(Object value)
	{
		rule.setValue(value);
		ClientPlayNetworking.send(new SyncConfigC2SPayload(parentId, rule.getAsNBT()));
	}
	
	public void stateUpdate()
	{
		switch(type)
		{
			case BOOLEAN_TYPE -> {
				if(((CheckboxWidget)valueWidget).isChecked() != Boolean.parseBoolean(rule.getValue().toString()))
					((CheckboxWidget)valueWidget).onPress();
			}
			case INT_TYPE -> ((TextFieldWidget)valueWidget).setText(rule.getValue().toString());
			case ENUM_TYPE -> ((CyclingButtonWidget)valueWidget).setValue(cycleValues[Integer.parseInt(rule.getValue().toString())]);
		}
	}
	
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
		context.drawText(textRenderer, text, x - 1, y, 0xff444444, false);
		context.drawText(textRenderer, text, x + 1, y, 0xff444444, false);
		context.drawText(textRenderer, text, x, y + 1, 0xff444444, false);
		context.drawText(textRenderer, text, x, y - 1, 0xff444444, false);
		RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
		
		context.drawText(textRenderer, text, x, y, 0xffffffff, false);
	}
	
	public <R extends ConfigEntry<?>> boolean isRule(R rule)
	{
		return this.rule.getId().equals(rule.getId());
	}
}
