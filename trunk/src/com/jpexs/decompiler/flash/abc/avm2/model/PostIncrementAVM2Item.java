/*
 *  Copyright (C) 2010-2014 JPEXS
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.flash.abc.avm2.model;

import com.jpexs.decompiler.flash.SourceGeneratorLocalData;
import com.jpexs.decompiler.flash.abc.avm2.instructions.AVM2Instruction;
import com.jpexs.decompiler.flash.abc.avm2.instructions.arithmetic.IncrementIns;
import com.jpexs.decompiler.flash.abc.avm2.instructions.localregs.GetLocalIns;
import com.jpexs.decompiler.flash.abc.avm2.instructions.localregs.KillIns;
import com.jpexs.decompiler.flash.abc.avm2.instructions.localregs.SetLocalIns;
import com.jpexs.decompiler.flash.abc.avm2.instructions.other.FindPropertyStrictIns;
import com.jpexs.decompiler.flash.abc.avm2.instructions.other.GetPropertyIns;
import com.jpexs.decompiler.flash.abc.avm2.instructions.other.SetPropertyIns;
import com.jpexs.decompiler.flash.abc.avm2.instructions.stack.DupIns;
import com.jpexs.decompiler.flash.abc.avm2.model.clauses.AssignmentAVM2Item;
import com.jpexs.decompiler.flash.abc.avm2.parser.script.AVM2SourceGenerator;
import com.jpexs.decompiler.flash.abc.avm2.parser.script.NameAVM2Item;
import com.jpexs.decompiler.flash.abc.avm2.parser.script.PropertyAVM2Item;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.model.LocalData;
import java.util.ArrayList;
import java.util.List;

public class PostIncrementAVM2Item extends AVM2Item implements AssignmentAVM2Item {

    public GraphTargetItem object;

    public PostIncrementAVM2Item(AVM2Instruction instruction, GraphTargetItem object) {
        super(instruction, PRECEDENCE_POSTFIX);
        this.object = object;
    }

    @Override
    public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData) throws InterruptedException {
        object.toString(writer, localData);
        return writer.append("++");
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }

    @Override
    public GraphTargetItem returnType() {
        return object.returnType();
    }

    @Override
    public boolean hasReturnValue() {
        return true;
    }

    @Override
    public List<GraphSourceItem> toSource(SourceGeneratorLocalData localData, SourceGenerator generator) {
        AVM2SourceGenerator g = (AVM2SourceGenerator) generator;
        int objectTempReg = g.getFreeRegister(localData);
        if (object instanceof PropertyAVM2Item) {
            PropertyAVM2Item p = (PropertyAVM2Item) object;
            int propertyId = p.resolveProperty();
            Object obj = p.object;
            GraphTargetItem index = p.index;
            if (obj == null) {
                obj = new AVM2Instruction(0, new FindPropertyStrictIns(), new int[]{propertyId}, new byte[0]);
            }

            List<GraphSourceItem> ret = toSourceMerge(localData, generator, obj,
                    new AVM2Instruction(0, new DupIns(), new int[]{}, new byte[0]),
                    new AVM2Instruction(0, new SetLocalIns(), new int[]{objectTempReg}, new byte[0])
            );
            int indexTempReg = 0;
            if (index != null) {
                indexTempReg = g.getFreeRegister(localData);
                ret.addAll(toSourceMerge(localData, generator, index,
                        new AVM2Instruction(0, new DupIns(), new int[]{}, new byte[0]),
                        new AVM2Instruction(0, new SetLocalIns(), new int[]{indexTempReg}, new byte[0])
                ));
            }

            ret.addAll(toSourceMerge(localData, generator,
                    new AVM2Instruction(0, new GetPropertyIns(), new int[]{}, new byte[0]),
                    new AVM2Instruction(0, new DupIns(), new int[]{}, new byte[0]),
                    new AVM2Instruction(0, new IncrementIns(), new int[]{}, new byte[0]),
                    new AVM2Instruction(0, new GetLocalIns(), new int[]{objectTempReg}, new byte[0])
            ));

            if (index != null) {
                ret.add(new AVM2Instruction(0, new GetLocalIns(), new int[]{indexTempReg}, new byte[0]));
            }
            ret.add(new AVM2Instruction(0, new SetPropertyIns(), new int[]{propertyId}, new byte[0]));
            ret.add(new AVM2Instruction(0, new KillIns(), new int[]{objectTempReg}, new byte[0]));
            if (index != null) {
                ret.add(new AVM2Instruction(0, new KillIns(), new int[]{indexTempReg}, new byte[0]));
            }
            return ret;
        }
        if(object instanceof NameAVM2Item){
            //TODO
                      
        }
        return toSourceMerge(localData, generator, object);
    }

}
