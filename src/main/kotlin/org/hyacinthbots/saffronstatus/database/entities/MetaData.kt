/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.saffronstatus.database.entities

import kotlinx.serialization.Serializable

/**
 * The metadata for the database.
 *
 * @property version The version of the database
 * @property id The database id
 */
@Serializable
data class MetaData(
	val version: Int,
	val id: String = "meta"
)
