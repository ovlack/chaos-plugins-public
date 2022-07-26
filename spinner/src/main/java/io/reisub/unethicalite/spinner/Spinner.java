package io.reisub.unethicalite.spinner;

import com.google.inject.Provides;
import io.reisub.unethicalite.spinner.data.PluginActivity;
import io.reisub.unethicalite.spinner.tasks.GoToBank;
import io.reisub.unethicalite.spinner.tasks.GoToWheel;
import io.reisub.unethicalite.spinner.tasks.HandleBank;
import io.reisub.unethicalite.spinner.tasks.Spin;
import io.reisub.unethicalite.utils.TickScript;
import io.reisub.unethicalite.utils.Utils;
import io.reisub.unethicalite.utils.api.Activity;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.items.Inventory;
import org.pf4j.Extension;
import org.slf4j.Logger;

@PluginDescriptor(
    name = "Chaos Spinner",
    description = "You spin me right 'round, baby, right 'round",
    enabledByDefault = false
)
@PluginDependency(Utils.class)
@Slf4j
@Extension
public class Spinner extends TickScript {

  @Inject
  private Config config;
  @Getter
  @Setter
  private int lastBankTick;

  @Provides
  public Config getConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  protected void onStart() {
    super.onStart();

    addTask(GoToBank.class);
    addTask(HandleBank.class);
    addTask(GoToWheel.class);
    addTask(Spin.class);
  }

  @Subscribe
  private void onItemContainerChanged(ItemContainerChanged event) {
    if (!isRunning()) {
      return;
    }

    if (isCurrentActivity(PluginActivity.SPINNING)
        && !Inventory.contains(config.material().getId())) {
      setActivity(Activity.IDLE);
    }
  }
}
