/*
 *  Copyright (C) 2010-2015 JPEXS
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
package com.sun.jna.platform.win32;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class ICONINFO extends Structure {

    public boolean fIcon;
    public DWORD xHotspot;
    public DWORD yHotspot;
    public HBITMAP hbmMask;
    public HBITMAP hbmColor;

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("fIcon", "xHotspot", "yHotspot", "hbmMask", "hbmColor");
    }
}
