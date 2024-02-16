@file:JvmSynthetic

package com.octacore.rexpay.data.cache

import java.util.UUID

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 16/02/2024
 **************************************************************************************************/
internal data class Session(val id: UUID = UUID.randomUUID())
