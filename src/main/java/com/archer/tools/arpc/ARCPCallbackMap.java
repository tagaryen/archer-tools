package com.archer.tools.arpc;

class ARCPCallbackMap {
	
	private static final int C = 127;
	
	int m;
	N[] map;
	
	public ARCPCallbackMap() {
		m = C;
		map = new N[m];
	}
	
	public void add(byte[] s, ARPCClientCallback<?> cb) {
		long p = 0;
		for(int i = 0; i < s.length; i++) {
			long l = (long) s[i];
			if(l < 0) {
				l += 256;
			}
			p |= (l << ((s.length - i) << 3));
		}
		if(p < 0) {
			p = -p;
		}
		int r = (int) (p % m);
		if(map[r] == null) {
			map[r] = new N(cb, s);
		} else {
			N n = new N(cb, s);
			N cur = map[r];
			while(cur.next != null) {
				cur = cur.next;
			}
			cur.next = n;
			n.last = cur;
		}
	}
	
	public ARPCClientCallback<?> get(byte[] bs) {
		long p = 0;
		for(int i = 0; i < bs.length; i++) {
			long l = (long) bs[i];
			if(l < 0) {
				l += 256;
			}
			p |= (l << ((bs.length - i) << 3));
		}
		if(p < 0) {
			p = -p;
		}
		int m = map.length;
		int r = (int) (p % m);
		N cur = map[r];
		int depth = 0;
		while(cur != null) {
			byte[] d = cur.seq;
			if(d.length == bs.length) {
				boolean ok = true;
				for(int i = 0; i < bs.length; i++) {
					if(d[i] != bs[i]) {
						ok = false;
						break; 
					}
				}
				if(ok) {
					if(depth == 0) {
						map[r] = cur.next;
						if(map[r] != null) {
							map[r].last = null;
						}
					} else {
						if(cur.last != null) {
							cur.last.next = cur.next;
						}
						if(cur.next != null) {
							cur.next.last = cur.last;
						}
					}
					return cur.cb;
				}
			}
			cur = cur.next;
			depth++;
		}
		return null;
	}
	
	class N {
		ARPCClientCallback<?> cb;
		byte[] seq;
		N next;
		N last;
		
		public N(ARPCClientCallback<?> cb, byte[] seq) {
			this.cb = cb;
			this.seq = seq;
		}
		
	}
}