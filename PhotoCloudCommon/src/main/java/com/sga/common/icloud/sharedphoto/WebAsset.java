package com.sga.common.icloud.sharedphoto;

import java.util.Map;

public class WebAsset {
	public Map<String, WebAssetLocation> locations;
	public Map<String, WebAssetItem> items; //the key to this map is the Derivative Checksum
}
