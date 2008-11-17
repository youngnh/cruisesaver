package status;

import java.awt.Component;

import org.jdesktop.jdic.screensaver.ScreensaverContext;
import org.jdesktop.jdic.screensaver.ScreensaverSettings;
import org.junit.Assert;
import org.junit.Test;

public class CCCollidingStatusTest {

	@Test
	public void testInitWhenHostAndPortAreNotEqualSizeCommaSeparatedLists() throws Exception {
		CCCollidingStatus testObject = new CCCollidingStatus();
		ScreensaverContext context = new ScreensaverContextStub(
				"-HOST debug -PORT 1,9 -PROTOCOL JMX");
		testObject.baseInit(context);

		testObject.init();

		Assert.assertEquals(CCCollidingStatus.CONFIG_ERROR_PREFIX
				+ "hosts and ports not of equal length comma separated strings.",
				testObject.messageRenderer.getMessage());

	}

	@Test
	public void testInitWhenNoProjectsFoundSetsMessageRenderer() throws Exception {
		CCCollidingStatus testObject = new CCCollidingStatus();
		ScreensaverContext context = new ScreensaverContextStub(
				"-HOST localhost -PORT 80 -PROTOCOL JMX");
		testObject.baseInit(context);

		testObject.init();

		Assert.assertEquals("used to default message in init, now wait until first render", null,
				testObject.messageRenderer.getMessage());

	}

	@Test
	public void testInitWhenDebugHostPortZeroSetsMessageRenderer() throws Exception {
		CCCollidingStatus testObject = new CCCollidingStatus();
		ScreensaverContext context = new ScreensaverContextStub("-HOST debug -PORT 0 -PROTOCOL JMX");
		testObject.baseInit(context);

		testObject.init();

		Assert.assertEquals("used to default message in init, now wait until first render", null,
				testObject.messageRenderer.getMessage());

	}

	class ScreensaverContextStub extends ScreensaverContext {

		private final String args;

		ScreensaverContextStub(String args) {
			this.args = args;

		}

		@Override
		public ScreensaverSettings getSettings() {
			ScreensaverSettings settings = new ScreensaverSettings();
			settings.loadFromCommandline(args);
			return settings;
		}

		@Override
		public Component getComponent() {
			return new Component() {
				private static final long serialVersionUID = 1L;

				@Override
				public int getHeight() {
					return 400;
				}
			};
		}
	}
}
