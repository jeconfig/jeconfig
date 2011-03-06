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

package org.jeconfig.client.annotation.simple;

import java.util.Date;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.annotation.merging.MergingStrategies;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME})
public class SimplePropertyTestConfiguration {

	private String xmlEvilValue;
	private int evilValue;
	private int intValue = 1;
	private double doubleValue;
	private long longValue;
	private short shortValue;
	private byte byteValue;
	private String stringValue;
	private float floatValue;
	private boolean boolValue;
	private Integer bigIntValue;
	private Double bigDoubleValue;
	private Long bigLongValue;
	private Short bigShortValue;
	private Byte bigByteValue;
	private Float bigFloatValue;
	private Boolean bigBoolValue;
	private int someIntValue1;
	private int someIntValue2;
	private Integer someBigIntValue1;
	private Integer someBigIntValue2;
	private TestConfigEnum enumValue;
	private Date dateValue;

	public SimplePropertyTestConfiguration() {}

	@ConfigSimpleProperty
	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(final int intValue) {
		this.intValue = intValue;
	}

	@ConfigSimpleProperty
	public double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(final double doubleValue) {
		this.doubleValue = doubleValue;
	}

	@ConfigSimpleProperty
	public long getLongValue() {
		return longValue;
	}

	public void setLongValue(final long longValue) {
		this.longValue = longValue;
	}

	@ConfigSimpleProperty
	public short getShortValue() {
		return shortValue;
	}

	public void setShortValue(final short shortValue) {
		this.shortValue = shortValue;
	}

	@ConfigSimpleProperty
	public byte getByteValue() {
		return byteValue;
	}

	public void setByteValue(final byte byteValue) {
		this.byteValue = byteValue;
	}

	@ConfigSimpleProperty
	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(final String stringValue) {
		this.stringValue = stringValue;
	}

	@ConfigSimpleProperty
	public float getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(final float floatValue) {
		this.floatValue = floatValue;
	}

	@ConfigSimpleProperty
	public boolean isBoolValue() {
		return boolValue;
	}

	public void setBoolValue(final boolean boolValue) {
		this.boolValue = boolValue;
	}

	@ConfigSimpleProperty
	public Integer getBigIntValue() {
		return bigIntValue;
	}

	public void setBigIntValue(final Integer bigIntValue) {
		this.bigIntValue = bigIntValue;
	}

	@ConfigSimpleProperty
	public Double getBigDoubleValue() {
		return bigDoubleValue;
	}

	public void setBigDoubleValue(final Double bigDoubleValue) {
		this.bigDoubleValue = bigDoubleValue;
	}

	@ConfigSimpleProperty
	public Long getBigLongValue() {
		return bigLongValue;
	}

	public void setBigLongValue(final Long bigLongValue) {
		this.bigLongValue = bigLongValue;
	}

	@ConfigSimpleProperty
	public Short getBigShortValue() {
		return bigShortValue;
	}

	public void setBigShortValue(final Short bigShortValue) {
		this.bigShortValue = bigShortValue;
	}

	@ConfigSimpleProperty
	public Byte getBigByteValue() {
		return bigByteValue;
	}

	public void setBigByteValue(final Byte bigByteValue) {
		this.bigByteValue = bigByteValue;
	}

	@ConfigSimpleProperty
	public Float getBigFloatValue() {
		return bigFloatValue;
	}

	public void setBigFloatValue(final Float bigFloatValue) {
		this.bigFloatValue = bigFloatValue;
	}

	@ConfigSimpleProperty
	public Boolean getBigBoolValue() {
		return bigBoolValue;
	}

	public void setBigBoolValue(final Boolean bigBoolValue) {
		this.bigBoolValue = bigBoolValue;
	}

	@ConfigSimpleProperty(mergingStrategy = MergingStrategies.ParentOverwrites.class)
	public int getSomeIntValue1() {
		return someIntValue1;
	}

	public void setSomeIntValue1(final int someIntValue1) {
		this.someIntValue1 = someIntValue1;
	}

	@ConfigSimpleProperty(mergingStrategy = MyMergingStrategy.class)
	public int getSomeIntValue2() {
		return someIntValue2;
	}

	public void setSomeIntValue2(final int someIntValue2) {
		this.someIntValue2 = someIntValue2;
	}

	@ConfigSimpleProperty(mergingStrategy = MergingStrategies.ParentOverwrites.class)
	public Integer getSomeBigIntValue1() {
		return someBigIntValue1;
	}

	public void setSomeBigIntValue1(final Integer someBigIntValue1) {
		this.someBigIntValue1 = someBigIntValue1;
	}

	@ConfigSimpleProperty(mergingStrategy = MyMergingStrategy.class)
	public Integer getSomeBigIntValue2() {
		return someBigIntValue2;
	}

	public void setSomeBigIntValue2(final Integer someBigIntValue2) {
		this.someBigIntValue2 = someBigIntValue2;
	}

	@ConfigSimpleProperty
	public TestConfigEnum getEnumValue() {
		return enumValue;
	}

	public void setEnumValue(final TestConfigEnum enumValue) {
		this.enumValue = enumValue;
	}

	@ConfigSimpleProperty
	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(final Date dateValue) {
		this.dateValue = dateValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bigBoolValue == null) ? 0 : bigBoolValue.hashCode());
		result = prime * result + ((bigByteValue == null) ? 0 : bigByteValue.hashCode());
		result = prime * result + ((bigDoubleValue == null) ? 0 : bigDoubleValue.hashCode());
		result = prime * result + ((bigFloatValue == null) ? 0 : bigFloatValue.hashCode());
		result = prime * result + ((bigIntValue == null) ? 0 : bigIntValue.hashCode());
		result = prime * result + ((bigLongValue == null) ? 0 : bigLongValue.hashCode());
		result = prime * result + ((bigShortValue == null) ? 0 : bigShortValue.hashCode());
		result = prime * result + (boolValue ? 1231 : 1237);
		result = prime * result + byteValue;
		result = prime * result + ((dateValue == null) ? 0 : dateValue.hashCode());
		long temp;
		temp = Double.doubleToLongBits(doubleValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((enumValue == null) ? 0 : enumValue.hashCode());
		result = prime * result + Float.floatToIntBits(floatValue);
		result = prime * result + intValue;
		result = prime * result + (int) (longValue ^ (longValue >>> 32));
		result = prime * result + shortValue;
		result = prime * result + ((someBigIntValue1 == null) ? 0 : someBigIntValue1.hashCode());
		result = prime * result + ((someBigIntValue2 == null) ? 0 : someBigIntValue2.hashCode());
		result = prime * result + someIntValue1;
		result = prime * result + someIntValue2;
		result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
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
		if (!(obj instanceof SimplePropertyTestConfiguration)) {
			return false;
		}
		final SimplePropertyTestConfiguration other = (SimplePropertyTestConfiguration) obj;
		if (bigBoolValue == null) {
			if (other.bigBoolValue != null) {
				return false;
			}
		} else if (!bigBoolValue.equals(other.bigBoolValue)) {
			return false;
		}
		if (bigByteValue == null) {
			if (other.bigByteValue != null) {
				return false;
			}
		} else if (!bigByteValue.equals(other.bigByteValue)) {
			return false;
		}
		if (bigDoubleValue == null) {
			if (other.bigDoubleValue != null) {
				return false;
			}
		} else if (!bigDoubleValue.equals(other.bigDoubleValue)) {
			return false;
		}
		if (bigFloatValue == null) {
			if (other.bigFloatValue != null) {
				return false;
			}
		} else if (!bigFloatValue.equals(other.bigFloatValue)) {
			return false;
		}
		if (bigIntValue == null) {
			if (other.bigIntValue != null) {
				return false;
			}
		} else if (!bigIntValue.equals(other.bigIntValue)) {
			return false;
		}
		if (bigLongValue == null) {
			if (other.bigLongValue != null) {
				return false;
			}
		} else if (!bigLongValue.equals(other.bigLongValue)) {
			return false;
		}
		if (bigShortValue == null) {
			if (other.bigShortValue != null) {
				return false;
			}
		} else if (!bigShortValue.equals(other.bigShortValue)) {
			return false;
		}
		if (boolValue != other.boolValue) {
			return false;
		}
		if (byteValue != other.byteValue) {
			return false;
		}
		if (dateValue == null) {
			if (other.dateValue != null) {
				return false;
			}
		} else if (!dateValue.equals(other.dateValue)) {
			return false;
		}
		if (Double.doubleToLongBits(doubleValue) != Double.doubleToLongBits(other.doubleValue)) {
			return false;
		}
		if (enumValue == null) {
			if (other.enumValue != null) {
				return false;
			}
		} else if (!enumValue.equals(other.enumValue)) {
			return false;
		}
		if (Float.floatToIntBits(floatValue) != Float.floatToIntBits(other.floatValue)) {
			return false;
		}
		if (intValue != other.intValue) {
			return false;
		}
		if (longValue != other.longValue) {
			return false;
		}
		if (shortValue != other.shortValue) {
			return false;
		}
		if (someBigIntValue1 == null) {
			if (other.someBigIntValue1 != null) {
				return false;
			}
		} else if (!someBigIntValue1.equals(other.someBigIntValue1)) {
			return false;
		}
		if (someBigIntValue2 == null) {
			if (other.someBigIntValue2 != null) {
				return false;
			}
		} else if (!someBigIntValue2.equals(other.someBigIntValue2)) {
			return false;
		}
		if (someIntValue1 != other.someIntValue1) {
			return false;
		}
		if (someIntValue2 != other.someIntValue2) {
			return false;
		}
		if (stringValue == null) {
			if (other.stringValue != null) {
				return false;
			}
		} else if (!stringValue.equals(other.stringValue)) {
			return false;
		}
		return true;
	}

	public void set0EvilValue(final int evilValue) {
		this.evilValue = evilValue;
	}

	@ConfigSimpleProperty
	public int get0EvilValue() {
		return evilValue;
	}

	@ConfigSimpleProperty
	public String getXmlEvilValue() {
		return xmlEvilValue;
	}

	public void setXmlEvilValue(final String xmlEvilValue) {
		this.xmlEvilValue = xmlEvilValue;
	}

}
