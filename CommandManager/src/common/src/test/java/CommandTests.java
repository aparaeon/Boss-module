import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.commandmanager.common.config.CommandManagerConfig;
import com.raduvoinea.utils.reflections.Reflections;
import command.*;
import manager.SimpleCommandManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommandTests {

	private static SimpleCommandManager commandManager;

	@BeforeAll
	public static void setup() {
		commandManager = new SimpleCommandManager(Reflections.simple(
				CommandTests.class.getClassLoader(),
				BaseCommand.class,
				SubCommand1.class,
				SubCommand2.class,
				SubSubCommand1.class,
				SubSubCommand2.class
		), new CommandManagerConfig());
		commandManager.register(BaseCommand.class);
	}

	@Test
	public void testCommandUsage() {
		CommonCommand command = commandManager.getCommand(BaseCommand.class);

		assertNotNull(command);

		String usage = command.getUsage();

		assertEquals("""
				Usage: /base <b1> <b2>
				
				Sub commands:
				/base sub1 <s1> <s2>
				/base sub1 subsub1 <ss1> <ss2>
				/base sub2 <s1> <s2>
				/base sub2 subsub2 <ss1> <ss2>
				""", usage);
	}

	@Test
	public void testSubCommandUsage() {
		CommonCommand command = commandManager.getCommand(SubCommand1.class);

		assertNotNull(command);

		String usage = command.getUsage();

		assertEquals("""
				Usage: /base sub1 <s1> <s2>
				
				Sub commands:
				/base sub1 subsub1 <ss1> <ss2>
				""", usage);
	}

}
