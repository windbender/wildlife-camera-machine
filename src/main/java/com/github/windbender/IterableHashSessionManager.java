package com.github.windbender;

import java.util.Iterator;

import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.HashedSession;

import com.github.windbender.domain.User;

public class IterableHashSessionManager extends HashSessionManager {
	public IterableHashSessionManager()
    {
        super();
    }
	
	public void operateOn(SessionOperator sessionOperator, User findUser)
    {
        //don't attempt to scavenge if we are shutting down
        if (isStopping() || isStopped())
            return;

        Thread thread=Thread.currentThread();
        ClassLoader old_loader=thread.getContextClassLoader();
        try
        {
            if (_loader!=null)
                thread.setContextClassLoader(_loader);

            // For each session
            long now=System.currentTimeMillis();
          
            for (Iterator<HashedSession> i=_sessions.values().iterator(); i.hasNext();)
            {
            	HashedSession session=i.next();
            	sessionOperator.operate(session, findUser);
            }
        }       
        finally
        {
            thread.setContextClassLoader(old_loader);
        }
    }
}
