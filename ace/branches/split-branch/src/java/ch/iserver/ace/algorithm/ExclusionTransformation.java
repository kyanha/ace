/*
 * $Id$
 *
 * ace - a collaborative editor
 * Copyright (C) 2005 Mark Bigler, Simon Raess, Lukas Zbinden
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package ch.iserver.ace.algorithm;

import ch.iserver.ace.Operation;

/**
 *
 */
public interface ExclusionTransformation {

    /**
     * Exclude operation <var>op2</var> from the context of operation 
     * <var>op1</var>. The transformed operation <var>op1'</var> is returned.
     * 
     * @param op1 the operation from which another is to be contextually excluded.
     * @param op2 the operation to be excluded.
     * @return the transformed operation <var>op1'</var>
     */
    public Operation transform(Operation op1, Operation op2);
    
    
}
