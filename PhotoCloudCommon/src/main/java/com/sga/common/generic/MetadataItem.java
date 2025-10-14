package com.sga.common.generic;

import java.util.Objects;

public class MetadataItem {

		public String directory;
		public String id;
		public String key;
		public String value;
		
		public MetadataItem()
		{
			id="";
			key="";
			value="";
			directory="";
		}
		
		public MetadataItem(String id, String directory, String key, String value)
		{
			this.id=id;
			this.key=key;
			this.value=value;
			this.directory=directory;
		}

		@Override
		public int hashCode() {
			return Objects.hash(directory, id, key);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MetadataItem other = (MetadataItem) obj;
			return Objects.equals(directory, other.directory) && Objects.equals(id, other.id)
					&& Objects.equals(key, other.key);
		}

		@Override
		public String toString() {
			return "MetadataItem [directory=" + directory + ", id=" + id + ", key=" + key + ", value=" + value + "]";
		}

		
		
		
}
