/**
 * 
 */
package com.daoben.rfid.service;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.daoben.rfid.mapper.AssetInLibraryMapper;
import com.daoben.rfid.mapper.AssetInfoMapper;
import com.daoben.rfid.model.AssetInLibrary;
import com.daoben.rfid.model.AssetInfo;

/**
 * @author 文
 *
 * 2017年2月14日下午1:09:30
 * 
 * 设备新增入库
 */

@Service
public class IntoTheLibraryService {
	@Resource
	private AssetInfoMapper assetInfoMapper;
	@Resource
	private AssetInLibraryMapper assetInLibraryMapper;
	
	public List<AssetInfo> findAll() {
		return assetInfoMapper.findAll();
	}
	public List<Map<String,Integer>> countAsset(){
		return assetInfoMapper.countAsset();
	}
	public List<Map<Object, Object>> findByAssetType(String assetType){
		return assetInfoMapper.findByAssetType(assetType);
	}
	public List<Map<Object, Object>> findByAssetType2(){
		return assetInfoMapper.findByAssetType2();
	}
	public Map<String, String> findByRfidLabelnum(String RfidLabelnum){
		return assetInfoMapper.findByRfidLabelnum(RfidLabelnum);
	}
	/**
	 * @param assetInLibrary
	 * @throws Exception 
	 */
	public int insertAsset(AssetInLibrary assetInLibrary) throws Exception {
		return assetInLibraryMapper.insertAsset(assetInLibrary);
	}
	/**
	 * @param string
	 * @return
	 */
	public List<AssetInLibrary> findByTagId(String tag_Id) {
		// TODO Auto-generated method stub
		return assetInLibraryMapper.findByTagId(tag_Id);
	}
	/**
	 * @param assetInLibrary
	 * @throws Exception 
	 */
	public int updateAsset(AssetInLibrary assetInLibrary) throws Exception {
		return assetInLibraryMapper.updateAsset(assetInLibrary);
		// TODO Auto-generated method stub
		
	}

	

	
	
	
}
