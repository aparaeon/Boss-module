package gg.mmorealms.module.pokemon.backend.common.utils;

import com.raduvoinea.utils.generic.dto.Pair2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.List;

public final class PokemonNameFormatter {

	private static final TagResolver RESOLVER = TagResolver.builder()
		.resolver(StandardTags.color())
		.resolver(StandardTags.gradient())
		.resolver(StandardTags.rainbow())
		.resolver(StandardTags.transition())
		.resolver(StandardTags.reset())

		.resolver(TagResolver.resolver("bold", Tag.styling(TextDecoration.BOLD)))
		.resolver(TagResolver.resolver("b", Tag.styling(TextDecoration.BOLD)))

		.build();
	private static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
		.tags(RESOLVER)
		.build();
	// legacy support
	private static final List<Pair2<String, String>> LEGACY = List.of(
		new Pair2<>("&0", "<black>"),
		new Pair2<>("&1", "<dark_blue>"),
		new Pair2<>("&2", "<dark_green>"),
		new Pair2<>("&3", "<dark_aqua>"),
		new Pair2<>("&4", "<dark_red>"),
		new Pair2<>("&5", "<dark_purple>"),
		new Pair2<>("&6", "<gold>"),
		new Pair2<>("&7", "<gray>"),
		new Pair2<>("&8", "<dark_gray>"),
		new Pair2<>("&9", "<blue>"),
		new Pair2<>("&a", "<green>"),
		new Pair2<>("&b", "<aqua>"),
		new Pair2<>("&c", "<red>"),
		new Pair2<>("&d", "<light_purple>"),
		new Pair2<>("&e", "<yellow>"),
		new Pair2<>("&f", "<white>"),
		new Pair2<>("&k", "<obfuscated>"),
		new Pair2<>("&l", "<bold>"),
		new Pair2<>("&m", "<strikethrough>"),
		new Pair2<>("&n", "<underline>"),
		new Pair2<>("&o", "<italic>")
	);

	private PokemonNameFormatter() {
	}

	public static String sanitize(String input, String fallbackName) {
		if (input == null || input.isBlank()) {
			return "";
		}

		String processed = input;

		for (Pair2<String, String> pair : LEGACY) {
			processed = processed.replace(pair.first(), pair.second());
		}

		String stripped = MINI_MESSAGE.stripTags(processed);

		if (stripped.isBlank()) {
			processed = processed + fallbackName;
		}

		// Strips all non-ASCII characters (keeps only characters in range 0–127)
		processed = processed.replaceAll("[^\\x00-\\x7F]", "");
		processed += "<reset>";

		return processed;
	}

	public static Component parse(String sanitized) {
		return MINI_MESSAGE.deserialize(sanitized);
	}

	public static MutableComponent formatToNms(String input, String fallbackName) {
		String sanitized = sanitize(input, fallbackName);
		if (sanitized.isEmpty()) {
			return null;
		}
		return adventureToNms(parse(sanitized));
	}

	private static MutableComponent adventureToNms(Component component) {
		String content = component instanceof TextComponent tc ? tc.content() : "";
		List<Component> children = component.children();

		if (content.isEmpty() && !children.isEmpty()) {
			MutableComponent root = adventureToNms(children.get(0));

			// Merge parent style as base — parent values fill in where child has none.
			// e.g. <bold><gradient:...> → bold from parent, color from child.
			Style parentNmsStyle = toNmsStyle(component.style());
			root.setStyle(parentNmsStyle.applyTo(root.getStyle()));

			for (int i = 1; i < children.size(); i++) {
				root.append(adventureToNms(children.get(i)));
			}
			return root;
		}

		MutableComponent nms = net.minecraft.network.chat.Component.literal(content);
		nms.setStyle(toNmsStyle(component.style()));

		for (Component child : children) {
			nms.append(adventureToNms(child));
		}

		return nms;
	}

	private static Style toNmsStyle(net.kyori.adventure.text.format.Style adventureStyle) {
		Style nmsStyle = Style.EMPTY;

		TextColor color = adventureStyle.color();
		if (color != null) {
			nmsStyle = nmsStyle.withColor(net.minecraft.network.chat.TextColor.fromRgb(color.value()));
		}

		for (TextDecoration decoration : TextDecoration.values()) {
			TextDecoration.State state = adventureStyle.decoration(decoration);
			if (state == TextDecoration.State.NOT_SET) {
				continue;
			}

			boolean value = state == TextDecoration.State.TRUE;
			nmsStyle = switch (decoration) {
				case BOLD -> nmsStyle.withBold(value);
				case ITALIC -> nmsStyle.withItalic(value);
				case OBFUSCATED -> nmsStyle.withObfuscated(value);
				case STRIKETHROUGH -> nmsStyle.withStrikethrough(value);
				case UNDERLINED -> nmsStyle.withUnderlined(value);
			};
		}

		return nmsStyle;
	}

}