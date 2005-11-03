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

package ch.iserver.ace.application;

import java.util.Locale;

import javax.swing.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class Main {
	
	private static final String[] CONTEXT_FILES = new String[] {
		"application-context.xml"
	};
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT_FILES);
		LocaleMessageSource ms = new LocaleMessageSourceImpl(context);
		
		System.out.println(ms.getIcon("iViewUser"));

		/*
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		UserView uv = new UserView(new LocaleMessageSourceImpl(context));
		frame.getContentPane().add(uv);
		uv.getUserViewSource().add(new BasicUserListItem("huhu"));		
		frame.show();*/
	}
	
}
