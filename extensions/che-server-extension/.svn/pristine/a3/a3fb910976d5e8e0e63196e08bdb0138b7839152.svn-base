/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package  com.sgitg.cuap.wpc.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/aaa")
public class MyService {

	@GET
	@Path("/hello/{xname}")
	public String sayHello(@PathParam("xname") String name) {
		//        try {
		//            Thread.sleep(2000);                 //1000 milliseconds is one second.
		//        } catch(InterruptedException ex) {
		//            Thread.currentThread().interrupt();
		//        }
		return "你好: " + name + " !";

		}

		@GET
		@Path("/sayHello/{a},{b}")
		public String sayHelloa2(@PathParam("a") String name,@PathParam("b") String name2) {
		//        try {
		//            Thread.sleep(2000);                 //1000 milliseconds is one second.
		//        } catch(InterruptedException ex) {
		//            Thread.currentThread().interrupt();
		//        }
		return "大家好: " + name+"/" +name2 + " !";

	}
}