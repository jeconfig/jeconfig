/*
 * Copyright (c) 2011: Edmund Wagner, Wolfram Weidel
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the jeconfig nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jeconfig.client.testconfigs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME})
public class ProxyTestConfiguration {

	private int a;
	private String b;
	private double c;
	private short d;
	private long e;
	private byte f;
	private boolean g;
	private Integer h;
	private Double i;
	private Short j;
	private Byte k;
	private Long l;
	private Set<Integer> m;
	private List<Integer> n;
	private Map<String, Integer> o;
	private int[] p;

	public ProxyTestConfiguration() {
		a = 3;
		b = "test"; //$NON-NLS-1$
		c = 6;
		d = 78;
		e = 99;
		f = 22;
		g = true;
		h = Integer.valueOf(456);
		i = Double.valueOf(2345);
		j = Short.valueOf((short) 234234);
		k = Byte.valueOf((byte) 2345);
		l = Long.valueOf(234);
		m = new HashSet<Integer>();
		m.add(Integer.valueOf(4));
		n = new LinkedList<Integer>();
		n.add(Integer.valueOf(777));
		o = new HashMap<String, Integer>();
		o.put("testsdf", Integer.valueOf(2345)); //$NON-NLS-1$
		p = new int[] {345, 2342};
	}

	@ConfigSimpleProperty
	public int getA() {
		return a;
	}

	public void setA(final int a) {
		this.a = a;
	}

	@ConfigSimpleProperty
	public String getB() {
		return b;
	}

	public void setB(final String b) {
		this.b = b;
	}

	@ConfigSimpleProperty
	public double getC() {
		return c;
	}

	public void setC(final double c) {
		this.c = c;
	}

	@ConfigSimpleProperty
	public short getD() {
		return d;
	}

	public void setD(final short d) {
		this.d = d;
	}

	@ConfigSimpleProperty
	public long getE() {
		return e;
	}

	public void setE(final long e) {
		this.e = e;
	}

	@ConfigSimpleProperty
	public byte getF() {
		return f;
	}

	public void setF(final byte f) {
		this.f = f;
	}

	@ConfigSimpleProperty
	public boolean isG() {
		return g;
	}

	public void setG(final boolean g) {
		this.g = g;
	}

	@ConfigSimpleProperty
	public Integer getH() {
		return h;
	}

	public void setH(final Integer h) {
		this.h = h;
	}

	@ConfigSimpleProperty
	public Double getI() {
		return i;
	}

	public void setI(final Double i) {
		this.i = i;
	}

	@ConfigSimpleProperty
	public Short getJ() {
		return j;
	}

	public void setJ(final Short j) {
		this.j = j;
	}

	@ConfigSimpleProperty
	public Byte getK() {
		return k;
	}

	public void setK(final Byte k) {
		this.k = k;
	}

	@ConfigSimpleProperty
	public Long getL() {
		return l;
	}

	public void setL(final Long l) {
		this.l = l;
	}

	@ConfigSetProperty(itemType = Integer.class)
	public Set<Integer> getM() {
		return m;
	}

	public void setM(final Set<Integer> m) {
		this.m = m;
	}

	@ConfigListProperty(itemType = Integer.class)
	public List<Integer> getN() {
		return n;
	}

	public void setN(final List<Integer> n) {
		this.n = n;
	}

	@ConfigMapProperty(keyType = String.class, valueType = Integer.class)
	public Map<String, Integer> getO() {
		return o;
	}

	public void setO(final Map<String, Integer> o) {
		this.o = o;
	}

	@ConfigArrayProperty
	public int[] getP() {
		return p;
	}

	public void setP(final int[] p) {
		this.p = p;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + a;
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		long temp;
		temp = Double.doubleToLongBits(c);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + d;
		result = prime * result + (int) (e ^ (e >>> 32));
		result = prime * result + f;
		result = prime * result + (g ? 1231 : 1237);
		result = prime * result + ((h == null) ? 0 : h.hashCode());
		result = prime * result + ((i == null) ? 0 : i.hashCode());
		result = prime * result + ((j == null) ? 0 : j.hashCode());
		result = prime * result + ((k == null) ? 0 : k.hashCode());
		result = prime * result + ((l == null) ? 0 : l.hashCode());
		result = prime * result + ((m == null) ? 0 : m.hashCode());
		result = prime * result + ((n == null) ? 0 : n.hashCode());
		result = prime * result + ((o == null) ? 0 : o.hashCode());
		result = prime * result + Arrays.hashCode(p);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProxyTestConfiguration)) {
			return false;
		}
		final ProxyTestConfiguration other = (ProxyTestConfiguration) obj;
		if (a != other.a) {
			return false;
		}
		if (b == null) {
			if (other.b != null) {
				return false;
			}
		} else if (!b.equals(other.b)) {
			return false;
		}
		if (Double.doubleToLongBits(c) != Double.doubleToLongBits(other.c)) {
			return false;
		}
		if (d != other.d) {
			return false;
		}
		if (e != other.e) {
			return false;
		}
		if (f != other.f) {
			return false;
		}
		if (g != other.g) {
			return false;
		}
		if (h == null) {
			if (other.h != null) {
				return false;
			}
		} else if (!h.equals(other.h)) {
			return false;
		}
		if (i == null) {
			if (other.i != null) {
				return false;
			}
		} else if (!i.equals(other.i)) {
			return false;
		}
		if (j == null) {
			if (other.j != null) {
				return false;
			}
		} else if (!j.equals(other.j)) {
			return false;
		}
		if (k == null) {
			if (other.k != null) {
				return false;
			}
		} else if (!k.equals(other.k)) {
			return false;
		}
		if (l == null) {
			if (other.l != null) {
				return false;
			}
		} else if (!l.equals(other.l)) {
			return false;
		}
		if (m == null) {
			if (other.m != null) {
				return false;
			}
		} else if (!m.equals(other.m)) {
			return false;
		}
		if (n == null) {
			if (other.n != null) {
				return false;
			}
		} else if (!n.equals(other.n)) {
			return false;
		}
		if (o == null) {
			if (other.o != null) {
				return false;
			}
		} else if (!o.equals(other.o)) {
			return false;
		}
		if (!Arrays.equals(p, other.p)) {
			return false;
		}
		return true;
	}
}
