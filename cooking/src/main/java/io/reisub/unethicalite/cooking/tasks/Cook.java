package io.reisub.unethicalite.cooking.tasks;

import io.reisub.unethicalite.cooking.Config;
import io.reisub.unethicalite.cooking.Cooking;
import io.reisub.unethicalite.cooking.data.PluginActivity;
import io.reisub.unethicalite.utils.api.Activity;
import io.reisub.unethicalite.utils.tasks.Task;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Item;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Production;
import net.unethicalite.client.Static;

@RequiredArgsConstructor
public class Cook extends Task {
  private final Cooking plugin;
  private final Config config;

  private int last;

  @Override
  public String getStatus() {
    return "Cooking";
  }

  @Override
  public boolean validate() {
    int count = Inventory.getCount(config.foodId());

    return !config.sonicMode()
        && (plugin.isCurrentActivity(Activity.IDLE) || count == 1)
        && (count > 0 || plugin.getLastBank() + 1 >= Static.getClient().getTickCount())
        && Static.getClient().getTickCount() >= last + 3;
  }

  @Override
  public void execute() {
    TileObject oven = TileObjects.getNearest(ObjectID.CLAY_OVEN_21302, ObjectID.RANGE_31631);
    TileObject fire = TileObjects.getNearest("Fire");
    if (oven == null && fire == null) {
      return;
    }

    int count = Inventory.getCount(config.foodId());

    if (oven != null) {
      oven.interact(0);
    } else {
      Item rawFood = Inventory.getFirst(config.foodId());
      if (rawFood == null) {
        return;
      }

      rawFood.useOn(fire);
    }

    if (count > 1
        || (count == 0 && plugin.getLastBank() + 1 >= Static.getClient().getTickCount())) {
      Time.sleepTicksUntil(Production::isOpen, 20);
    }

    if (Production.isOpen()) {
      Production.chooseOption(1);

      Time.sleepTicksUntil(() -> plugin.isCurrentActivity(PluginActivity.COOKING), 5);
    }

    last = Static.getClient().getTickCount();
  }
}
