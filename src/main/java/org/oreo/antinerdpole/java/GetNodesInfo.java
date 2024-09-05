package org.oreo.antinerdpole.java;

import org.bukkit.block.Block;
import phonon.nodes.Nodes;
import phonon.nodes.objects.Territory;
import phonon.nodes.war.FlagWar;

public class GetNodesInfo {

    /**
     *  yes I know this is terrible coding but I couldn't find how to do this in kotlin for th life of me
     * @return boolean war is on
     */
    public static boolean isWarOn(){
        return FlagWar.INSTANCE.getEnabled$nodes();
    }

    public static Territory getTerritorry(Block block){
        return Nodes.INSTANCE.getTerritoryFromBlock(block.getX(),block.getZ());
    }
}
