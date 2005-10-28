/*
 *   @(#) $Id: IoSessionFilterChain.java 327113 2005-10-21 06:59:15Z trustin $
 *
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.mina.io;

import org.apache.mina.common.ByteBuffer;

/**
 * An {@link IoFilterChain} that forwards <tt>filterWrite</tt>
 * requests to the specified {@link IoSessionManagerFilterChain}.
 * <p>
 * This filter chain is used by implementations of {@link IoSession}s.
 * 
 * @author The Apache Directory Project (dev@directory.apache.org)
 * @version $Rev: 327113 $, $Date: 2005-10-21 15:59:15 +0900 $
 */
public class IoSessionFilterChain extends AbstractIoFilterChain {

    private final IoSessionManagerFilterChain managerChain;

    public IoSessionFilterChain( IoSessionManagerFilterChain managerChain )
    {
        this.managerChain = managerChain;
    }

    protected void doWrite( IoSession session, ByteBuffer buf, Object marker )
    {
        managerChain.filterWrite( session, buf, marker );
    }
}
