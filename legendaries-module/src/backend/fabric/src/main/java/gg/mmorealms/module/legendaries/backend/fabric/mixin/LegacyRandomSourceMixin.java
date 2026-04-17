package gg.mmorealms.module.legendaries.backend.fabric.mixin;

import com.raduvoinea.utils.logger.Logger;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.MarsagliaPolarGaussian;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.atomic.AtomicLong;

/**
 * This mixin transforms the original, fail‑fast RNG into one that simply spins
 * on contention and logs a notice when a collision occurs.
 * Any code creating a LegacyRandomSource will now get thread‑safe behavior automatically.
 */
@Mixin(LegacyRandomSource.class)
@SuppressWarnings("SynchronizeOnNonFinalField")
public class LegacyRandomSourceMixin {
	// Shadow the private constants and fields so we match vanilla exactly
	@Final
	@Shadow
	private static long MODULUS_MASK;
	@Final
	@Shadow
	private static long MULTIPLIER;
	@Final
	@Shadow
	private static long INCREMENT;

	@Final
	@Shadow
	private AtomicLong seed;
	@Final
	@Shadow
	private MarsagliaPolarGaussian gaussianSource;

	/**
	 * @author ZeroDelusions
	 * @reason Replace "compareAndSet + throw on failure" with a simple set(),
	 * so re‑seeding never throws under contention
	 */
	@Overwrite
	public void setSeed(long seedValue) {
		long scrambled = (seedValue ^ MULTIPLIER) & MODULUS_MASK;
		this.seed.set(scrambled);
		// Reset the Gaussian helper so old cached values are cleared
		this.gaussianSource.reset();
	}

	/**
	 * @author ZeroDelusions
	 * @reason Replace "one‑and‑done CAS + throw" with "spin until CAS succeeds"
	 */
	@Overwrite
	public int next(int bits) {
		long oldSeed, newSeed;
		int attempts = 0;
		final int MAX_ATTEMPTS = 100;

		do {
			oldSeed = this.seed.get();
			newSeed = (oldSeed * MULTIPLIER + INCREMENT) & MODULUS_MASK;
			attempts++;

			if (attempts > MAX_ATTEMPTS) {
				Logger.warn("Excessive LegacyRandomSource contention detected, forcing update");
				this.seed.set(newSeed); // Force update to break the loop
				break;
			}

		} while (!this.seed.compareAndSet(oldSeed, newSeed));

		if (attempts > 1) {
			Logger.warn("LegacyRandomSource collision detected. Retried CAS " + attempts + " times");
		}

		return (int) (newSeed >>> (48 - bits));
	}

	/**
	 * @author ZeroDelusions
	 * @reason Wrap nextGaussian() in a synchronized block so MarsagliaPolarGaussian’s internal
	 * cached fields are not raced across threads.
	 */
	@Overwrite
	public double nextGaussian() {
		synchronized (this.gaussianSource) {
			return this.gaussianSource.nextGaussian();
		}
	}
}
