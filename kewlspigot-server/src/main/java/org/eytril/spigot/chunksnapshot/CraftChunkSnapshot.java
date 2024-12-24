package org.eytril.spigot.chunksnapshot;

import lombok.Getter;
import net.minecraft.server.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class CraftChunkSnapshot {
   @Getter
   public final ChunkSectionSnapshot[] sections = new ChunkSectionSnapshot[16];
   public final List tileEntities = new ArrayList();

    public List<NBTTagCompound> getTileEntities() {
      return this.tileEntities;
   }
}
