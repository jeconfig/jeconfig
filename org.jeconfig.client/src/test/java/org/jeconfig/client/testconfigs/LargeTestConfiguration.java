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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.client.annotation.simple.TestConfigEnum;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME})
public class LargeTestConfiguration {
	private int a;
	private String b;
	private ComplexSubtype c;
	private IMyInterface d;
	private Set<Integer> e;
	private Set<ComplexSubtype> f;
	private Set<IMyInterface> g;
	private List<Integer> h;
	private List<ComplexSubtype> i;
	private List<IMyInterface> j;
	private Map<String, Integer> k;
	private Map<String, ComplexSubtype> l;
	private Map<String, IMyInterface> m;
	private int[] n;
	private ComplexSubtype[] o;
	private IMyInterface[] p;
	private TestConfigEnum q;
	private Date r;

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

	@ConfigComplexProperty
	public ComplexSubtype getC() {
		return c;
	}

	public void setC(final ComplexSubtype c) {
		this.c = c;
	}

	@ConfigComplexProperty(polymorph = true)
	public IMyInterface getD() {
		return d;
	}

	public void setD(final IMyInterface d) {
		this.d = d;
	}

	@ConfigSetProperty(itemType = Integer.class)
	public Set<Integer> getE() {
		return e;
	}

	public void setE(final Set<Integer> e) {
		this.e = e;
	}

	@ConfigSetProperty(itemType = ComplexSubtype.class)
	public Set<ComplexSubtype> getF() {
		return f;
	}

	public void setF(final Set<ComplexSubtype> f) {
		this.f = f;
	}

	@ConfigSetProperty(itemType = IMyInterface.class, polymorph = true)
	public Set<IMyInterface> getG() {
		return g;
	}

	public void setG(final Set<IMyInterface> g) {
		this.g = g;
	}

	@ConfigListProperty(itemType = Integer.class)
	public List<Integer> getH() {
		return h;
	}

	public void setH(final List<Integer> h) {
		this.h = h;
	}

	@ConfigListProperty(itemType = ComplexSubtype.class)
	public List<ComplexSubtype> getI() {
		return i;
	}

	public void setI(final List<ComplexSubtype> i) {
		this.i = i;
	}

	@ConfigListProperty(itemType = IMyInterface.class, polymorph = true)
	public List<IMyInterface> getJ() {
		return j;
	}

	public void setJ(final List<IMyInterface> j) {
		this.j = j;
	}

	@ConfigMapProperty(valueType = Integer.class)
	public Map<String, Integer> getK() {
		return k;
	}

	public void setK(final Map<String, Integer> k) {
		this.k = k;
	}

	@ConfigMapProperty(valueType = ComplexSubtype.class)
	public Map<String, ComplexSubtype> getL() {
		return l;
	}

	public void setL(final Map<String, ComplexSubtype> l) {
		this.l = l;
	}

	@ConfigMapProperty(valueType = IMyInterface.class, polymorph = true)
	public Map<String, IMyInterface> getM() {
		return m;
	}

	public void setM(final Map<String, IMyInterface> m) {
		this.m = m;
	}

	@ConfigArrayProperty
	public int[] getN() {
		return n;
	}

	public void setN(final int[] n) {
		this.n = n;
	}

	@ConfigArrayProperty
	public ComplexSubtype[] getO() {
		return o;
	}

	public void setO(final ComplexSubtype[] o) {
		this.o = o;
	}

	@ConfigArrayProperty(polymorph = true)
	public IMyInterface[] getP() {
		return p;
	}

	public void setP(final IMyInterface[] p) {
		this.p = p;
	}

	@ConfigSimpleProperty
	public TestConfigEnum getQ() {
		return q;
	}

	public void setQ(final TestConfigEnum q) {
		this.q = q;
	}

	@ConfigSimpleProperty
	public Date getR() {
		return r;
	}

	public void setR(final Date r) {
		this.r = r;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + a;
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		result = prime * result + ((d == null) ? 0 : d.hashCode());
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + ((f == null) ? 0 : f.hashCode());
		result = prime * result + ((g == null) ? 0 : g.hashCode());
		result = prime * result + ((h == null) ? 0 : h.hashCode());
		result = prime * result + ((i == null) ? 0 : i.hashCode());
		result = prime * result + ((j == null) ? 0 : j.hashCode());
		result = prime * result + ((k == null) ? 0 : k.hashCode());
		result = prime * result + ((l == null) ? 0 : l.hashCode());
		result = prime * result + ((m == null) ? 0 : m.hashCode());
		result = prime * result + Arrays.hashCode(n);
		result = prime * result + Arrays.hashCode(o);
		result = prime * result + Arrays.hashCode(p);
		result = prime * result + ((q == null) ? 0 : q.hashCode());
		result = prime * result + ((r == null) ? 0 : r.hashCode());
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
		if (!(obj instanceof LargeTestConfiguration)) {
			return false;
		}
		final LargeTestConfiguration other = (LargeTestConfiguration) obj;
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
		if (c == null) {
			if (other.c != null) {
				return false;
			}
		} else if (!c.equals(other.c)) {
			return false;
		}
		if (d == null) {
			if (other.d != null) {
				return false;
			}
		} else if (!d.equals(other.d)) {
			return false;
		}
		if (e == null) {
			if (other.e != null) {
				return false;
			}
		} else if (!e.equals(other.e)) {
			return false;
		}
		if (f == null) {
			if (other.f != null) {
				return false;
			}
		} else if (!f.equals(other.f)) {
			return false;
		}
		if (g == null) {
			if (other.g != null) {
				return false;
			}
		} else if (!g.equals(other.g)) {
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
		if (!Arrays.equals(n, other.n)) {
			return false;
		}
		if (!Arrays.equals(o, other.o)) {
			return false;
		}
		if (!Arrays.equals(p, other.p)) {
			return false;
		}
		if (q != other.q) {
			return false;
		}
		if (r == null) {
			if (other.r != null) {
				return false;
			}
		} else if (!r.equals(other.r)) {
			return false;
		}
		return true;
	}
}
