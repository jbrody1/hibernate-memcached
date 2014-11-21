package com.speakeasy.memcached;

import java.util.Collection;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ClientMode;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.DefaultHashAlgorithm;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.config.NodeEndPoint;
import net.spy.memcached.internal.OperationFuture;

import org.junit.Assert;
import org.junit.Test;

public class TestBasics {

	@Test
	public void testConnection() throws Exception {		
		MemcachedClient client = new MemcachedClient(buildDefaultConnectionFactory(),
				AddrUtil.getAddresses(getServerList()));
		String key="cabbage"+System.currentTimeMillis();
		String value="babbage"+System.currentTimeMillis();
		String o=(String) client.get(key);
		Assert.assertNull(o);
		Collection<NodeEndPoint> c=client.getAllNodeEndPoints();
		Assert.assertEquals(2, c.size());
		System.out.println("THE NODES: ");
		for (NodeEndPoint nodeEndPoint : c) {
			System.out.println(nodeEndPoint.getHostName()+"::"+nodeEndPoint.getIpAddress()+":"+nodeEndPoint.getPort());
		}	
		OperationFuture<Boolean> f=client.set(key, 600, value);
		f.get();
		o=(String) client.get(key);
		Assert.assertNotNull(o);
		Assert.assertEquals(value,o);
	}
	
    private String getServerList() {
		return "cache1.stage.speakeasyapp.net:11211";
//    	return "localhost:11211";
	}

	protected DefaultConnectionFactory buildDefaultConnectionFactory() {
        return new DefaultConnectionFactory(ClientMode.Dynamic,getOperationQueueLength(), 
        		getReadBufferSize(), getHashAlgorithm()) {
            @Override
            public long getOperationTimeout() {
                return getOperationTimeoutMillis();
            }

            private long getOperationTimeoutMillis() {
				return 2500;
			}

			@Override
            public boolean isDaemon() {
                return isDaemonMode();
            }

            private boolean isDaemonMode() {
				return false;
			}

			@Override
            public AuthDescriptor getAuthDescriptor() {
                return createAuthDescriptor();
            }

			private AuthDescriptor createAuthDescriptor() {				
				return null;
			}
        };
    }

	private HashAlgorithm getHashAlgorithm() {
		return DefaultHashAlgorithm.NATIVE_HASH;
	}

	private int getReadBufferSize() {
		return 16384;
	}

	private int getOperationQueueLength() {
		return 16384;
	}

}
