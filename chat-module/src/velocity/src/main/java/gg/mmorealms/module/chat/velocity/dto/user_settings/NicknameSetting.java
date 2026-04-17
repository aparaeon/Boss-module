package gg.mmorealms.module.chat.velocity.dto.user_settings;

import com.raduvoinea.utils.generic.dto.Pair2;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.chat.velocity.ChatVelocityModule;
import gg.mmorealms.module.chat.velocity.exceptions.InvalidNicknameException;
import gg.mmorealms.module.user_data.common.dto.IUserSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

import java.util.List;

@Getter
@NoArgsConstructor
public class NicknameSetting implements IUserSetting<Player> {

	private static final TagResolver strippingTagResolved = TagResolver.builder()
			.resolvers(
					StandardTags.decorations(),
//					StandardTags.color(),
					StandardTags.hoverEvent(),
					StandardTags.clickEvent(),
					StandardTags.keybind(),
					StandardTags.translatable(),
					StandardTags.translatableFallback(),
					StandardTags.insertion(),
					StandardTags.font(),
//					StandardTags.gradient(),
					StandardTags.rainbow(),
					StandardTags.transition(),
					StandardTags.reset(),
					StandardTags.newline(),
					StandardTags.selector(),
					StandardTags.score(),
					StandardTags.nbt(),
					StandardTags.pride(),
					StandardTags.shadowColor()
			).build();

	private final List<Pair2<String, String>> legacyColorCodes = List.of(
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

	private String value = "";

	public String setValue(String nickname) throws InvalidNicknameException {
		if (nickname.isEmpty()) {
			this.value = "";
		}

		String strippedNickname = MiniMessage.miniMessage().stripTags(nickname);

		if (!ChatVelocityModule.instance().getConfig().nicknameLength.contains(strippedNickname.length())) {
			throw new InvalidNicknameException(new MessageBuilder("Nickname must be between {min} and {max} characters")
					.parse("min", ChatVelocityModule.instance().getConfig().nicknameLength.getMin())
					.parse("max", ChatVelocityModule.instance().getConfig().nicknameLength.getMax())
					.toString());
		}

		for (String blacklistedWord : ChatVelocityModule.instance().getConfig().blacklist) {
			if (nickname.toLowerCase().contains(blacklistedWord.toLowerCase())) {
				throw new InvalidNicknameException("Nickname contains blacklisted words");
			}
		}

		for (Pair2<String, String> legacyColorCode : legacyColorCodes) {
			nickname = nickname.replace(legacyColorCode.first(), legacyColorCode.second());
		}

		nickname = MiniMessage.builder()
				.tags(strippingTagResolved)
				.build().stripTags(nickname);

		nickname = nickname.replaceAll("[^\\x00-\\x7F]", ""); // remove non-ASCII characters

		nickname += "<white>";
		this.value = nickname;
		return this.value;
	}

	@Override
	public void apply(Player player) {
		//nop
	}
}
