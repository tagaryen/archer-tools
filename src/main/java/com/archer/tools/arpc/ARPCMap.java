package com.archer.tools.arpc;

class ARPCMap {
	
	private byte[][]  nonces;
	private ARPCClientCallback<?>[] cbs;
	
	private Object lock = new Object();
	
	public ARPCMap() {
		nonces = new byte[1024][];
		cbs = new ARPCClientCallback[1024];
	}
	
	public void saveCallback(byte[] nonce, ARPCClientCallback<?> cb) {
		if(nonce.length != 16) {
			throw new ARPCException("Invalid nonce");
		}
		synchronized(lock) {
			for(int i = 0; i < cbs.length; i++) {
				if(cbs[i] == null) {
					nonces[i] = nonce;
					cbs[i] = cb;
					return ;
				}
			}
			int len = nonces.length;
			byte[][] tmpnonces = new byte[nonces.length * 2][];
			ARPCClientCallback<?>[] tmpcbs = new ARPCClientCallback[nonces.length * 2];
			System.arraycopy(nonces, 0, tmpnonces, 0, nonces.length);
			System.arraycopy(cbs, 0, tmpcbs, 0, nonces.length);
			nonces = tmpnonces;
			cbs = tmpcbs;
			nonces[len] = nonce;
			cbs[len] = cb;
		}
	}
	
	public ARPCClientCallback<?> findCallback(byte[] nonce) {
		if(nonce.length != 16) {
			throw new ARPCException("Invalid nonce");
		}
		for(int i = 0; i < cbs.length; i++) {
			boolean match = true;
			if(cbs[i] != null) {
				for(int j = 0; j < 16; j++) {
					if(nonces[i][j] != nonce[j]) {
						match = false;
						break;
					}
				}
			}
			if(cbs[i] != null && match) {
				synchronized(lock) {
					ARPCClientCallback<?> ret = cbs[i];
					nonces[i] = null;
					cbs[i] = null;
					return ret;
				}
			}
		}
		return null;
	}
}