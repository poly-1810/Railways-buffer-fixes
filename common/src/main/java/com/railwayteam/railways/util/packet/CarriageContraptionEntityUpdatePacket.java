/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.mixin.AccessorCarriageContraptionEntity;
import com.railwayteam.railways.mixin_interfaces.IUpdateCount;
import com.railwayteam.railways.multiloader.S2CPacket;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class CarriageContraptionEntityUpdatePacket implements S2CPacket {
  final int id;
  final int carriageIndex;
  final UUID trainId;

  public CarriageContraptionEntityUpdatePacket(CarriageContraptionEntity target, Train train) {
    id = target.getId();
    carriageIndex = target.carriageIndex;
    trainId = train.id;
  }

  public CarriageContraptionEntityUpdatePacket(FriendlyByteBuf buf) {
    id = buf.readInt();
    carriageIndex = buf.readInt();
    trainId = buf.readUUID();
  }

  @Override
  public void write(FriendlyByteBuf buffer) {
    buffer.writeInt(this.id);
    buffer.writeInt(this.carriageIndex);
    buffer.writeUUID(this.trainId);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void handle(Minecraft mc) {
    Level level = mc.level;
    if (level != null) {
      Entity target = level.getEntity(this.id);
      if (target instanceof CarriageContraptionEntity cce) {
        cce.trainId = trainId;
        ((AccessorCarriageContraptionEntity) cce).railways$setCarriage(null);
        cce.carriageIndex = carriageIndex;
        ((AccessorCarriageContraptionEntity) cce).railways$bindCarriage();
        ((IUpdateCount) cce).railways$markUpdate();
      }
    }
  }
}
