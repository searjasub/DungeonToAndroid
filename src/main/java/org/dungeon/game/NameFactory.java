/*
 * Copyright (C) 2015 Bernardo Sulzbach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dungeon.game;

import org.dungeon.io.DungeonLogger;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.jetbrains.annotations.NotNull;

/**
 * A factory of names. All Name objects should be created through this factory.
 */
public final class NameFactory {

  private NameFactory() {
    throw new AssertionError();
  }

  /**
   * Creates a new Name from a singular form.
   *
   * @param singular the singular form, not null
   * @return a Name constructed using the provided singular form and this form concatenated with an 's'
   */
  public static Name newInstance(@NotNull String singular) {
    return newInstance(singular, singular + 's');
  }

  /**
   * Creates a new Name from a singular and a plural form.
   *
   * @param singular the singular form
   * @param plural the plural form
   * @return a Name constructed using the provided singular and plural forms
   */
  private static Name newInstance(String singular, String plural) {
    return new Name(singular, plural);
  }

  /**
   * Convenience method that creates a Name from an array of Strings.
   *
   * @param object a JSON object of the form {"singular": "..."} or {"singular": "...", "plural": "..."}.
   * @return a Name
   */
  public static Name nameFromJsonObject(JsonObject object) {
    if (object.get("plural") == null) {
      return newInstance(object.get("singular").asString());
    } else {
      return newInstance(object.get("singular").asString(), object.get("plural").asString());
    }
  }

  /**
   * Creates the Name the corpse Item of a creature whose name is provided should have.
   *
   * @param creatureName the Name of the creature
   * @return a Name object
   */
  public static Name newCorpseName(Name creatureName) {
    return newInstance(creatureName.getSingular() + " Corpse");
  }

  /**
   * Attempts to generate a Name object from a JSON object.
   *
   * <p>The JSON object should have the form:
   * <pre>
   *   {"singular": "singularForm",
   *    "plural": "pluralForm"}}
   * </pre>
   *
   *
   * <p>If the plural form is obtained by appending 's' to the singular form, the plural form should be omitted.
   * Becoming simply:
   * <pre>
   *   {"singular": "singularForm"}
   * </pre>
   * This rule helps remove unnecessary information from developers reading the resource files.
   *
   * @param jsonObject a JsonObject, not null, as specified in the Javadoc
   * @return a Name object
   */
  public static Name fromJsonObject(@NotNull JsonObject jsonObject) {
    String singular = jsonObject.get("singular").asString();
    String plural = readOrRenderPlural(jsonObject, singular);
    return newInstance(singular, plural);
  }

  private static String readOrRenderPlural(@NotNull JsonObject jsonObject, String singular) {
    JsonValue value = jsonObject.get("plural");
    if (value == null) {
      return singular + 's';
    } else {
      String plural = value.asString();
      warnIfPluralIsUnnecessary(singular, plural);
      return plural;
    }
  }

  private static void warnIfPluralIsUnnecessary(@NotNull String singular, @NotNull String plural) {
    if ((singular + 's').equals(plural)) {
      DungeonLogger.warning("Unnecessary JSON property: " + plural + " can be rendered from " + singular + ".");
    }
  }

}
