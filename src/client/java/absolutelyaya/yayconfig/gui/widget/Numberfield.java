package absolutelyaya.yayconfig.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class Numberfield extends TextFieldWidget
{
	public Numberfield(TextRenderer textRenderer, int x, int y, int width, int height, Text text)
	{
		super(textRenderer, x, y, width, height, null, text);
		setDrawsBackground(false);
	}
	
	public boolean charTyped(char chr, int modifiers)
	{
		if (isActive() && isValidChar(chr))
		{
			write(Character.toString(chr));
			return true;
		}
		return false;
	}
	
	protected boolean isValidChar(char c)
	{
		return Character.isDigit(c) || (c == '.' && !getText().contains("."));
	}
	
	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta)
	{
		context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xff000000);
		context.fill(getX(), getY(), getX() + getWidth(), getY() + 1, 0xff555555);
		context.drawBorder(getX(), getY(), getWidth(), getHeight(), isFocused() ? 0xffffffff : 0xff555555);
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(3, 5, 0);
		super.renderWidget(context, mouseX, mouseY, delta);
		matrices.pop();
	}
}
