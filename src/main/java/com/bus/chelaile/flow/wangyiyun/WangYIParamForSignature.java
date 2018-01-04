package com.bus.chelaile.flow.wangyiyun;

public class WangYIParamForSignature implements Comparable<WangYIParamForSignature>{
		private String keyName;
		private String value;
		public WangYIParamForSignature(String keyName, String value) {
			super();
			this.keyName = keyName;
			this.value = value;
			if(this.keyName==null)
				throw new IllegalArgumentException();
		}
		public String getKeyName() {
			return keyName;
		}
		public String getValue() {
			return value;
		}
//		public void setKeyName(String keyName) {
//			this.keyName = keyName;
//		}
//		public void setValue(String value) {
//			this.value = value;
//		}
		@Override
		public int compareTo(WangYIParamForSignature o) {
			
			return this.getKeyName().compareTo(o.getKeyName());
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WangYIParamForSignature other = (WangYIParamForSignature) obj;
			if (keyName == null) {
				if (other.keyName != null)
					return false;
			} else if (!keyName.equals(other.keyName))
				return false;
			return true;
		}
		
		
}
